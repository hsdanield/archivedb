package br.com.archivedb.service;

import br.com.archivedb.model.InformationSchema;
import br.com.archivedb.model.Table;
import br.com.archivedb.repository.ArchivedbRepository;
import br.com.archivedb.utils.ArchiveDbUtil;
import br.com.archivedb.utils.SqlUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArchivedbService {

    @Autowired
    private ArchivedbRepository repository;

    @Autowired
    private FactoryService factoryService;

    public void setDataSource(String db) {
        this.repository.setDataSource(db);
    }

    public Page<Map<String, Object>> findByPage(String tableName, String schemaName, int page, int size, String orderBy) {
        PageRequest pageRequest = PageRequest.of(page, size);

        String sql = SqlUtil.sqlInformationSchema(tableName, schemaName);
        List<InformationSchema> informationSchemaList = this.repository.findListInformationSchema(sql);

        List<String> listColumnName = new ArrayList<>();

        for (InformationSchema obj : informationSchemaList) {
            listColumnName.add(obj.getColumnName());
        }

        sql = SqlUtil.sqlQueryMySqlPage(tableName, listColumnName, pageRequest.getPageSize(), pageRequest.getOffset(), orderBy);
        return this.repository.findByPage(sql, tableName, pageRequest);
    }

    public void createTable(Table table) {
        String sql = SqlUtil.sqlCreateTable(table);
        this.repository.createTable(sql);
    }

    public void insertCSV(MultipartFile file, String tableName, String schemaName, String delimiter, String db) {
        Character delimiterChar = ArchiveDbUtil.getDelimiter(delimiter); //verifica e retorna delimiter como character
        String sql;

        if (delimiterChar != null) {

            try {
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(delimiterChar).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                boolean verifyColumnsAndHeaders = true;

                List<String> nameColumns = new ArrayList<>();
                List<String> headers = csvParser.getHeaderNames();

                sql = SqlUtil.sqlInformationSchema(tableName, schemaName);

                List<InformationSchema> informationSchemaList = repository.findListInformationSchema(sql);
                this.setDataTypeCode(informationSchemaList, db);
                int[] types = new int[informationSchemaList.size()];

                for (int i = 0; i < informationSchemaList.size(); i++) {
                    nameColumns.add(informationSchemaList.get(i).getColumnName());
                    types[i] = informationSchemaList.get(i).getDataTypeCode();
                }

                if (nameColumns.size() == headers.size()) {
                    for (int i = 0; i < nameColumns.size(); i++) {
                        if (!nameColumns.get(i).equals(headers.get(i))) {
                            verifyColumnsAndHeaders = false;
                            break;
                        }
                    }
                } else {
                    verifyColumnsAndHeaders = false;
                }

                if (verifyColumnsAndHeaders) {
                    String questionMark = ArchiveDbUtil.questionMark(nameColumns);
                    sql = SqlUtil.sqlInsert(tableName, headers, questionMark);

                    List<CSVRecord> csvRecords = csvParser.getRecords().stream().toList();
                    List<Object[]> parameters = new ArrayList<Object[]>();

                    for (CSVRecord csvRecord : csvRecords) {
                        List<Object> auxParameters = new ArrayList<Object>();
                        for (InformationSchema obj : informationSchemaList) {
                            auxParameters.add(csvRecord.get(obj.getColumnName()));
                        }
                        parameters.add(ArchiveDbUtil.deleteBracket(auxParameters.toString()).split(","));
                    }

                    csvParser.close();
                    this.repository.save(sql, parameters, types);
                }
            } catch (IOException e) {
                throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
            }

        }
    }

    public ByteArrayInputStream exportCSV(
            String tableName, String schemaName, int page, int size, String orderBy, String delimiter) {

        PageRequest pageRequest = PageRequest.of(page, size);
        String[] headers = this.getNameColumns(tableName, schemaName).toArray(new String[0]);

        Character delimiterChar = ArchiveDbUtil.getDelimiter(delimiter); //verifica e retorna delimiter como character
        CSVFormat format = CSVFormat.DEFAULT.withQuote(null).withDelimiter(delimiterChar).withHeader(headers);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)), format);

            Page<Map<String, Object>> pageList = this.findByPage(tableName, schemaName, page, size, orderBy);
            for (Map<String, Object> map : pageList.getContent()) {

                List<String> data = map.values().stream()
                        .map(object -> Objects.toString(object, null))
                        .collect(Collectors.toList());

                csvPrinter.printRecord(data);
            }


            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }

    }

    public List<String> getNameColumns(String tableName, String schemaName) {

        String sql = SqlUtil.sqlInformationSchema(tableName, schemaName);

        List<String> nameColumns = new ArrayList<>();

        List<InformationSchema> informationSchemaList = this.repository.findListInformationSchema(sql);
        for (InformationSchema obj : informationSchemaList) {
            nameColumns.add(obj.getColumnName());
        }

        return nameColumns;
    }

    public List<Map<String, Object>> csvToJson(MultipartFile file, String delimiter) {
        Character delimiterChar = ArchiveDbUtil.getDelimiter(delimiter); //verifica e retorna delimiter como character

        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(delimiterChar).withFirstRecordAsHeader().withTrim());

            List<String> headers = csvParser.getHeaderNames();
            List<CSVRecord> csvRecords = csvParser.getRecords().stream().toList();

            List<Map<String, Object>> mapJson = new ArrayList<>();

            for (CSVRecord csvRecord : csvRecords) {
                Map<String, Object> map = new HashMap<>();
                for (String header : headers) {
                    map.put(header, csvRecord.get(header));
                }
                mapJson.add(map);
            }

            csvParser.close();
            return mapJson;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }


    public ByteArrayInputStream listJsonToCSV(String json, String delimiter) {

        Character delimiterChar = ArchiveDbUtil.getDelimiter(delimiter); //verifica e retorna delimiter como character
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, ByteArrayInputStream> csvFile = new HashMap<>();

        try {
            /*Transformando Json em List<Map<String, Object>>*/
            JacksonJsonParser jsonParser = new JacksonJsonParser();
            List<Map<String, Object>> listMapJson = new ArrayList<>();
            List<Object> listJson = jsonParser.parseList(json);
            ObjectMapper oMapper = new ObjectMapper(); // jackson's objectmapper
            for (Object lineJson : listJson) {
                /*Convertendo json e verificando referencia*/
                Map<String, Object> map = oMapper.convertValue(lineJson, new TypeReference<Map<String, Object>>() {});
                listMapJson.add(map);
            }

            /*setando header*/
            String[] header = listMapJson.get(0).keySet().stream().toList().toArray(new String[0]);

            /*gravando CSV*/
            CSVFormat format = CSVFormat.DEFAULT.withQuote(null).withDelimiter(delimiterChar).withHeader(header);
            try (CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)), format);) {
                for (Map<String, Object> line : listMapJson) {
                    List<Object> lineCSV = new ArrayList<>(line.values());
                    csvPrinter.printRecord(lineCSV);
                }
                csvPrinter.flush();
            } catch (IOException e) {
                throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
            }
        } catch (JsonParseException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    public void setDataTypeCode(List<InformationSchema> informationSchema, String db) {
        this.factoryService.setDataTypeCode(informationSchema, db);
    }

}

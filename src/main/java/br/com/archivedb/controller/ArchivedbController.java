package br.com.archivedb.controller;

import br.com.archivedb.model.Table;
import br.com.archivedb.service.ArchivedbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/archivedb/{db}")
public class ArchivedbController {

    @Autowired
    private ArchivedbService service;

    @GetMapping
    public ResponseEntity<Page<Map<String, Object>>> findByPage(@RequestParam("tableName") String tableName,
                                                                @RequestParam(value = "schemaName", required = false) String schemaName,
                                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                                @RequestParam(name = "size", defaultValue = "500") int size,
                                                                @RequestParam(name = "orderBy", required = false) String orderBy,
                                                                @PathVariable("db") String db) {
        this.service.setDataSource(db);
        return ResponseEntity.ok(service.findByPage(tableName, schemaName, page, size, orderBy));
    }

    @PostMapping("/create-table")
    public ResponseEntity<Void> createTable(@RequestBody Table table, @PathVariable("db") String db) {
        this.service.setDataSource(db);
        this.service.createTable(table);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/insert-csv")
    public ResponseEntity<Void> insertCSV(@RequestParam("file") MultipartFile file, @RequestParam("tableName") String tableName,
                                          @RequestParam(value = "schemaName", required = false) String schemaName, @RequestParam("delimtier") String delimiter,
                                          @PathVariable("db") String db) {
        this.service.setDataSource(db);
        this.service.insertCSV(file, tableName, schemaName, delimiter, db);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/export-csv")
    public ResponseEntity<Resource> exportCSV(@RequestParam("tableName") String tableName,
                                              @RequestParam(value = "schemaName", required = false) String schemaName,
                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                              @RequestParam(name = "size", defaultValue = "500") int size,
                                              @RequestParam(name = "orderBy", required = false) String orderBy,
                                              @RequestParam(name = "delimiter") String delimiter,
                                              @PathVariable("db") String db) {
        this.service.setDataSource(db);
        InputStreamResource file = new InputStreamResource(this.service.exportCSV(tableName, schemaName, page, size, orderBy, delimiter));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tableName + ".csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @PostMapping("/csv-to-json")
    public ResponseEntity<List<Map<String, Object>>> csvToJson(@RequestParam("file") MultipartFile file,
                                                               @RequestParam(name = "delimiter") String delimiter) {
        return ResponseEntity.ok(service.csvToJson(file, delimiter));
    }


    @PostMapping("/json-to-csv")
    public ResponseEntity<Resource> listJsonToCSV(@RequestBody String json,
                                                  @RequestParam(name = "delimiter") String delimiter) {

        InputStreamResource file = new InputStreamResource(this.service.listJsonToCSV(json, delimiter));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=archive.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }



}

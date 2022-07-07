package br.com.archivedb.service;

import br.com.archivedb.config.factory.DataTypeMysql;
import br.com.archivedb.config.factory.DataTypePostgresql;
import br.com.archivedb.model.InformationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;

@Service
public class FactoryService {

    @Autowired
    private DataTypePostgresql dataTypePostgresql;

    @Autowired
    private DataTypeMysql dataTypeMysql;

    public void setDataTypeCode(List<InformationSchema> informationSchemaList, String db) {
        if (this.dataTypePostgresql.getVarEndpoint().equals(db)) setDataTypeCodePostgresql(informationSchemaList);
        else if (this.dataTypeMysql.getVarEndpoint().equals(db)) setDataTypeCodeMysql(informationSchemaList);
    }

    public void setDataTypeCodePostgresql(List<InformationSchema> informationSchema) {

        for (InformationSchema info : informationSchema) {
            for (String type : this.dataTypePostgresql.getIntegerTypes()) {
                if (info.getDataType().equals(type)) {
                    info.setDataTypeCode(Types.INTEGER);
                    break;
                }
            }
            for (String type : this.dataTypePostgresql.getCharacterTypes()) {
                if (info.getDataType().equals(type)) {
                    info.setDataTypeCode(Types.VARCHAR);
                    break;
                }
            }
        }
    }

    public void setDataTypeCodeMysql(List<InformationSchema> informationSchema) {

        for (InformationSchema info : informationSchema) {
            for (String type : this.dataTypeMysql.getIntegerTypes()) {
                if (info.getDataType().equals(type)) {
                    info.setDataTypeCode(Types.INTEGER);
                    break;
                }
            }
            for (String type : this.dataTypeMysql.getCharacterTypes()) {
                if (info.getDataType().equals(type)) {
                    info.setDataTypeCode(Types.VARCHAR);
                    break;
                }
            }
        }
    }
}



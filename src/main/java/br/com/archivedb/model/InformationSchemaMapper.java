package br.com.archivedb.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InformationSchemaMapper implements RowMapper<InformationSchema> {

    public InformationSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
        InformationSchema informationSchema = new InformationSchema();
        informationSchema.setColumnName(rs.getString("column_name"));
        informationSchema.setOrdinalPosition(rs.getLong("ordinal_position"));
        informationSchema.setDataType(rs.getString("data_type"));
        return informationSchema;
    }

}

package br.com.archivedb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InformationSchemaParams {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String tableSchema;
    private String tableName;

}

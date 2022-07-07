package br.com.archivedb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Table {

    private List<TableAttribute> listField;
    private String sqlMoreCommandInTable;
    private String tableName;

}

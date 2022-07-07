package br.com.archivedb.utils;

import br.com.archivedb.model.Table;
import br.com.archivedb.model.TableAttribute;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class SqlUtil {

    public static String sqlInformationSchema(String tableName, String schemaName) {
        StringBuilder builder = new StringBuilder()
                .append(" select column_name, ordinal_position, data_type from information_schema.columns ")
                .append("where table_name = '")
                .append(tableName).append("' ");
        if(!ObjectUtils.isEmpty(schemaName)) {
            builder.append(" and table_schema = '")
                    .append(tableName).append("' ");
        }
        return builder.toString();
    }

    public static String sqlCreateTable(String tableName, String finalFields) {
        return new StringBuilder()
                .append("create table ")
                .append(tableName)
                .append(" ( ")
                .append(finalFields)
                .append(" );").toString();
    }

    public static String sqlCreateTable(Table table) {

        StringBuilder sql = new StringBuilder()
                .append("create table ")
                .append(table.getTableName())
                .append("( ");
        for (TableAttribute obj : table.getListField()) {
            sql.append(obj.getNameField())
                    .append(" ")
                    .append(obj.getTypeField())
                    .append(", ");
        }

        /*verificando se possui sqlBlock para executar*/
        if(!ObjectUtils.isEmpty(table.getSqlMoreCommandInTable())) {
            sql.append(table.getSqlMoreCommandInTable()).append(")");;
        } else {
            /*trocando ultima virgula por parenteses*/
            sql.setCharAt(sql.length()-2, ')');
        }

        return sql.toString();
    }

    public static String sqlInsert(String tableName, List<String> listColumnName, String questionMark) {
        return new StringBuilder()
                .append("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(ArchiveDbUtil.deleteBracket(listColumnName.toString()))
                .append(") VALUES ")
                .append(questionMark).toString();
    }

    public static String sqlQueryDefault(String tableName, List<String> listColumnName) {
        return new StringBuilder()
                .append("SELECT ")
                .append(ArchiveDbUtil.deleteBracket(listColumnName.toString()))
                .append(" FROM ")
                .append(tableName).toString();
    }

    public static String sqlQueryMySqlPage(String tableName, List<String> listColumnName, int size, Long offSet, String orderBy) {
        StringBuilder builder = new StringBuilder()
                .append("SELECT ")
                .append(ArchiveDbUtil.deleteBracket(listColumnName.toString()))
                .append("* FROM ")
                .append(tableName);
        if(!ObjectUtils.isEmpty(orderBy)) builder.append(" ORDER BY ").append(orderBy);
        builder.append(" LIMIT ")
                .append(size)
                .append(" OFFSET ")
                .append(offSet);
        return builder.toString();
    }

    public static String sqlCount(String tableName) {
        return "SELECT count(*) FROM " + tableName;
    }

}

package br.com.archivedb.config;

import br.com.archivedb.config.factory.ConnectionFactoryMysql;
import br.com.archivedb.config.factory.ConnectionFactoryPostgresql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceConfig {

    @Autowired
    private ConnectionFactoryMysql connectionFactoryMySql;

    @Autowired
    private ConnectionFactoryPostgresql connectionFactoryPostgresql;

    public void dataSourceMySql(JdbcTemplate jdbcTemplate) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(connectionFactoryMySql.getDriver_class_name());
        dataSource.setUrl(connectionFactoryMySql.getUrl());
        dataSource.setUsername(connectionFactoryMySql.getUsername());
        dataSource.setPassword(connectionFactoryMySql.getPassword());
//        if (!ObjectUtils.isEmpty(tableName)) dataSource.setSchema(tableSchema);
        jdbcTemplate.setDataSource(dataSource);
    }

    public void dataSourcePostgresql(JdbcTemplate jdbcTemplate) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(connectionFactoryPostgresql.getDriver_class_name());
        dataSource.setUrl(connectionFactoryPostgresql.getUrl());
        dataSource.setUsername(connectionFactoryPostgresql.getUsername());
        dataSource.setPassword(connectionFactoryPostgresql.getPassword());
//        if (!ObjectUtils.isEmpty(tableName)) dataSource.setSchema(tableSchema);
        jdbcTemplate.setDataSource(dataSource);
    }

    public void setDataSource(JdbcTemplate jdbcTemplate, String data) {
        if(data.equals("mysql")) {
            this.dataSourceMySql(jdbcTemplate);
        }
        if(data.equals("postgresql")){
            this.dataSourcePostgresql(jdbcTemplate);
        }
    }

}
package br.com.archivedb.repository;

import br.com.archivedb.config.DataSourceConfig;
import br.com.archivedb.model.InformationSchema;
import br.com.archivedb.model.InformationSchemaMapper;
import br.com.archivedb.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ArchivedbRepository implements ArchivedbRepositoryInt  {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSourceConfig dataSourceConfig;

   public void setDataSource(String data) {
        dataSourceConfig.setDataSource(jdbcTemplate, data);
    }

    @Override
    public Integer count(String tableName) {
        return jdbcTemplate.queryForObject(SqlUtil.sqlCount(tableName), Integer.class);
    }

    @Override
    public void save(String sql, List<Object[]> objects, int[] teste) {
        jdbcTemplate.batchUpdate(sql, objects, teste);
    }

    @Override
    public Page<Map<String, Object>> findByPage(String sql, String tableName, PageRequest pageRequest) {
        List<Map<String, Object>> listMap = jdbcTemplate.queryForList(sql);
        return new PageImpl<>(listMap, pageRequest, count(tableName));
    }

    @Override
    public List<InformationSchema>  findListInformationSchema(String sql) {
        return jdbcTemplate.query(sql, new InformationSchemaMapper());
    }

    @Override
    public void createTable(String sql) {
        jdbcTemplate.execute(sql);
    }

}

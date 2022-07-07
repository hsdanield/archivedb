package br.com.archivedb.repository;

import br.com.archivedb.model.InformationSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface ArchivedbRepositoryInt {

     Integer count(String tableName);

     void save(String sql, List<Object[]> objects, int[] types);

     Page<Map<String, Object>> findByPage(String sql, String tableName, PageRequest pageRequest);

     void createTable(String sql);

     List<InformationSchema> findListInformationSchema(String sql);

}

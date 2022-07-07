package br.com.archivedb.config.factory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource-postgresql", ignoreInvalidFields = true)
@Getter
@Setter
public class ConnectionFactoryPostgresql {

    private String url;
    private String username;
    private String password;
    private String driver_class_name;

}

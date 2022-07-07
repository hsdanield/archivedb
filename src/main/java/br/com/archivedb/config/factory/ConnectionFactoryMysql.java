package br.com.archivedb.config.factory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource-mysql", ignoreInvalidFields = true)
@Getter
@Setter
public class ConnectionFactoryMysql {

    private String url;
    private String username;
    private String password;
    private String driver_class_name;
    private String varEndpoint;
}

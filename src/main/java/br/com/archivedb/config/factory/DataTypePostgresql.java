package br.com.archivedb.config.factory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "data-type.postgresql", ignoreInvalidFields = true)
@Getter
@Setter
public class DataTypePostgresql {

    private List<String> integerTypes;
    private List<String> floatTypes;
    private List<String> characterTypes;
    private List<String> dateTypes;
    private String varEndpoint;

}

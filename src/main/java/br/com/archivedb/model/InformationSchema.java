package br.com.archivedb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InformationSchema {

    String columnName;
    Long ordinalPosition;
    String dataType;
    int dataTypeCode;

    @Override
    public String toString() {
        return "InformationSchema{" +
                "columnName='" + columnName + '\'' +
                ", ordinalPosition=" + ordinalPosition +
                ", dataType='" + dataType + '\'' +
                '}';
    }

}

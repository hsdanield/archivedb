package br.com.archivedb.utils;

import br.com.archivedb.model.InformationSchema;
import org.springframework.util.ObjectUtils;

import java.sql.Types;
import java.util.List;

public class ArchiveDbUtil {

    public static String deleteBracket(String str) {
        return str.replace("[", "").replace("]", "");
    }

    public static Character getDelimiter(String delimiter) {
        Character delimiterChar = null;
        if (delimiterIsValid(delimiter))
            delimiterChar = delimiter.trim().charAt(0);
        return delimiterChar;
    }

    private static boolean delimiterIsValid(String delimiter) {
        return (
                !ObjectUtils.isEmpty(delimiter) && (delimiter.trim().charAt(0) == ';' || delimiter.trim().charAt(0) == ',')
        );
    }

    public static String questionMark(List<String> fields) {
        StringBuilder questionMark = new StringBuilder();
        questionMark.append("(");
        questionMark.append("?,".repeat(fields.size()));
        questionMark.setCharAt(questionMark.length() - 1, ')');
        return questionMark.toString();
    }

}

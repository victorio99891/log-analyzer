package com.example.core_modules.reader.converter;

import com.example.core_modules.config.GlobalConfigurationHandler;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.log.LogStructure;
import com.example.core_modules.model.log.LogType;
import com.example.core_modules.reader.converter.exception.WrongLogStructureException;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LogStringToModelConverter {

    private LogStringToModelConverter() {

    }

    /**
     * Method responsible for converting well-known log model structure:
     * [LOG | LOG_TYPE | DATE_AND_TIME | ORIGIN | DETAILS]
     * OPTIONAL_MESSAGE
     * [END]
     * OPTIONAL_STACK_TRACE
     * <p>
     * No. of places for every section described by Log Structure:
     * <p>
     * 0 - LOG DELIMITER
     * 1 - LOG_TYPE
     * 2 - DATE_AND_TIME
     * 3 - ORIGIN
     * 4 - DETAILS
     * 5 - MESSAGE
     * 6 - END DELIMITER
     * 7 - STACK TRACE
     *
     * @param parsedString log representation by string
     * @return converted object which represents
     * @throws ParseException when DATE_AND_TIME cannot be parsed
     * @see com.example.core_modules.model.log.LogStructure
     */
    public static LogModel convert(String parsedString, String originalString, String fileName) throws ParseException {
        List<String> tokenList = tokenizeLog(parsedString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogModel.DATE_TIME_PATTERN);

        LogType logType = resolveLogType(getSection(tokenList, LogStructure.LOG_TYPE));
        DateTime dateTime = new DateTime(simpleDateFormat.parse(getSection(tokenList, LogStructure.DATE_AND_TIME)));

        String message = originalString;

        if (message != null && message.length() >= 32750) {
            message = message.substring(0, 32750);
        }

        String messageWithStackTrace = getSection(tokenList, LogStructure.MESSAGE).concat(getSection(tokenList, LogStructure.STACK_TRACE));

        return new LogModel(
                null,
                logType,
                dateTime,
                dateTime,
                fileName,
                tokenList.get(LogStructure.DETAILS.section()),
                message,
                messageWithStackTrace
        );
    }

    static List<String> tokenizeLog(String logString) {
        String delimiter = GlobalConfigurationHandler.getInstance().config().getLogDelimiterPattern();
        StringTokenizer tokenizer = new StringTokenizer(logString, delimiter);

        List<String> tokenList = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            tokenList.add(tokenizer.nextToken());
        }

        mockMessageOrStackTrace(tokenList);

        return tokenList;
    }

    static void mockMessageOrStackTrace(List<String> tokenList) {

        if (tokenList.get(LogStructure.MESSAGE.section()).equals(LogModel.LOG_END_DELIMITER)) {
            tokenList.add(LogStructure.MESSAGE.section(), "");
        }

        if (tokenList.get(LogStructure.END_DELIMITER.section()).equals(LogModel.LOG_END_DELIMITER) && tokenList.size() == 7) {
            tokenList.add("");
        }

    }

    static String getSection(List<String> tokenList, LogStructure structure) {
        String section = tokenList.get(structure.section());
        if (section != null && section.trim().isEmpty()) {
            section = section.trim();
        }

        if (null == section) {
            throw new WrongLogStructureException("Problem with parsing log structure. Exact cause: Missing section " + structure.name());
        }

        return section;
    }

    static LogType resolveLogType(String log) {
        for (LogType type : LogType.values()) {
            if (type.name().equalsIgnoreCase(log)) {
                return type;
            }
        }
        return null;
    }
}

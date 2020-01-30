package com.example.core_modules.model.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.Locale;


@Getter
@Setter
@NoArgsConstructor
public class LogModel {

    public static final String LOG_START_DELIMITER = "[LOG";
    public static final String LOG_END_DELIMITER = "[END]";
    public static final String DATE_TIME_PATTERN = "yyyy MMMM dd, HH:mm:ss (SSS)";

    private String hashId;
    private LogType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss.SSS", timezone = "UTC")
    private DateTime firstCallDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss.SSS", timezone = "UTC")
    private DateTime lastCallDate;
    private String origin;
    private String logFileDetails;
    private String message;
    private int occurrences;

    public LogModel(String hashId, LogType type, DateTime firstCallDate, DateTime lastCallDate, String origin, String logFileDetails, String message) {
        this.hashId = hashId;
        this.type = type;
        this.firstCallDate = firstCallDate;
        this.lastCallDate = lastCallDate;
        this.origin = origin;
        this.logFileDetails = logFileDetails;
        this.message = message;
        this.occurrences = 1;
    }
}

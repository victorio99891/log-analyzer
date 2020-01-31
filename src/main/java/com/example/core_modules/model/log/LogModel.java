package com.example.core_modules.model.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;


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
    private DateTime firstCallDateString;
    private DateTime firstCallDateTimeStamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss.SSS", timezone = "UTC")
    private DateTime lastCallDateString;
    private DateTime lastCallDateTimeStamp;
    private String origin;
    private String logFileDetails;
    private int occurrences;
    private String message;

    public LogModel(String hashId, LogType type, DateTime firstCallDateTimeStamp, DateTime lastCallDateTimeStamp, String origin, String logFileDetails, String message) {
        this.hashId = hashId;
        this.type = type;
        this.firstCallDateString = firstCallDateTimeStamp;
        this.firstCallDateTimeStamp = firstCallDateTimeStamp;
        this.lastCallDateString = lastCallDateTimeStamp;
        this.lastCallDateTimeStamp = lastCallDateTimeStamp;
        this.origin = origin;
        this.logFileDetails = logFileDetails;
        this.message = message;
        this.occurrences = 1;
    }

    public void setLastCallDateTimeStamp(DateTime lastCallDateTimeStamp) {
        this.lastCallDateString = lastCallDateTimeStamp;
        this.lastCallDateTimeStamp = lastCallDateTimeStamp;
    }
}

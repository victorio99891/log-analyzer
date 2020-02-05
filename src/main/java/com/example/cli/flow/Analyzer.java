package com.example.cli.flow;

import com.example.core_modules.explorer.DirectoryExplorer;
import com.example.core_modules.model.file.FileExtension;
import com.example.core_modules.model.file.FilePath;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.reader.LogFileReader;
import com.example.core_modules.reader.ZipFileReader;
import com.example.core_modules.reader.loader.HistoryLoader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Analyzer {

    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    private LogFileReader logFileReader = new LogFileReader();

    private ZipFileReader zipFileReader = new ZipFileReader();

    private HistoryLoader historyLoader = new HistoryLoader();

    public Map<String, LogModel> analyzeWithoutTimeSpecified(String path) {
        Map<String, LogModel> logModelMap = historyLoader.loadFromJSON();
        List<FilePath> paths = new DirectoryExplorer().exploreEndDir(new File(path));
        processLogsFromPaths(logModelMap, paths);
        historyLoader.generateHistoryJSON(logModelMap);
        return logModelMap;
    }

    public Map<String, LogModel> analyzeWithTimeSpecified(String path, String dateFrom, String dateTo) {
        try {
            DateTime dateTimeFrom = resolveDateFromString(dateFrom);
            DateTime dateTimeTo = resolveDateFromString(dateTo);

            boolean isValid = validateDateAndTime(dateTimeFrom, dateTimeTo);
            log.debug("Passed date/dates is/are valid: " + isValid);

            if (isValid) {
                Map<String, LogModel> logModelMap = historyLoader.loadFromJSON();
                List<FilePath> paths = new DirectoryExplorer().exploreEndDir(new File(path));

                log.info("Filter logs from history...");
                logModelMap = filterLogsByDateRange(logModelMap, dateTimeFrom, dateTimeTo);

                processLogsFromPaths(logModelMap, paths);

                logModelMap = filterLogsByDateRange(logModelMap, dateTimeFrom, dateTimeTo);

                historyLoader.generateHistoryJSON(logModelMap);

                return logModelMap;
            }
        } catch (ParseException e1) {
            log.error("Cannot parse date 'from' or 'to'.");
        } catch (Exception e2) {
            log.error("", e2);
        }

        return null;
    }

    private void processLogsFromPaths(Map<String, LogModel> logModelMap, List<FilePath> paths) {
        int allProcessedLogsCounter = 0;
        for (int i = 0; i < paths.size(); ++i) {
            log.info("----------------------------------");
            FilePath filePath = paths.get(i);

            log.info("[{}/{}] [{} FILE] Found ERROR or FATAL logs in file: {}",
                    i + 1, paths.size(), filePath.getExtension().name(), filePath.getFullPath());

            int singleFileLogCounter = readLogsFromPath(filePath, logModelMap);
            allProcessedLogsCounter += singleFileLogCounter;

            log.info("Successfully processed {} logs from file.", singleFileLogCounter);
        }
        log.info("----------------------------------");
        log.info("Sum of processed files: {}", paths.size());
        log.info("Sum of processed logs: {}", allProcessedLogsCounter);
        log.info("----------------------------------");
    }

    int readLogsFromPath(FilePath path, Map<String, LogModel> logModelMap) {
        int counter = 0;
        log.info("This process could take a while. Stay calm.");
        if (FileExtension.LOG.equals(path.getExtension())) {
            counter = logFileReader.read(path.getFullPath(), logModelMap);
        } else if (FileExtension.ZIP.equals(path.getExtension())) {
            counter = zipFileReader.read(path.getFullPath(), logModelMap);
        }
        return counter;
    }

    private Map<String, LogModel> mapFromList(List<LogModel> historyList) {
        Map<String, LogModel> map = new HashMap<>();
        for (LogModel logModel : historyList) {
            if (logModel.getHashId() == null) {
                throw new NullPointerException("HashId in log from history cannot be null!");
            }
            map.put(logModel.getHashId(), logModel);
        }
        return map;
    }

    private Map<String, LogModel> filterLogsByDateRange(Map<String, LogModel> logModelMap, DateTime dateFrom, DateTime dateTo) {
        List<LogModel> filteredLogs = new ArrayList<>();

        if (dateFrom != null || dateTo != null) {
            int filteredCounter = 0;
            log.info("Start filtering logs. Current collection size: " + logModelMap.size());
            for (LogModel logModel : logModelMap.values()) {
                if (dateFrom != null && dateTo == null) {
                    if (logModel.getFirstCallDateTimeStamp().isAfter(dateFrom)) {
                        filteredLogs.add(logModel);
                    } else {
                        ++filteredCounter;
                    }
                } else if (dateFrom == null && dateTo != null) {
                    if (logModel.getFirstCallDateTimeStamp().isBefore(dateTo)) {
                        filteredLogs.add(logModel);
                    } else {
                        ++filteredCounter;
                    }
                } else if (dateFrom != null && dateTo != null) {
                    if (logModel.getFirstCallDateTimeStamp().isAfter(dateFrom) &&
                            logModel.getLastCallDateTimeStamp().isBefore(dateTo)) {
                        filteredLogs.add(logModel);
                    } else {
                        ++filteredCounter;
                    }
                }
            }
            log.info("Filtered out " + filteredCounter + " which occurrences don't match to date range.");
        } else {
            log.info("Log filtering is omitted due to 'dateFrom' and 'dateTo' absence. ");
        }
        return mapFromList(filteredLogs);
    }

    boolean validateDateAndTime(DateTime dateTimeFrom, DateTime dateTimeTo) {

        boolean result = false;

        if (dateTimeFrom != null) {
            if (!dateTimeFrom.isAfterNow()) {
                result = true;
            } else {
                log.error("Date 'from' is after now.");
            }
        } else if (dateTimeTo != null) {
            if (!dateTimeTo.isAfterNow()) {
                result = true;
            } else {
                log.error("Date 'to' is after now.");
            }
        }

        if (dateTimeFrom != null && dateTimeTo != null) {
            if (dateTimeTo.isAfter(dateTimeFrom)) {
                result = true;
            } else {
                log.error("Date 'to' is not after date 'from'.");
            }
        }

        return result;
    }

    DateTime resolveDateFromString(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Analyzer.DATE_TIME_PATTERN);
        DateTime dateTime = null;
        if (dateString != null) {
            dateTime = new DateTime(simpleDateFormat.parse(dateString));
        }
        return dateTime;
    }
}

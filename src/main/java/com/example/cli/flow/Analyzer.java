package com.example.cli.flow;

import com.example.core_modules.explorer.DirectoryExplorer;
import com.example.core_modules.model.file.FileExtension;
import com.example.core_modules.model.file.FilePath;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.reader.LogFileReader;
import com.example.core_modules.reader.ZipFileReader;
import com.example.core_modules.reader.converter.HashTool;
import com.example.core_modules.reader.loader.HistoryLoader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class Analyzer {

    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";
    private LogFileReader logFileReader = new LogFileReader();

    private ZipFileReader zipFileReader = new ZipFileReader();

    private HistoryLoader historyLoader = new HistoryLoader();

    public Map<String, LogModel> analyzeWithoutTimeSpecified(String path) {
        Map<String, LogModel> logModelMap = historyLoader.loadFromJSON();
        List<FilePath> paths = new DirectoryExplorer().exploreEndDir(new File(path));

        // READ LOGS
        for (FilePath filePath : paths) {
            List<LogModel> readLogs = readLogsFromPath(filePath);

            // CALCULATE HASH
            calculateHash(logModelMap, readLogs);
        }

        // FILL-UP THE HISTORY
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

                // HISTORY
                log.info("Filter logs from history...");
                List<LogModel> historyList = filterLogsByDateRange(new ArrayList<>(logModelMap.values()), dateTimeFrom, dateTimeTo);
                logModelMap = mapFromList(historyList);

                // READ LOGS
                for (FilePath filePath : paths) {
                    List<LogModel> readLogs = readLogsFromPath(filePath);

                    // CURRENT
                    log.info("Filter current logs...");
                    readLogs = filterLogsByDateRange(readLogs, dateTimeFrom, dateTimeTo);

                    // CALCULATE HASH
                    calculateHash(logModelMap, readLogs);
                }


                // FILL-UP THE HISTORY
                historyLoader.generateHistoryJSON(logModelMap);

                return logModelMap;
            }

        } catch (ParseException e1) {
            log.error("Cannot parse date 'from' or 'to' typed in 'path -f' or 'path -t' or 'path -ft' command. " + e1.toString());
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return null;
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

    private List<LogModel> filterLogsByDateRange(List<LogModel> logModelMap, DateTime dateFrom, DateTime dateTo) {
        List<LogModel> filteredLogs = new ArrayList<>();

        if (dateFrom != null || dateTo != null) {
            int filteredCounter = 0;
            log.info("Start filtering logs. Current collection size: " + logModelMap.size());
            for (LogModel logModel : logModelMap) {
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
        return filteredLogs;
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

    //TODO: REFACTOR THIS CODE TO LOAD SINGLE FILE AND MAKE ALL OPERATIONS ON SINGLE FILE !!!!!!!!!!
    //SORT THIS FILE PATHS BY CREATION DATE
    List<LogModel> readLogsFromPath(FilePath path) {
        List<LogModel> readLogsList = new ArrayList<>();
        log.info("Start reading all entries from path...");
        if (FileExtension.LOG.equals(path.getExtension())) {
            final List<LogModel> modelList = logFileReader.read(path.getFullPath());
            readLogsList.addAll(modelList);
            log.info("[LOG FILE] Found " + modelList.size() + " ERROR or FATAL logs in file: " + path.getFullPath());
        } else if (FileExtension.ZIP.equals(path.getExtension())) {
            final List<LogModel> modelList = zipFileReader.read(path.getFullPath());
            readLogsList.addAll(modelList);
            log.info("[ZIP FILE] Found " + modelList.size() + " ERROR or FATAL logs in file: " + path.getFullPath());
        }

        // SORT BY DATE
        log.info("Sorting found logs by date in ascending order.");
        Collections.sort(readLogsList, new Comparator<LogModel>() {
            @Override
            public int compare(LogModel log1, LogModel log2) {
                return log1.getFirstCallDateTimeStamp().compareTo(log2.getFirstCallDateTimeStamp());
            }
        });
        return readLogsList;
    }

    void calculateHash(Map<String, LogModel> logModelMap, List<LogModel> readLogs) {
        for (LogModel model : readLogs) {
            HashTool.generateHash(logModelMap, model);
        }
    }
}

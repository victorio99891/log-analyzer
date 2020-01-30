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
        Set<FilePath> paths = new DirectoryExplorer().exploreEndDir(new File(path));

        // READ LOGS
        List<LogModel> readLogs = readLogsFromPaths(paths);


        // CALCULATE HASH
        calculateHash(logModelMap, readLogs);

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
                Set<FilePath> paths = new DirectoryExplorer().exploreEndDir(new File(path));

                /*paths = resolveFilesAtCorrectTimeIntervals(paths, dateTimeFrom, dateTimeTo);*/

                // READ LOGS
                List<LogModel> readLogs = readLogsFromPaths(paths);
                // HISTORY
                log.info("Filter logs from history...");
                List<LogModel> historyList = filterLogsByDateRange(new ArrayList<>(logModelMap.values()), dateTimeFrom, dateTimeTo);
                logModelMap = mapFromList(historyList);

                // CURRENT
                log.info("Filter current logs...");
                readLogs = filterLogsByDateRange(readLogs, dateTimeFrom, dateTimeTo);


                calculateHash(logModelMap, readLogs);

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
                    if (logModel.getFirstCallDate().isAfter(dateFrom)) {
                        filteredLogs.add(logModel);
                    } else {
                        ++filteredCounter;
                    }
                } else if (dateFrom == null && dateTo != null) {
                    if (logModel.getFirstCallDate().isBefore(dateTo)) {
                        filteredLogs.add(logModel);
                    } else {
                        ++filteredCounter;
                    }
                } else if (dateFrom != null && dateTo != null) {
                    if (logModel.getFirstCallDate().isAfter(dateFrom) &&
                            logModel.getLastCallDate().isBefore(dateTo)) {
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

    /*Set<FilePath> resolveFilesAtCorrectTimeIntervals(Set<FilePath> paths, DateTime dateFrom, DateTime dateTo) throws ParseException {
        Set<FilePath> filteredSet = new HashSet<>();
        for (FilePath path : paths) {
            if (FileExtension.ZIP.equals(path.getExtension())) {
                StringTokenizer tokenizer = new StringTokenizer(path.getName(), "_");
                String dateWithExtension = null;
                for (int i = 0; i <= 1; i++) {
                    tokenizer.nextToken();
                }
                dateWithExtension = tokenizer.nextToken();

                DateTime fileRolledDate = resolveDateFromString(dateWithExtension.substring(0, dateWithExtension.length() - 4));

                if (fileRolledDate != null) {
                    if (dateFrom != null && dateTo != null) {
                        if (fileRolledDate.isAfter(dateFrom) && fileRolledDate.isBefore(dateTo)) {
                            filteredSet.add(path);
                        }
                    } else if (dateFrom != null) {
                        if (fileRolledDate.isAfter(dateFrom)) {
                            filteredSet.add(path);
                        }
                    } else if (dateTo != null) {
                        if (fileRolledDate.isBefore(dateTo)) {
                            filteredSet.add(path);
                        }
                    }
                }
            } else if (FileExtension.LOG.equals(path.getExtension())) {
                if (dateFrom != null && dateTo != null) {
                    if (dateFrom.isBeforeNow() && dateTo.isBeforeNow()) {
                        filteredSet.add(path);
                    }
                } else if (dateFrom != null) {
                    filteredSet.add(path);
                } else if (dateTo != null) {
                    if (dateTo.isBeforeNow()) {
                        filteredSet.add(path);
                    }
                }
            }
        }

        return filteredSet;
    }*/

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

    List<LogModel> readLogsFromPaths(Set<FilePath> paths) {
        List<LogModel> readLogsList = new ArrayList<>();
        for (FilePath path : paths) {
            //TODO: Differentiate two responsibilities is necessary -> generating hash is something different that reading log list !!!
            if (FileExtension.LOG.equals(path.getExtension())) {
                final List<LogModel> modelList = logFileReader.read(path.getFullPath());
                readLogsList.addAll(modelList);
                log.info("[LOG FILE] Found " + modelList.size() + " ERROR or FATAL logs in file: " + path.getFullPath());
            } else if (FileExtension.ZIP.equals(path.getExtension())) {
                final List<LogModel> modelList = zipFileReader.read(path.getFullPath());
                readLogsList.addAll(modelList);
                log.info("[ZIP FILE] Found " + modelList.size() + " ERROR or FATAL logs in file: " + path.getFullPath());
            }
        }
        // SORT BY DATE
        Collections.sort(readLogsList, new Comparator<LogModel>() {
            @Override
            public int compare(LogModel log1, LogModel log2) {
                return log1.getFirstCallDate().compareTo(log2.getFirstCallDate());
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

package com.example.cli.flow;

import com.example.core_modules.explorer.DirectoryExplorer;
import com.example.core_modules.model.file.FileExtension;
import com.example.core_modules.model.file.FilePath;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.reader.LogFileReader;
import com.example.core_modules.reader.ZipFileReader;
import com.example.core_modules.reader.converter.HashTool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class Analyzer {

    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";
    private LogFileReader logFileReader = new LogFileReader();

    private ZipFileReader zipFileReader = new ZipFileReader();

    public static Map<String, LogModel> loadJsonHistoryFile() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        try {

            log.info("Trying to load JSON history file...");

            Map<String, LogModel> mapForJson = mapper.readValue(new File("LogHistory.json"), new TypeReference<HashMap<String, LogModel>>() {
            });

            log.info("History JSON successfully loaded. (Number of records: " + mapForJson.size() + ")");

            return mapForJson;

        } catch (IOException e) {
            log.info("History JSON not available.");
        }
        return new HashMap<>();
    }

    public Map<String, LogModel> analyzeWithoutTimeSpecified(String path) {
        Map<String, LogModel> logModelMap = loadLogsFromHistoryJSON();
        HashTool hashTool = new HashTool(logModelMap);
        Set<FilePath> paths = DirectoryExplorer.exploreEndDir(path);
        generateHistoryJSON(logModelMap, hashTool, paths);

        return logModelMap;
    }

    public Map<String, LogModel> analyzeWithTimeSpecified(String path, String dateFrom, String dateTo) {

        try {
            DateTime dateTimeFrom = resolveDateFromString(dateFrom);

            DateTime dateTimeTo = resolveDateFromString(dateTo);

            boolean isValid = validateDateAndTime(dateTimeFrom, dateTimeTo);
            log.debug("Passed date/dates is/are valid: " + isValid);

            if (isValid) {
                Map<String, LogModel> logModelMap = loadLogsFromHistoryJSON();

                HashTool hashTool = new HashTool(logModelMap);
                Set<FilePath> paths = DirectoryExplorer.exploreEndDir(path);

                paths = resolveFilesAtCorrectTimeIntervals(paths, dateTimeFrom, dateTimeTo);

                // FILL-UP THE HISTORY
                generateHistoryJSON(logModelMap, hashTool, paths);

                // FILTER BY DATES
                // logModelMap = filterHistoryLogsByDates(logModelMap, dateTimeFrom, dateTimeTo);

                return logModelMap;
            }

        } catch (ParseException e1) {
            log.error("Cannot parse date 'from' or 'to' typed in 'path -f' or 'path -t' or 'path -ft' command. " + e1.toString());
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return null;
    }

    private Map<String, LogModel> filterHistoryLogsByDates(Map<String, LogModel> logModelMap, DateTime dateFrom, DateTime dateTo) {
        Map<String, LogModel> filteredLogs = new HashMap<>();

        for (LogModel logModel : logModelMap.values()) {
            if (dateFrom != null && dateTo == null) {
                if (logModel.getFirstCallDate().isAfter(dateFrom)) {
                    filteredLogs.put(logModel.getHashId(), logModel);
                }
            } else if (dateFrom == null && dateTo != null) {
                if (logModel.getFirstCallDate().isBefore(dateTo)) {
                    filteredLogs.put(logModel.getHashId(), logModel);
                }
            } else if (dateFrom != null && dateTo != null) {
                if (logModel.getFirstCallDate().isAfter(dateFrom) &&
                        logModel.getLastCallDate().isBefore(dateTo)) {
                    filteredLogs.put(logModel.getHashId(), logModel);
                }
            }
        }

        return filteredLogs;
    }

    Set<FilePath> resolveFilesAtCorrectTimeIntervals(Set<FilePath> paths, DateTime dateFrom, DateTime dateTo) throws ParseException {
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
    }

    Map<String, LogModel> loadLogsFromHistoryJSON() {
        Map<String, LogModel> logModelMap;
        logModelMap = loadJsonHistoryFile();
        return logModelMap;
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
                result = false;
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

    void generateHistoryJSON(Map<String, LogModel> logModelMap, HashTool hashTool, Set<FilePath> paths) {
        for (FilePath path1 : paths) {
            List<LogModel> readLogsList = null;
            if (FileExtension.LOG.equals(path1.getExtension())) {
                readLogsList = logFileReader.read(path1.getFullPath());
                log.info("[LOG FILE] Found " + readLogsList.size() + " ERROR or FATAL logs in file: " + path1.getFullPath());
                for (LogModel model : readLogsList) {
                    hashTool.generateHash(model);
                }
            } else if (FileExtension.ZIP.equals(path1.getExtension())) {
                readLogsList = zipFileReader.read(path1.getFullPath());
                log.info("[ZIP FILE] Found " + readLogsList.size() + " ERROR or FATAL logs in file: " + path1.getFullPath());
                for (LogModel model : readLogsList) {
                    hashTool.generateHash(model);
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        log.info("Rewriting the LogHistory.json file...");
        try {
            mapper.writeValue(new File("LogHistory.json"), logModelMap);
            log.info("Newer version of LogHistory.json has been successfully saved!");
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}

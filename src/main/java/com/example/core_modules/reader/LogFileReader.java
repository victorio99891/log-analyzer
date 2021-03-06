package com.example.core_modules.reader;

import com.example.core_modules.config.GlobalConfigurationHandler;
import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.structure.Pair;
import com.example.core_modules.reader.converter.HashTool;
import com.example.core_modules.reader.converter.LogStringToModelConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;

@Slf4j
public final class LogFileReader extends FileReader {

    @Override
    public int read(String path, Map<String, LogModel> logModelMap) {
        int processedLogsCounter = 0;
        try {
            super.checkIfLogFileOnPath(path);

            File file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            processedLogsCounter = readAndHash(reader, path, logModelMap);

        } catch (UnsupportedFileFormatException e1) {
            log.error(e1.toString());
        } catch (IOException e2) {
            log.error("Cannot open and read the *.log.\n" + e2.toString());
        } catch (ParseException e3) {
            log.error("Cannot parse date in log." + e3.toString());
        }

        return processedLogsCounter;
    }

    public int readAndHash(BufferedReader reader, String path, Map<String, LogModel> logModelMap) throws IOException, ParseException {
        int counter = 0;
        String delimiter = GlobalConfigurationHandler.getInstance().config().getLogDelimiterPattern();
        String line;
        StringBuilder builder = null;
        String originalLog = null;
        while ((line = reader.readLine()) != null) {

            if (line.contains(LogModel.LOG_START_DELIMITER)) {
                if (builder != null) {
                    Pair<String, String> pair = new Pair<>(builder.toString(), originalLog);
                    analyseAndHash(path, pair, logModelMap);
                    counter++;
                }
                builder = new StringBuilder();
                originalLog = "";
                originalLog = originalLog.concat(line);
                // Replace all pipe characters in first line into delimiter defined in global config.
                line = line.replaceAll("\\|", delimiter);
                builder.append(line);
                builder.append(delimiter);
            }

            if (builder != null && !line.contains(LogModel.LOG_START_DELIMITER)) {

                if (line.equals(LogModel.LOG_END_DELIMITER)) {
                    builder.append(delimiter);
                    builder.append(line);
                } else {
                    builder.append(line);
                    builder.append("\r\n");
                }

                originalLog = originalLog.concat("\n").concat(line);

                if (line.contains(LogModel.LOG_END_DELIMITER)) {
                    builder.append(delimiter);
                }
            }
        }

        if (builder != null && !builder.toString().trim().isEmpty()) {
            Pair<String, String> pair = new Pair<>(builder.toString(), originalLog);
            analyseAndHash(path, pair, logModelMap);
            counter++;
        }

        return counter;
    }

    public void analyseAndHash(final String path, final Pair<String, String> log, Map<String, LogModel> logModelMap) throws ParseException {
        final LogModel logModel =
                LogStringToModelConverter.convert(log.getFirst(), log.getSecond(),
                        Paths.get(path).getFileName().toString());

        HashTool.generateHash(logModelMap, logModel);
    }
}

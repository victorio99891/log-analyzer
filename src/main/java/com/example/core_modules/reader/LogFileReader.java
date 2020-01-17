package com.example.core_modules.reader;

import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.structure.Pair;
import com.example.core_modules.reader.converter.LogStringToModelConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class LogFileReader extends FileReader {

    @Override
    public List<LogModel> read(String path) {

        List<LogModel> logModelList = new ArrayList<>();

        try {
            super.checkIfLogFileOnPath(path);

            File file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            List<Pair<String, String>> logsString = readByPath(reader);

            for (Pair<String, String> log : logsString) {
                logModelList.add(LogStringToModelConverter.convert(log.getFirst(), log.getSecond(), Paths.get(path).getFileName().toString()));
            }

        } catch (UnsupportedFileFormatException e1) {
            log.error(e1.toString());
        } catch (IOException e2) {
            log.error("Cannot open and read the *.log.\n" + e2.toString());
        } catch (ParseException e3) {
            log.error("Cannot parse date in log." + e3.toString());
        }

        return logModelList;
    }


    public List<Pair<String, String>> readByPath(BufferedReader reader) throws IOException {
        List<Pair<String, String>> logs = new ArrayList<>();
        String line;
        StringBuilder builder = null;
        String originalLog = null;
        while ((line = reader.readLine()) != null) {

            if (line.contains(LogModel.LOG_START_DELIMITER)) {
                if (builder != null) {
                    logs.add(new Pair(builder.toString(), originalLog));
                }
                builder = new StringBuilder();
                originalLog = "";
                builder.append(line);
                builder.append("|");
                originalLog = originalLog + line;
            }

            if (builder != null && !line.contains(LogModel.LOG_START_DELIMITER)) {

                if (line.equals(LogModel.LOG_END_DELIMITER)) {
                    builder.append("|");
                    builder.append(line);
                    originalLog = originalLog + "\n" + line;
                } else {
                    builder.append(line);
                    builder.append("\r\n");
                    originalLog = originalLog + "\n" + line;
                }
                if (line.contains(LogModel.LOG_END_DELIMITER)) {
                    builder.append("|");
                }
            }
        }

        if (builder != null && !builder.toString().trim().isEmpty()) {
            logs.add(new Pair(builder.toString(), originalLog));
        }

        return logs;
    }
}

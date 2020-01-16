package com.example.core_modules.reader;

import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.reader.converter.LogStringToModelConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public final class ZipFileReader extends FileReader {


    private LogFileReader logFileReader = new LogFileReader();

    @Override
    public List<LogModel> read(String path) {

        List<LogModel> logModelList = new ArrayList<>();

        try {
            ZipFile zipFile = new ZipFile(path);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                super.checkIfLogFileOnPath(zipEntry.getName());

                InputStream stream = zipFile.getInputStream(zipEntry);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                List<String> logsString = logFileReader.readByPath(reader);

                for (String log : logsString) {
                    logModelList.add(LogStringToModelConverter.convert(log));
                }
            }
        } catch (UnsupportedFileFormatException e1) {
            log.error(e1.toString());
        } catch (IOException e2) {
            log.error("Problem with file on path: " + path + " Details: " + e2.getMessage());
        } catch (ParseException e3) {
            log.error("Cannot parse date in log." + e3.toString());
        }


        return logModelList;
    }


}


package com.example.core_modules.reader;

import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.log.LogModel;

import java.util.Map;

abstract class FileReader {

    public abstract int read(String path, Map<String, LogModel> logModelMap);

    protected void checkIfLogFileOnPath(String path) throws UnsupportedFileFormatException {
        if (!path.endsWith(".log")) {
            throw new UnsupportedFileFormatException("FileReader can be only used to parse *.log files. Current file path: " + path);
        }
    }

}

package com.example.core_modules.reader;

import com.example.core_modules.model.log.LogModel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class ZipFileReaderTest {

    private ZipFileReader zipFileReader;

    @Before
    public void setUp() {
        zipFileReader = new ZipFileReader();
    }

    @Test
    public void read_passWrongFileName_throwFileNotFoundException() {
        String fakePath = "/var/log/file-log.log";

        zipFileReader.read(fakePath, new HashMap<String, LogModel>());
    }
}
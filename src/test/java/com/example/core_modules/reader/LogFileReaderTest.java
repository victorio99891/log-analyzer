package com.example.core_modules.reader;

import com.example.core_modules.model.log.LogModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;

public class LogFileReaderTest {


    private final String LOG_STRING = "[LOG|ERROR|2009 January 23, 07:46:26 (955)|Server|/var/Server_4.log]\n" +
            "The incoming message is referring to non-existing outgoing message. No such linked id  in a database!\n" +
            "[END]\n" +
            "[LOG|ERROR|2009 January 23, 07:46:26 (965)|Server|/var/Server_4.log]\n" +
            "The incoming message is referring to non-existing outgoing message. No such linked id  in a database!\n" +
            "[END]";
    private LogFileReader logFileReader;

    @Before
    public void setUp() {
        logFileReader = new LogFileReader();
    }

    @Test
    public void readByPath_readFromLogString_shouldParseTwoLogs() throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new StringReader(LOG_STRING));

        int counter = logFileReader.readAndHash(br, "fakePath", new HashMap<String, LogModel>());

        Assert.assertEquals(2, counter);
    }

    @Test
    public void read_passWrongFileName_throwFileNotFoundException() {
        String fakePath = "/var/log/file-log.log";

        final int counter = logFileReader.read(fakePath, new HashMap<String, LogModel>());

        Assert.assertEquals(0, counter);
    }

    @Test
    public void read_passWrongFileFormat_throwUnsupportedFileFormatException() {
        String fakePath = "/var/log/file-log.docx";

        final int counter = logFileReader.read(fakePath, new HashMap<String, LogModel>());

        Assert.assertEquals(0, counter);
    }


}
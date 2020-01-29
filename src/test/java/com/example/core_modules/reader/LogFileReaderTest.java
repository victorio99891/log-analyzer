package com.example.core_modules.reader;

import com.example.core_modules.model.structure.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class LogFileReaderTest {


    private LogFileReader logFileReader;

    private String logString = "[LOG|ERROR|2009 January 23, 07:46:26 (955)|Server|/var/Server_4.log]\n" +
            "The incoming message is referring to non-existing outgoing message. No such linked id  in a database!\n" +
            "[END]\n" +
            "[LOG|ERROR|2009 January 23, 07:46:26 (965)|Server|/var/Server_4.log]\n" +
            "The incoming message is referring to non-existing outgoing message. No such linked id  in a database!\n" +
            "[END]";

    @Before
    public void setUp() {
        logFileReader = new LogFileReader();
    }

    @Test
    public void readByPath_readFromLogString_shouldParseTwoLogs() throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(logString));

        List<Pair<String, String>> log = logFileReader.readByPath(br);

        Assert.assertEquals(2, log.size());
    }

    @Test
    public void read_passWrongFileName_throwFileNotFoundException() {
        String fakePath = "/var/log/file-log.log";

        logFileReader.read(fakePath);
    }

    @Test
    public void read_passWrongFileFormat_throwUnsupportedFileFormatException() {
        String fakePath = "/var/log/file-log.docx";

        logFileReader.read(fakePath);
    }


}
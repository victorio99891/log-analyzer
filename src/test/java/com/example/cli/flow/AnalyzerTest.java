package com.example.cli.flow;

import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.reader.loader.HistoryLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RunWith(PowerMockRunner.class)
public class AnalyzerTest {

    private static final String FAKE_LOG_FILE_NAME = "TEST_SysMonErrorTEST.log";
    private static final String FAKE_ZIP_FILE_NAME = "TEST_SysMonErrorTESTlog_20090126120001.zip";
    private static final String TEST_LOG_STRING = "[LOG|ERROR|2009 January 23, 07:46:26 (955)|Server|/var/Server_4.log]\n" +
            "The incoming message is referring to non-existing outgoing message. No such linked id  in a database!\n" +
            "[END]\n" +
            "[LOG|ERROR|2009 January 23, 07:46:26 (965)|Server|/var/Server_4.log]\n" +
            "The incoming message is referring to non-existing outgoing message. No such linked id  in a database!\n" +
            "[END]";
    @Rule
    private TemporaryFolder fakeLogDirectory = new TemporaryFolder();
    private Analyzer analyzer;
    private HistoryLoader historyLoader = Mockito.mock(HistoryLoader.class);

    public static void writeToZipFile(String path, ZipOutputStream zipStream)
            throws IOException {
        System.out.println("Writing file : '" + path + "' to zip file");
        File aFile = new File(path);
        FileInputStream fis = new FileInputStream(aFile);
        ZipEntry zipEntry = new ZipEntry(path);
        zipStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipStream.write(bytes, 0, length);
        }
        zipStream.closeEntry();
        fis.close();
    }

    @Before
    public void setUp() throws IOException {
        fakeLogDirectory.create();
        File file = fakeLogDirectory.newFile(FAKE_LOG_FILE_NAME);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(TEST_LOG_STRING);
        bw.close();
        File zip = fakeLogDirectory.newFile(FAKE_ZIP_FILE_NAME);
        FileOutputStream fos = new FileOutputStream(zip);
        ZipOutputStream zipOS = new ZipOutputStream(fos);

        writeToZipFile(fakeLogDirectory.getRoot().getAbsolutePath() + "/" + FAKE_LOG_FILE_NAME, zipOS);

        zipOS.close();
        fos.close();
    }

    @Before
    public void setUpAnalyzer() {
        analyzer = new Analyzer();
        Whitebox.setInternalState(analyzer, "historyLoader", this.historyLoader);
    }

    @Test
    public void analyzeWithoutTimeSpecified_shouldPass() {
        Map<String, LogModel> hashMap = new HashMap<>();
        Mockito.doNothing().when(historyLoader).generateHistoryJSON(hashMap);
        Mockito.when(historyLoader.loadFromJSON()).thenReturn(hashMap);

        Map<String, LogModel> map = analyzer.analyzeWithoutTimeSpecified(fakeLogDirectory.getRoot().getAbsolutePath());

        Assert.assertEquals(1, map.size());

        String hash = new ArrayList<>(map.values()).get(0).getHashId();
        Assert.assertEquals(4, map.get(hash).getOccurrences());
    }

    @Test
    public void analyzeWithTimeSpecified_providedTwoDates_shouldPass() {
        Map<String, LogModel> hashMap = new HashMap<>();
        Mockito.doNothing().when(historyLoader).generateHistoryJSON(hashMap);
        Mockito.when(historyLoader.loadFromJSON()).thenReturn(hashMap);

        Map<String, LogModel> map = analyzer.analyzeWithTimeSpecified(fakeLogDirectory.getRoot().getAbsolutePath(), "20090122000001", "20090125000001");

        Assert.assertEquals(1, map.size());

        String hash = new ArrayList<>(map.values()).get(0).getHashId();
        Assert.assertEquals(4, map.get(hash).getOccurrences());
    }

    @Test
    public void analyzeWithTimeSpecified_dateFromNull_shouldPass() {
        Map<String, LogModel> hashMap = new HashMap<>();
        Mockito.doNothing().when(historyLoader).generateHistoryJSON(hashMap);
        Mockito.when(historyLoader.loadFromJSON()).thenReturn(hashMap);

        Map<String, LogModel> map = analyzer.analyzeWithTimeSpecified(fakeLogDirectory.getRoot().getAbsolutePath(), null, "20090125000001");

        Assert.assertEquals(1, map.size());

        String hash = new ArrayList<>(map.values()).get(0).getHashId();
        Assert.assertEquals(4, map.get(hash).getOccurrences());
    }

    @Test
    public void analyzeWithTimeSpecified_dateToNull_shouldPass() {
        Map<String, LogModel> hashMap = new HashMap<>();
        Mockito.doNothing().when(historyLoader).generateHistoryJSON(hashMap);
        Mockito.when(historyLoader.loadFromJSON()).thenReturn(hashMap);

        Map<String, LogModel> map = analyzer.analyzeWithTimeSpecified(fakeLogDirectory.getRoot().getAbsolutePath(), "20090122000001", null);

        Assert.assertEquals(1, map.size());

        String hash = new ArrayList<>(map.values()).get(0).getHashId();
        Assert.assertEquals(4, map.get(hash).getOccurrences());
    }

}
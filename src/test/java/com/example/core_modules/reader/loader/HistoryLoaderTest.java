package com.example.core_modules.reader.loader;

import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.log.LogType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.DateTime;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HistoryLoaderTest {

    private static final String FAKE_HISTORY_FILE_NAME = "LogFakeHistory.json";
    private static File historyFile;
    private static Map<String, LogModel> fakeMap;
    private HistoryLoader historyLoader;

    @BeforeClass
    public static void setUp() throws IOException {
        historyFile = new File(FAKE_HISTORY_FILE_NAME);
        if (historyFile.exists()) {
            historyFile.delete();
        }
        LogModel fakeLog = new LogModel("fakeHash", LogType.ERROR, DateTime.now(), DateTime.now(), "fakeOrigin", "fakeDetails", "fakeMessage");
        fakeMap = new HashMap<>();
        fakeMap.put("fakeHash", fakeLog);
        if (!historyFile.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            mapper.writeValue(historyFile, fakeMap);
        }
    }

    @AfterClass
    public static void onFinish() {
        if (historyFile.exists()) {
            historyFile.delete();
        }
    }

    @Before
    public void setUpLoader() {
        this.historyLoader = new HistoryLoader();
    }

    @Test
    public void loadJsonHistoryFile_fakeFileProvided_shouldLoadDataFromFile() {
        Map<String, LogModel> map = historyLoader.loadJsonHistoryFile(FAKE_HISTORY_FILE_NAME);

        Assert.assertEquals(fakeMap.size(), map.size());
    }

    @Test
    public void loadJsonRegexHistoryFile_fakeFileProvided_shouldLoadDataFromFile() {
        Map<String, LogModel> map = historyLoader.loadJsonRegexHistoryFile(FAKE_HISTORY_FILE_NAME);

        Assert.assertEquals(fakeMap.size(), map.size());
    }

    @Test
    public void loadJsonHistoryFile_nonExistingFile_shouldCatchIOException() {
        Map<String, LogModel> map = historyLoader.loadJsonHistoryFile("notExistingFile.json");

        Assert.assertEquals(0, map.size());
    }

    @Test
    public void loadJsonRegexHistoryFile_nonExistingFile_shouldCatchIOException() {
        Map<String, LogModel> map = historyLoader.loadJsonHistoryFile("notExistingFile.json");

        Assert.assertEquals(0, map.size());
    }
}
package com.example.core_modules.writer;

import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.log.LogType;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private Set<LogModel> logModelSet;
    private LogModel logModel;

    @Before
    public void setUp() {
        logModelSet = new HashSet<>();
        logModel = new LogModel("fakeHash", LogType.ERROR, DateTime.now(), DateTime.now(), "fakeServer", "fakeDetails", "fakeMessage");
        logModelSet.add(logModel);

        reportGenerator = new ReportGenerator();
    }

    @Test
    public void reportShouldBeGenerated() {
        reportGenerator.generateReport(logModelSet, false);
    }

    @After
    public void after() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(ReportGenerator.DATE_TIME_PATTERN);
        String fileNameNow = "LogReport_" + formatter.print(DateTime.now()) + ".xlsx";
        String fileNameBefore = "LogReport_" + formatter.print(DateTime.now().minusMinutes(1)) + ".xlsx";

        File file = new File(fileNameNow);
        file.delete();

        file = new File(fileNameBefore);
        file.delete();
    }

}
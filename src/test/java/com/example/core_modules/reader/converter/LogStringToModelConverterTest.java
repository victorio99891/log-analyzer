package com.example.core_modules.reader.converter;

import com.example.core_modules.config.GlobalConfigurationHandler;
import com.example.core_modules.model.log.LogModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

public class LogStringToModelConverterTest {

    String parsedLog = "";
    String originalLog = "";

    @Before
    public void setUp() {
        String configDelimiter = GlobalConfigurationHandler.getInstance().config().getLogDelimiterPattern();
        this.parsedLog = buildMockedStringLog(configDelimiter);
        this.originalLog = buildMockedStringLog("|");
    }

    @Test
    public void convert() throws ParseException {

        LogModel logModel = LogStringToModelConverter.convert(this.parsedLog, this.parsedLog, "fakeFileName.log");

        Assert.assertNotNull(logModel);
        Assert.assertNull(logModel.getHashId());
        Assert.assertNotNull(logModel.getFullLogMessage());
        Assert.assertNotNull(logModel.getType());
        Assert.assertNotNull(logModel.getOrigin());
        Assert.assertNotNull(logModel.getFirstCallDateTimeStamp());
        Assert.assertNotNull(logModel.getLastCallDateTimeStamp());
        Assert.assertEquals(1,logModel.getOccurrences());
    }

    private String buildMockedStringLog(String delimiter) {
        String emptyString = "";
        return emptyString
                .concat("[LOG")
                .concat(delimiter)
                .concat("ERROR")
                .concat(delimiter)
                .concat("2020 January 15, 06:35:30 (584)")
                .concat(delimiter)
                .concat("TestServer")
                .concat(delimiter)
                .concat("/var/log/Server_141012.log]")
                .concat(delimiter)
                .concat("test message")
                .concat(delimiter)
                .concat("[END]")
                .concat(delimiter)
                .concat("com.tk.marketdata.MarketDataException: No Currency Default Set up for null\n" +
                        "\tat com.tk.util.CurrencyUtil.getFamilyCurrencyPair(CurrencyUtil.java:793)\n" +
                        "\tat com.tk.util.CurrencyUtil.getCcyFamily(CurrencyUtil.java:1095)");
    }
}
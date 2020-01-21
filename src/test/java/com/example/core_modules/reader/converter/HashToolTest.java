package com.example.core_modules.reader.converter;

import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.log.LogType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashToolTest {

    private HashTool hashTool;
    private LogModel logModelNow;
    private LogModel logModelLater;

    @Before
    public void setUp() {
        this.hashTool = new HashTool(null);
        this.logModelNow = new LogModel(null, LogType.ERROR, DateTime.now(), DateTime.now(), "fakeServer", "fakeDetails", "fakeMessage");
        this.logModelLater = new LogModel(null, LogType.ERROR, DateTime.now().plusDays(5), DateTime.now().plusDays(5), "fakeServer", "fakeDetails", "fakeMessage");
    }

    @Test
    public void generateHash_oneLogModel() {
        this.hashTool.generateHash(this.logModelNow, false);

        Assert.assertEquals(1, this.hashTool.hashedCollection.size());
    }

    @Test
    public void generateHash_twoRepeatedLogModels() {

        this.hashTool.generateHash(this.logModelNow, false);

        this.hashTool.generateHash(this.logModelLater, false);

        Assert.assertEquals(1, this.hashTool.hashedCollection.size());
        Assert.assertEquals(2, this.hashTool.hashedCollection.get(this.logModelLater.getHashId()).getOccurrences());
    }



}
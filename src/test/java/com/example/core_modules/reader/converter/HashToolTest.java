package com.example.core_modules.reader.converter;

import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.model.log.LogType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HashToolTest {

    private Map<String, LogModel> collection;
    private LogModel logModelNow;
    private LogModel logModelLater;

    @Before
    public void setUp() {
        collection = new HashMap<>();
        this.logModelNow = new LogModel(null, LogType.ERROR, DateTime.now(), DateTime.now(), "fakeServer", "fakeDetails", "fakeMessage");
        this.logModelLater = new LogModel(null, LogType.ERROR, DateTime.now().plusDays(5), DateTime.now().plusDays(5), "fakeServer", "fakeDetails", "fakeMessage");
    }

    @Test
    public void generateHash_oneLogModel() {
        HashTool.generateHash(collection, this.logModelNow);

        Assert.assertEquals(1, this.collection.size());
    }

    @Test
    public void generateHash_twoRepeatedLogModels() {

        HashTool.generateHash(this.collection, this.logModelNow);

        HashTool.generateHash(this.collection, this.logModelLater);

        Assert.assertEquals(1, this.collection.size());
        Assert.assertEquals(2, this.collection.get(this.logModelLater.getHashId()).getOccurrences());
    }


}
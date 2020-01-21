package com.example.core_modules.model.structure;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PairTest {

    private Pair<String, String> testModel;

    @Before
    public void setUp() {
        this.testModel = new Pair<>("FIRST", "SECOND");
    }

    @Test
    public void equals_theSamePairs_shouldReturnTrue() {
        Pair<String, String> anotherModel = new Pair<>("FIRST", "SECOND");

        Assert.assertEquals(testModel, anotherModel);
    }

    @Test
    public void hashCode_differentObjects_shouldReturnFalse() {
        Pair<String, String> anotherModel = new Pair<>("FIRST", "THIRD");

        Assert.assertNotEquals(testModel.hashCode(), anotherModel.hashCode());
    }

}
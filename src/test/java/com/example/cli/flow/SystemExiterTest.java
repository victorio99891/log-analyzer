package com.example.cli.flow;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SystemExiterTest {

    private SystemExiter exiter;

    @Before
    public void setUp() {
        exiter = SystemExiter.getInstance();
    }

    @Test
    public void getInstance_call_shouldNotReturnNull() {
        Assert.assertNotNull(exiter);
    }

    @Test(expected = SystemExiter.UnexpectedExitException.class)
    public void exitWithError() {
        exiter.exitWithError(new RuntimeException());
    }
}
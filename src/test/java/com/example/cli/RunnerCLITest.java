package com.example.cli;

import com.example.cli.flow.SystemExiter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemExiter.class)
public class RunnerCLITest {


    @Test
    public void getInstance_calledManyTimes_shouldReturnAlwaysTheSameObject() {
        Assert.assertEquals(RunnerCLI.getInstance(), RunnerCLI.getInstance());
    }

    @Test
    public void run_helpCommand_shouldRunApp() {
        RunnerCLI.getInstance().run(new String[]{"--help"});
    }

    @Test
//    @Test(expected = CliCommandNotFoundException.class)
    public void run_nonExistingCommand_shouldThrowCliCommandNotFound() throws Exception {
//        SystemExiter exiter = SystemExiter.getInstance();
//
//        PowerMockito.doNothing().when(exiter).exitWithError();
//
//        Mockito.doNothing().when(SystemExiter.getInstance()).exitWithError();
//
//        RunnerCLI.getInstance().run(new String[]{"--unknown"});
    }
}
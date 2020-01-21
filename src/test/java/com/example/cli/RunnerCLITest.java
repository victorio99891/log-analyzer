package com.example.cli;

import com.example.cli.exception.CliCommandArgumentException;
import com.example.cli.exception.CliCommandDoublePassedException;
import com.example.cli.exception.CliCommandNotFoundException;
import com.example.cli.exception.CliCommandWrongMethodCombinationException;
import com.example.cli.flow.SystemExiter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemExiter.class)
public class RunnerCLITest {

    @Test
    public void getInstance_calledManyTimes_shouldReturnAlwaysTheSameObject() {
        Assert.assertEquals(RunnerCLI.getInstance(), RunnerCLI.getInstance());
    }

    @Test
    public void run_helpCommand_shouldRunApp() {
        PowerMockito.mockStatic(RunnerCLI.class);

        RunnerCLI.getInstance().run(new String[]{"--help"});

        PowerMockito.verifyStatic();
    }

    @Test
    public void run_emptyArgumentList_shouldThrowCliCommandArgumentException() {
        try {
            RunnerCLI.getInstance().run(new String[]{});
        } catch (Throwable e) {
            Assert.assertThat(e.getCause(), instanceOf(CliCommandArgumentException.class));
        }
    }

    @Test
    public void run_dateToWithoutArgument_shouldThrowCliCommandArgException() {
        try {
            RunnerCLI.getInstance().run(new String[]{"--dateto"});
        } catch (Throwable e) {
            Assert.assertThat(e.getCause(), instanceOf(CliCommandArgumentException.class));
        }
    }

    @Test
    public void run_doublePassedSameArgument_shouldThrowCliCommandDoublePassedException() {
        try {
            RunnerCLI.getInstance().run(new String[]{"--help", "--help"});
        } catch (Throwable e) {
            Assert.assertThat(e.getCause(), instanceOf(CliCommandDoublePassedException.class));
        }
    }

    @Test
    public void run_wrongFlagsCombination_shouldThrowCliCommandWrongMethodCombinationException() {
        try {
            RunnerCLI.getInstance().run(new String[]{"--path", "/ver/path", "--help"});
        } catch (Throwable e) {
            Assert.assertThat(e.getCause(), instanceOf(CliCommandWrongMethodCombinationException.class));
        }
    }

    @Test
    public void run_noArgumentInFlagWhereRequired_shouldThrowCliCommandArgumentException() {
        try {
            RunnerCLI.getInstance().run(new String[]{"--path", "--dateto"});
        } catch (Throwable e) {
            Assert.assertThat(e.getCause(), instanceOf(CliCommandArgumentException.class));
        }
    }

    @Test
    public void run_nonExistingCommand_shouldThrowCliCommandNotFoundException() {
        try {
            RunnerCLI.getInstance().run(new String[]{"--unknown"});
        } catch (Throwable e) {
            Assert.assertThat(e.getCause(), instanceOf(CliCommandNotFoundException.class));
        }
    }
}
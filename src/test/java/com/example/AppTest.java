package com.example;

import com.example.cli.RunnerCLI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class AppTest {

    @Test
    @PrepareForTest({App.class})
    public void main_shouldStartAppAndPrintHelp_success() {
        PowerMockito.mockStatic(RunnerCLI.class);

        App.main(new String[]{"--help"});

        PowerMockito.verifyStatic();
    }

}

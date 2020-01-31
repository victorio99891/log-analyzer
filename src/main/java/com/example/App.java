package com.example;


import com.example.cli.RunnerCLI;
import com.example.cli.flow.SystemExiter;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Slf4j
public class App {
    public static void main(String[] args) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            RunnerCLI.getInstance().run(args);
        } catch (SystemExiter.UnexpectedExitException e) {
            log.error(e.getCause().getMessage());
            //TODO: Uncomment to print whole stacktrace when application fails
            // log.error("\n{}", ExceptionUtils.getStackTrace(e.getCause()));
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("\n{}", ExceptionUtils.getStackTrace(e.getCause()));
        }

        stopwatch.stop();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        log.info("Execution time: " + formatter.format(stopwatch.elapsed(TimeUnit.MILLISECONDS)));

    }
}

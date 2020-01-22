package com.example;


import com.example.cli.RunnerCLI;
import com.example.cli.flow.SystemExiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class App {
    public static void main(String[] args) {
        try {
            RunnerCLI.getInstance().run(args);
        } catch (SystemExiter.UnexpectedExitException e) {
            log.error(e.getCause().getMessage());
            //TODO: Uncomment to print whole stacktrace when application fails
            // log.error("\n{}", ExceptionUtils.getStackTrace(e.getCause()));
        } catch (Exception e) {
            log.error("\n{}", ExceptionUtils.getStackTrace(e.getCause()));
        }
    }
}

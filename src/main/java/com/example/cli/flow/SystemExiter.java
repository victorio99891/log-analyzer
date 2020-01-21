package com.example.cli.flow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemExiter {

    private static SystemExiter instance = null;

    private SystemExiter() {
    }

    public static SystemExiter getInstance() {
        if (instance == null) {
            instance = new SystemExiter();
        }
        return instance;
    }


    //TODO: Add global error handling when application would crash under control
    public void exitWithError(Throwable e) throws UnexpectedExitException {
        throw new UnexpectedExitException(e);
    }

    public static class UnexpectedExitException extends RuntimeException {
        public UnexpectedExitException(Throwable e) {
            super(e);
        }
    }
}

package com.example.cli.flow;

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


    public void exitWithError() {
        System.exit(-1);
    }


}

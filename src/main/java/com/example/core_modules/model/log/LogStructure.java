package com.example.core_modules.model.log;

public enum LogStructure {
    LOG_DELIMITER(0),
    LOG_TYPE(1),
    DATE_AND_TIME(2),
    ORIGIN(3),
    DETAILS(4),
    MESSAGE(5),
    END_DELIMITER(6);

    private int sectionNumber;

    LogStructure(int number) {
        this.sectionNumber = number;
    }

    public int section() {
        return sectionNumber;
    }
}

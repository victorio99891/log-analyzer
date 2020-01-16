package com.example.core_modules.writer.model;

public enum ReportColumn {

    LINE(0, "LINE", "A1"),
    STATUS(1, "STATUS", "B1"),
    WHO(2, "WHO", "C1"),
    JIRA(3, "JIRA", "D1"),
    COMMENT(4, "COMMENT", "E1"),
    // Generated hash from LogModel.
    EVENT_ID(5, "EVENT ID", "F1"),
    STAT(6, "STAT", "G1"),
    HOSTNAME(7, "HOSTNAME", "H1"),
    MESSAGE(8, "MESSAGE", "I1"),
    FIRST_OCCURRENCE_TIME(9, "FIRST OCCURRENCE TIME", "J1"),
    LAST_OCCURRENCE_TIME(10, "LAST OCCURRENCE TIME", "K1"),
    // Default value: Calypso BO
    APPLICATION(11, "APPLICATION", "L1"),
    COUNT(12, "COUNT", "M1"),
    OBJECT(13, "OBJECT", "N1");


    private int orderNumber;
    private String name;
    private String headerCellAddress;

    ReportColumn(int orderNumber, String name, String headerCellAddress) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.headerCellAddress = headerCellAddress;
    }

    public static ReportColumn getByOrderName(int orderNumberToFind) {
        ReportColumn rc = null;

        for (ReportColumn reportColumn : ReportColumn.values()) {
            if (reportColumn.getOrderNumber() == orderNumberToFind) {
                rc = reportColumn;
            }
        }

        if (rc == null) {
            throw new ArrayIndexOutOfBoundsException("ReportColumn cannot be null!");
        }

        return rc;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getName() {
        return name;
    }

    public String getHeaderCellAddress() {
        return headerCellAddress;
    }
}

package com.example.core_modules.writer;

import com.example.cli.flow.Analyzer;
import com.example.cli.flow.SystemExiter;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.writer.model.ReportColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ReportGenerator {

    private static final String DATE_TIME_PATTERN = "yyyyMMddHHmm";

    public void generateReportFromHistoryFile() {
        log.info("Report: Loading of history file for report needs...");
        final Set<LogModel> logModelSet = loadLogsFromHistoryFile();
        generateReport(logModelSet);
    }

    public void generateReport(Set<LogModel> logModelSet) {

        if (logModelSet.isEmpty()) {
            log.error("Report cannot be created because of empty log set. Check data validity.");
            SystemExiter.getInstance().exitWithError();
        }

        log.info("Report [1/10]: Creating new sheet...");
        Workbook workbook = new XSSFWorkbook();
        CreationHelper helper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("LOG REPORT");
        CellStyle headerCellStyle = getHeaderCellStyle(workbook);
        Row headerRow = sheet.createRow(0);

        log.info("Report [2/10]: Creating column headers...");
        for (int i = 0; i < ReportColumn.values().length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(ReportColumn.getByOrderName(i).getName());
            cell.setCellStyle(headerCellStyle);
        }
        sheet.createFreezePane(0, 1);

        log.info("Report [3/10]: Creating cell styles... ");
        CellStyle dateCellStyle = getCellDateCellStyle(workbook, helper);
        CellStyle alignmentCenter = getCellAlignmentStyle(workbook, HorizontalAlignment.CENTER);
        CellStyle alignmentLeft = getCellAlignmentStyle(workbook, HorizontalAlignment.LEFT);
        alignmentLeft.setWrapText(true);

        int rowNum = 1;

        log.info("Report [4/10]: Inserting data to the report...");
        for (LogModel model : logModelSet) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(60);
            buildReportRows(dateCellStyle, alignmentCenter, alignmentLeft, model, row);
        }

        log.info("Report [5/10]: Resizing columns... ");
        sheet.setColumnWidth(ReportColumn.STATUS.getOrderNumber(), 3750);
        sheet.setColumnWidth(ReportColumn.EVENT_ID.getOrderNumber(), 20000);
        sheet.setColumnWidth(ReportColumn.STAT.getOrderNumber(), 3750);
        sheet.setColumnWidth(ReportColumn.MESSAGE.getOrderNumber(), 30000);
        sheet.setColumnWidth(ReportColumn.FIRST_OCCURRENCE_TIME.getOrderNumber(), 7500);
        sheet.setColumnWidth(ReportColumn.LAST_OCCURRENCE_TIME.getOrderNumber(), 7500);
        sheet.setColumnWidth(ReportColumn.APPLICATION.getOrderNumber(), 4000);
        sheet.setColumnWidth(ReportColumn.ORIGIN_FILE.getOrderNumber(), 12000);

        log.info("Report [6/10]: Setting up auto filters...");
        sheet.setAutoFilter(
                CellRangeAddress.valueOf(
                        ReportColumn.LINE.getHeaderCellAddress() +
                                ":" +
                                ReportColumn.ORIGIN_FILE.getHeaderCellAddress())
        );


        log.info("Report [7/10]: Setting up possible values for statuses...");
        setStatusValidationOnColumn(ReportColumn.STATUS.getOrderNumber(), logModelSet.size(), sheet);
        setStatusValidationOnColumn(ReportColumn.STAT.getOrderNumber(), logModelSet.size(), sheet);


        log.info("Report [8/10]: Creating report 'xlsx' file...");
        FileOutputStream fileOut;
        try {
            log.info("Report [9/10]: Trying to save created file...");
            DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_TIME_PATTERN);
            fileOut = new FileOutputStream("LogReport_" + formatter.print(DateTime.now()) + ".xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            SystemExiter.getInstance().exitWithError();
        }
        log.info("Report [10/10]: Report has been successfully generated!");
    }

    private void setStatusValidationOnColumn(int column, int collectionSize, Sheet sheet) {
        XSSFDataValidationHelper h = new XSSFDataValidationHelper((XSSFSheet) sheet);
        DataValidationConstraint dvConstraint = h.createExplicitListConstraint(new String[]{"OPEN", "IN-PROGRESS", "CLOSED", "IGNORE"});
        CellRangeAddressList addressList1 = new CellRangeAddressList(1, collectionSize, column, column);
        DataValidation validation = h.createValidation(dvConstraint, addressList1);
        validation.setEmptyCellAllowed(false);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerCellStyle;
    }

    private void buildReportRows(CellStyle dateCellStyle, CellStyle alignmentCenter, CellStyle alignmentLeft, LogModel model, Row row) {
        final Cell lineCell = row.createCell(ReportColumn.LINE.getOrderNumber());
        lineCell.setCellStyle(alignmentCenter);

        final Cell statusCell = row.createCell(ReportColumn.STATUS.getOrderNumber());
        statusCell.setCellValue("OPEN");
        statusCell.setCellStyle(alignmentCenter);

        final Cell statCell = row.createCell(ReportColumn.STAT.getOrderNumber());
        statCell.setCellValue("OPEN");
        statCell.setCellStyle(alignmentCenter);

        final Cell whoCell = row.createCell(ReportColumn.WHO.getOrderNumber());
        whoCell.setCellStyle(alignmentCenter);

        final Cell jiraCell = row.createCell(ReportColumn.JIRA.getOrderNumber());
        jiraCell.setCellStyle(alignmentCenter);

        final Cell commentCell = row.createCell(ReportColumn.COMMENT.getOrderNumber());
        commentCell.setCellStyle(alignmentCenter);

        final Cell hostnameCell = row.createCell(ReportColumn.HOSTNAME.getOrderNumber());
        hostnameCell.setCellStyle(alignmentCenter);

        final Cell objectCell = row.createCell(ReportColumn.OBJECT.getOrderNumber());
        objectCell.setCellStyle(alignmentCenter);

        final Cell eventIdCell = row.createCell(ReportColumn.EVENT_ID.getOrderNumber());
        eventIdCell.setCellValue(model.getHashId());
        eventIdCell.setCellStyle(alignmentCenter);

        Cell firstOccurrenceDate = row.createCell(ReportColumn.FIRST_OCCURRENCE_TIME.getOrderNumber());
        firstOccurrenceDate.setCellStyle(dateCellStyle);
        firstOccurrenceDate.setCellValue(model.getFirstCallDate().toDate());

        Cell lastOccurrenceDate = row.createCell(ReportColumn.LAST_OCCURRENCE_TIME.getOrderNumber());
        lastOccurrenceDate.setCellStyle(dateCellStyle);
        lastOccurrenceDate.setCellValue(model.getLastCallDate().toDate());

        final Cell messageCell = row.createCell(ReportColumn.MESSAGE.getOrderNumber());
        messageCell.setCellStyle(alignmentLeft);
        messageCell.setCellValue(model.getMessage());

        final Cell applicationCell = row.createCell(ReportColumn.APPLICATION.getOrderNumber());
        applicationCell.setCellStyle(alignmentCenter);
        applicationCell.setCellValue("Calypso BO");

        final Cell counterCell = row.createCell(ReportColumn.COUNT.getOrderNumber());
        counterCell.setCellValue(model.getOccurrences());
        counterCell.setCellStyle(alignmentCenter);

        final Cell originCell = row.createCell(ReportColumn.ORIGIN_FILE.getOrderNumber());
        originCell.setCellValue(model.getOrigin());
        originCell.setCellStyle(alignmentCenter);
    }

    private CellStyle getCellAlignmentStyle(Workbook workbook, HorizontalAlignment center) {
        CellStyle alignment = workbook.createCellStyle();
        alignment.setAlignment(center);
        alignment.setVerticalAlignment(VerticalAlignment.TOP);
        return alignment;
    }

    private CellStyle getCellDateCellStyle(Workbook workbook, CreationHelper helper) {
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(helper.createDataFormat().getFormat("dd.MM.yy hh:mm:ss.000"));
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        return dateCellStyle;
    }

    private Set<LogModel> loadLogsFromHistoryFile() {
        return new HashSet<>(Analyzer.loadJsonHistoryFile().values());
    }


}

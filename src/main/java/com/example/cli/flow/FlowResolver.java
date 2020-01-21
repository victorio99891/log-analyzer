package com.example.cli.flow;

import com.example.cli.model.CliCommand;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.writer.ReportGenerator;

import java.util.HashSet;
import java.util.Map;

public class FlowResolver {

    Map<String, LogModel> logModelMap = null;
    private Analyzer analyzer = new Analyzer();
    private ReportGenerator reportGenerator = new ReportGenerator();

    public void resolve(Map<CliCommand, String> commandsToExecute) {
        if (commandsToExecute.containsKey(CliCommand.HELP)) {
            CliCommand.printHelp();
        }

        if (commandsToExecute.containsKey(CliCommand.DIRECTORY_PATH) &&
                !commandsToExecute.containsKey(CliCommand.DATE_FROM) &&
                !commandsToExecute.containsKey(CliCommand.DATE_TO)) {
            logModelMap = analyzer.analyzeWithoutTimeSpecified(
                    commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                    commandsToExecute.containsKey(CliCommand.REGEX_FILTER)
            );
            if (commandsToExecute.containsKey(CliCommand.REPORT)) {
                reportGenerator.generateReport(new HashSet<>(logModelMap.values()),
                        commandsToExecute.containsKey(CliCommand.REGEX_FILTER));
            }
        }

        if (commandsToExecute.containsKey(CliCommand.DIRECTORY_PATH) &&
                commandsToExecute.containsKey(CliCommand.DATE_FROM) &&
                !commandsToExecute.containsKey(CliCommand.DATE_TO)) {
            logModelMap = analyzer.analyzeWithTimeSpecified(
                    commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                    commandsToExecute.get(CliCommand.DATE_FROM),
                    null,
                    commandsToExecute.containsKey(CliCommand.REGEX_FILTER)
            );
            if (commandsToExecute.containsKey(CliCommand.REPORT)) {
                reportGenerator.generateReport(new HashSet<>(logModelMap.values()),
                        commandsToExecute.containsKey(CliCommand.REGEX_FILTER));
            }
        }

        if (commandsToExecute.containsKey(CliCommand.DIRECTORY_PATH) &&
                !commandsToExecute.containsKey(CliCommand.DATE_FROM) &&
                commandsToExecute.containsKey(CliCommand.DATE_TO)) {
            logModelMap = analyzer.analyzeWithTimeSpecified(
                    commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                    null,
                    commandsToExecute.get(CliCommand.DATE_TO),
                    commandsToExecute.containsKey(CliCommand.REGEX_FILTER)
            );
            if (commandsToExecute.containsKey(CliCommand.REPORT)) {
                reportGenerator.generateReport(new HashSet<>(logModelMap.values()),
                        commandsToExecute.containsKey(CliCommand.REGEX_FILTER));
            }
        }

        if (commandsToExecute.containsKey(CliCommand.DIRECTORY_PATH) &&
                commandsToExecute.containsKey(CliCommand.DATE_FROM) &&
                commandsToExecute.containsKey(CliCommand.DATE_TO)) {
            logModelMap = analyzer.analyzeWithTimeSpecified(
                    commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                    commandsToExecute.get(CliCommand.DATE_FROM),
                    commandsToExecute.get(CliCommand.DATE_TO),
                    commandsToExecute.containsKey(CliCommand.REGEX_FILTER)
            );
            if (commandsToExecute.containsKey(CliCommand.REPORT)) {
                reportGenerator.generateReport(new HashSet<>(logModelMap.values()),
                        commandsToExecute.containsKey(CliCommand.REGEX_FILTER));
            }
        }

        if (commandsToExecute.containsKey(CliCommand.REPORT) && !commandsToExecute.containsKey(CliCommand.DIRECTORY_PATH)) {
            reportGenerator.generateReportFromHistoryFile(commandsToExecute.containsKey(CliCommand.REGEX_FILTER));
        }
    }

}

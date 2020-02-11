package com.example.cli.model;

import com.example.cli.exception.CliCommandNotFoundException;
import com.example.cli.exception.CliCommandWrongMethodCombinationException;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public enum CliCommand {
    HELP("Help",
            "--help",
            false,
            "Shows available commands as also their description and briefly information about software version"),
    DIRECTORY_PATH("Directory Path",
            "--path",
            true,
            "Specify the path to the directory to scan."),
    DATE_FROM("Date From",
            "--datefrom",
            true,
            "Specify the date with time where log analysis should be STARTED. Format: yyyyMMddHHmmss Example: 20190125121544 for 25-01-2019 12:15:44."),
    DATE_TO("Date To",
            "--dateto",
            true,
            "Specify the date with time where log analysis should be FINISHED. Format: yyyyMMddHHmmss Example: 20190125121544 for 25-01-2019 12:15:44."),
    REPORT("Generate report",
            "--report",
            false,
            "Generates Excel report file.");
    private String name;
    private String flag;
    private boolean hasArgument;
    private String description;

    CliCommand(String name, String flag, boolean hasArgument, String description) {
        this.name = name;
        this.flag = flag;
        this.hasArgument = hasArgument;
        this.description = description;
    }


    public static CliCommand resolveCommandByFlag(String flag) throws CliCommandNotFoundException {
        CliCommand command = null;
        for (CliCommand cmd : CliCommand.values()) {
            if (cmd.getFlag().equals(flag)) {
                command = cmd;
            }
        }
        if (command == null) {
            throw new CliCommandNotFoundException("Flag: " + flag + " is incorrect. Try to run app with --help to see available commands.");
        }
        return command;
    }

    public static void validateCommandsCombination(Map<CliCommand, String> commandsToExecute) throws CliCommandWrongMethodCombinationException {
        if (commandsToExecute.containsKey(HELP) && commandsToExecute.containsKey(DIRECTORY_PATH)) {
            throw new CliCommandWrongMethodCombinationException("Program cannot be run with combination of '--path' and '--help' flags.");
        }

        if ((commandsToExecute.containsKey(DATE_FROM) || commandsToExecute.containsKey(DATE_TO)) &&
                !commandsToExecute.containsKey(DIRECTORY_PATH)) {
            throw new CliCommandWrongMethodCombinationException("Program cannot be run with combination of '--datefrom' or '--dateto' without '--path' flag.");
        }
    }

    public static void printHelp() {
        System.out.println("\nAvailable commands:\n");
        for (CliCommand cmd : CliCommand.values()) {
            if (!cmd.isHasArgument()) {
                System.out.printf("%-20s %-30s  %-10s\n", cmd.getName(), cmd.getFlag(), cmd.getDescription());
            } else {
                System.out.printf("%-20s %-30s  %-10s\n", cmd.getName(), cmd.getFlag() + " [argument]", cmd.getDescription());
            }
        }
        System.out.println("\n");
    }

}
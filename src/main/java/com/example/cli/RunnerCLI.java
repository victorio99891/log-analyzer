package com.example.cli;

import com.example.cli.exception.CliCommandArgumentException;
import com.example.cli.exception.CliCommandDoublePassedException;
import com.example.cli.exception.CliCommandNotFoundException;
import com.example.cli.exception.CliCommandWrongMethodCombinationException;
import com.example.cli.flow.FlowResolver;
import com.example.cli.flow.SystemExiter;
import com.example.cli.model.CliCommand;
import com.example.core_modules.config.GlobalConfigurationHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class RunnerCLI {

    private static RunnerCLI instance = null;
    private static Set<CliCommand> availableCommands = null;
    private FlowResolver flowResolver = new FlowResolver();

    private RunnerCLI() {
    }

    public static RunnerCLI getInstance() {
        if (instance == null) {
            instance = new RunnerCLI();
            availableCommands = initializeAvailableCommands();
        }
        return instance;
    }

    private static Set<CliCommand> initializeAvailableCommands() {
        availableCommands = new HashSet<>();
        availableCommands.addAll(Arrays.asList(CliCommand.values()));
        return availableCommands;
    }


    public void run(String[] args) {
        log.info("===================================================");
        log.info("               Log Analyzer v 0.1                  ");
        log.info("===================================================");

        if (args.length == 0) {
            SystemExiter.getInstance()
                    .exitWithError(
                            new CliCommandArgumentException("Program cannot be run with empty arguments list. See '--help' flag for more information"));
        }

        Map<CliCommand, String> commandsToExecute = resolveCommandsFromUserInput(args);
        validatePassedCommands(commandsToExecute);
        GlobalConfigurationHandler.init();
        flowResolver.resolve(commandsToExecute);
    }

    private void validatePassedCommands(Map<CliCommand, String> commandsToExecute) {
        try {
            CliCommand.validateCommandsCombination(commandsToExecute);
        } catch (CliCommandWrongMethodCombinationException e) {
            SystemExiter.getInstance().exitWithError(e);
        }
    }


    private Map<CliCommand, String> resolveCommandsFromUserInput(String[] args) {
        Map<CliCommand, String> commands = new EnumMap<>(CliCommand.class);
        int i = 0;
        for (; i < args.length; i++) {

            try {
                CliCommand passed = CliCommand.resolveCommandByFlag(args[i]);
                if (commands.containsKey(passed)) {
                    throw new CliCommandDoublePassedException("Cannot pass the same command two times!");
                }
                if (passed.isHasArgument()) {
                    ++i;
                    validateArgument(args, i);
                    commands.put(passed, args[i]);

                } else {
                    commands.put(passed, null);
                }
            } catch (CliCommandNotFoundException | CliCommandDoublePassedException | CliCommandArgumentException e) {
                SystemExiter.getInstance().exitWithError(e);
            }
        }
        return commands;
    }

    private void validateArgument(String[] args, int i) throws CliCommandArgumentException {
        String argument = null;
        try {
            argument = args[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CliCommandArgumentException("Missing argument for command which need it.");
        }

        if (argument.startsWith("--")) {
            throw new CliCommandArgumentException("Wrong argument for passed command.");
        }

    }


}

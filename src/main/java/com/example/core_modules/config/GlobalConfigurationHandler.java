package com.example.core_modules.config;

import com.example.cli.flow.SystemExiter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;


@Slf4j
public class GlobalConfigurationHandler {

    private static GlobalConfigurationHandler instance = null;
    private static ConfigurationModel config = null;

    private GlobalConfigurationHandler() {
    }

    public static GlobalConfigurationHandler getInstance() {
        if (instance == null) {
            log.info("Trying to initialize settings...");
            ObjectMapper mapper = new ObjectMapper();
            File configFile = new File("config/GlobalConfiguration.json");
            try {
                config = mapper.readValue(configFile, ConfigurationModel.class);
                validateConfiguration(config);
            } catch (IOException e) {
                log.error("Global configuration file cannot be loaded.", e);
                SystemExiter.getInstance().exitWithError(e);
            } catch (GlobalConfigurationNotValidException e) {
                SystemExiter.getInstance().exitWithError(e);
            }
            instance = new GlobalConfigurationHandler();
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    private static void validateConfiguration(ConfigurationModel model) throws GlobalConfigurationNotValidException {

        if (model.getLogDelimiterPattern() == null || model.getLogDelimiterPattern().trim().isEmpty()) {
            throw new GlobalConfigurationNotValidException("Setting: 'logFileDelimiterPattern' - cannot be null or empty.");
        }

        if (model.getRegexFilteredHistoryName() == null ||
                !model.getRegexFilteredHistoryName().matches("([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.json)$") ||
                model.getRegexFilteredHistoryName().trim().isEmpty()) {
            throw new GlobalConfigurationNotValidException("Setting: 'regexFilteredHistoryName' - cannot be null or empty. Must has the *.json extension.");
        }

        if (model.getUnfilteredHistoryName() == null ||
                !model.getUnfilteredHistoryName().matches("([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.json)$") ||
                model.getUnfilteredHistoryName().trim().isEmpty()) {
            throw new GlobalConfigurationNotValidException("Setting: 'unfilteredHistoryName' - cannot be null or empty. Must has the *.json extension.");
        }

        log.info("Settings are successfully initialized.");
    }

    public ConfigurationModel config() {
        return config;
    }

    private static class GlobalConfigurationNotValidException extends Exception {
        public GlobalConfigurationNotValidException(String message) {
            super(message);
        }
    }
}

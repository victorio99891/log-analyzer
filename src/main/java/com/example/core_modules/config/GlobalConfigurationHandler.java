package com.example.core_modules.config;

import com.example.cli.flow.SystemExiter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class GlobalConfigurationHandler {

    private static final String GLOBAL_SETTINGS_PATH = "GlobalConfiguration.json";
    private static GlobalConfigurationHandler instance = null;
    private ConfigurationModel config = null;

    private GlobalConfigurationHandler() {
    }

    public static GlobalConfigurationHandler getInstance() {
        if (instance == null) {
            log.info("Trying to initialize settings...");
            GlobalConfigurationHandler handler = new GlobalConfigurationHandler();
            ObjectMapper mapper = new ObjectMapper();
            try {
                InputStream configFile = handler.loadFileFromResources();
                handler.config = mapper.readValue(configFile, ConfigurationModel.class);
                handler.validateConfiguration(handler.config);
            } catch (IOException e) {
                log.error("{\n" +
                        "  \"logDelimiterPattern\": \"*#-!-#*\",\n" +
                        "  \"regexFilterList\": [\n" +
                        "    \"@[\\\\D\\\\d]{8}\"\n" +
                        "  ],\n" +
                        "  \"historyFileName\" : \"LogHistory.json\"\n" +
                        "}", e);
                SystemExiter.getInstance().exitWithError(e);
            } catch (GlobalConfigurationNotValidException e) {
                SystemExiter.getInstance().exitWithError(e);
            }
            instance = handler;
        }
        return instance;
    }

    public static void init() {
        getInstance();
    }

    private void validateConfiguration(ConfigurationModel model) throws GlobalConfigurationNotValidException {

        if (model.getLogDelimiterPattern() == null || model.getLogDelimiterPattern().trim().isEmpty()) {
            throw new GlobalConfigurationNotValidException("Setting: 'logFileDelimiterPattern' - cannot be null or empty.");
        }

        if (model.getHistoryFileName() == null ||
                !model.getHistoryFileName().matches("([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.json)$") ||
                model.getHistoryFileName().trim().isEmpty()) {
            throw new GlobalConfigurationNotValidException("Setting: 'unfilteredHistoryName' - cannot be null or empty. Must has the *.json extension.");
        }

        log.info("Settings are successfully initialized.");
    }

    private InputStream loadFileFromResources() {
        return getClass().getResourceAsStream("/" + GLOBAL_SETTINGS_PATH);
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

package com.example.core_modules.reader.loader;

import com.example.core_modules.config.GlobalConfigurationHandler;
import com.example.core_modules.model.log.LogModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HistoryLoader {
    public Map<String, LogModel> loadJsonHistoryFile(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        try {

            log.info("Trying to load JSON history file...");

            Map<String, LogModel> mapForJson = mapper.readValue(
                    new File(fileName),
                    new TypeReference<HashMap<String, LogModel>>() {
                    });

            log.info("History JSON successfully loaded. (Number of records: " + mapForJson.size() + ")");

            return mapForJson;

        } catch (IOException e) {
            log.info("History JSON not available.");
        }
        return new HashMap<>();
    }

    public Map<String, LogModel> loadFromJSON() {
        return loadJsonHistoryFile(GlobalConfigurationHandler.getInstance().config().getHistoryFileName());
    }

    public void generateHistoryJSON(Map<String, LogModel> logModelMap) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        try {
            String fileName = GlobalConfigurationHandler.getInstance().config().getHistoryFileName();
            log.info("Rewriting the {} file...", fileName);
            mapper.writeValue(new File(fileName), logModelMap);
            log.info("Newer version of history has been successfully saved!");
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}

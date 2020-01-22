package com.example.core_modules.reader.converter;

import com.example.core_modules.config.GlobalConfigurationHandler;
import com.example.core_modules.model.log.LogModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HashTool {

    Map<String, LogModel> hashedCollection;

    public HashTool(Map<String, LogModel> hashedCollection) {
        if (hashedCollection != null) {
            this.hashedCollection = hashedCollection;
        } else {
            this.hashedCollection = new HashMap<>();
        }
    }

    public void generateHash(LogModel currentLogModel, boolean isRegexFilterActive) {
        String hash = hash(currentLogModel.getMessage(), isRegexFilterActive);

        currentLogModel.setHashId(hash);

        if (hashedCollection.containsKey(hash)) {
            LogModel modelFromCollection = hashedCollection.get(hash);
            if (currentLogModel.getFirstCallDate().isAfter(modelFromCollection.getLastCallDate())) {
                modelFromCollection.setOccurrences(modelFromCollection.getOccurrences() + 1);
                modelFromCollection.setLastCallDate(currentLogModel.getFirstCallDate());
            }
        } else {
            hashedCollection.put(hash, currentLogModel);
        }
    }

    String hash(String text, boolean isRegexFilterActive) {
        String edited = text;
        List<String> regexFilterList = GlobalConfigurationHandler.getInstance()
                .config()
                .getRegexFilterList();

        if (isRegexFilterActive) {
            //TODO: [DONE] Regex filtration
            for (String regex : regexFilterList) {
                edited = edited.replaceAll(regex, "#REGEX#");
            }
        }

        edited = edited.replaceAll("\\d", "X");
        edited = edited.replaceAll("\\s+", "");

        return DigestUtils.sha256Hex(edited);
    }
}

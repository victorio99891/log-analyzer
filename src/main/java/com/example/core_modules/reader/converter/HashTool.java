package com.example.core_modules.reader.converter;

import com.example.core_modules.config.GlobalConfigurationHandler;
import com.example.core_modules.model.log.LogModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class HashTool {

    private HashTool() {

    }

    public static void generateHash(Map<String, LogModel> hashedCollection, LogModel currentLogModel) {
        String hash = hash(currentLogModel.getMessage());

        currentLogModel.setHashId(hash);

        if (hashedCollection.containsKey(hash)) {
            LogModel modelFromCollection = hashedCollection.get(hash);
            if (currentLogModel.getFirstCallDateTimeStamp().equals(modelFromCollection.getLastCallDateTimeStamp()) ||
                    currentLogModel.getFirstCallDateTimeStamp().isAfter(modelFromCollection.getLastCallDateTimeStamp())) {
                modelFromCollection.setOccurrences(modelFromCollection.getOccurrences() + 1);
                modelFromCollection.setLastCallDateTimeStamp(currentLogModel.getFirstCallDateTimeStamp());
            }
        } else {
            hashedCollection.put(hash, currentLogModel);
        }
    }

    static String hash(String text) {
        String edited = text;
        List<String> regexFilterList = GlobalConfigurationHandler.getInstance()
                .config()
                .getRegexFilterList();

        for (String regex : regexFilterList) {
            edited = edited.replaceAll(regex, "#REGEX#");
        }

        edited = edited.replaceAll("\\d", "X");
        edited = edited.replaceAll("\\s+", "");

        return DigestUtils.sha256Hex(edited);
    }
}

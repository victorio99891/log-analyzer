package com.example.core_modules.reader.converter;

import com.example.core_modules.model.log.LogModel;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

public class HashTool {

    Map<String, LogModel> hashedCollection;

    public HashTool(Map<String, LogModel> hashedCollection) {
        if (hashedCollection != null) {
            this.hashedCollection = hashedCollection;
        } else {
            this.hashedCollection = new HashMap<>();
        }
    }

    public void generateHash(LogModel currentLogModel) {
        String hash = hash(currentLogModel.getMessage());

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

    String hash(String text) {
        //TODO Think about REGEX or something in case where you have logs with something like
        // addreses as @73sd73 and in other log @98a8d -> in this case hashes will be different
        String edited = text;
        edited = edited.replaceAll("\\s+", "");
        edited = edited.replaceAll("\\d", "X");

//        return Hashing.sha256()
//                .hashString(edited, StandardCharsets.UTF_8)
//                .toString();
        return DigestUtils.sha256Hex(edited);
    }
}

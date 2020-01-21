package com.example.core_modules.explorer;

import com.example.cli.flow.SystemExiter;
import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.file.FileExtension;
import com.example.core_modules.model.file.FilePath;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
public final class DirectoryExplorer {

    static final String ERROR_LOG_FILE_PATTERN = "SysMonError";

    public Set<FilePath> exploreEndDir(File directory) {
        Set<FilePath> paths = new HashSet<>();

        log.info("Resolving all paths in passed directory...");

        try {
            if (directory == null) {
                throw new NullPointerException("Specified directory path has 'null' value.");
            }

            if (!directory.isDirectory()) {
                throw new UnsupportedFileFormatException("Specified path doesn't points to the directory.");
            }

            if (directory.listFiles() != null) {
                searchForFiles(paths, directory);
            } else {
                log.info("Specified path points to an empty directory.");
            }

        } catch (Exception e) {
            SystemExiter.getInstance().exitWithError(e);
        }

        if (paths.isEmpty()) {
            log.info("Collection of file paths is empty.");
        } else {
            log.info("Collection of file path has size of: " + paths.size());
        }


        return paths;
    }

    private void searchForFiles(Set<FilePath> paths, File directory) {
        if (directory != null) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file != null && file.isFile()) {
                    addFilePath(paths, file);
                } else if (file != null && file.isDirectory()) {
                    // Recursive call for looking for files in subdirectories
                    if (file.getName().contains("archive")) {
                        searchForFiles(paths, file);
                    }
                }
            }
        }
    }

    private void addFilePath(Set<FilePath> paths, File file) {
        FilePath filePath = new FilePath();
        if (file.getName().contains(ERROR_LOG_FILE_PATTERN)) {
            filePath.setName(file.getName());
            filePath.setFullPath(file.getAbsolutePath());
            filePath.setRelativePath(file.getPath());
            filePath.setExtension(extractExtensionFromFileName(file.getName()));

            boolean isAdded = paths.add(filePath);
            if (!isAdded) {
                log.info("Duplicated file path: " + file.getAbsolutePath());
            }
        }
    }

    private FileExtension extractExtensionFromFileName(String fileName) {
        FileExtension toReturn = null;
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

        for (FileExtension ext : FileExtension.values()) {
            if (ext.name().equalsIgnoreCase(extension)) {
                toReturn = ext;
            }
        }

        return toReturn;
    }

}

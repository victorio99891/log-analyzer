package com.example.core_modules.explorer;

import com.example.cli.flow.SystemExiter;
import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.file.FileExtension;
import com.example.core_modules.model.file.FilePath;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public final class DirectoryExplorer {

    static final String ERROR_LOG_FILE_PATTERN = "SysMonError";
    static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    public List<FilePath> exploreEndDir(File directory) {
        List<FilePath> paths = new ArrayList<>();

        log.info("----------------------------------");
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

        return sortAllPathsByDate(paths);
    }

    List<FilePath> sortAllPathsByDate(List<FilePath> paths) {

        FilePath logFilePath = null;

        // REMOVE *.log FILE FROM PATHS - THIS FILE HAS CREATION DATE SET TO NULL
        for (FilePath path : paths) {
            if (path.getCreationDate() == null) {
                logFilePath = path;
                paths.remove(logFilePath);
                break;
            }
        }

        // SORT ALL ZIP FILES
        Collections.sort(paths, new Comparator<FilePath>() {
            @Override
            public int compare(FilePath o1, FilePath o2) {
                return o1.getCreationDate().compareTo(o2.getCreationDate());
            }
        });

        // APPEND *.log FILE AT THE END OF PATHS
        if (logFilePath != null) {
            paths.add(logFilePath);
        }

        return paths;
    }

    private void searchForFiles(List<FilePath> paths, File directory) {
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

    private void addFilePath(List<FilePath> paths, File file) {
        FilePath filePath = new FilePath();
        if (file.getName().contains(ERROR_LOG_FILE_PATTERN)) {
            filePath.setName(file.getName());
            filePath.setFullPath(file.getAbsolutePath());
            filePath.setRelativePath(file.getPath());
            filePath.setExtension(extractExtensionFromFileName(file.getName()));

            //TODO: Parse date from ZIP files
            if (FileExtension.ZIP.equals(filePath.getExtension())) {

                StringTokenizer tokenizer = new StringTokenizer(file.getName(), "_");
                String dateWithExtension;
                for (int i = 0; i <= 1; i++) {
                    tokenizer.nextToken();
                }
                dateWithExtension = tokenizer.nextToken();

                DateTime fileRolledDate = resolveDateFromString(dateWithExtension.substring(0, dateWithExtension.length() - 4));

                filePath.setCreationDate(fileRolledDate);
            } else {
                filePath.setCreationDate(null);
            }

            //TODO: *.log file should be loaded always as last file in collection to analysis

            boolean isAdded = paths.add(filePath);
            if (!isAdded) {
                log.info("Duplicated file path: " + file.getAbsolutePath());
            }
        }
    }

    DateTime resolveDateFromString(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DirectoryExplorer.DATE_TIME_PATTERN);
        DateTime dateTime = null;
        if (dateString != null) {
            try {
                dateTime = new DateTime(simpleDateFormat.parse(dateString));
            } catch (ParseException e) {
                SystemExiter.getInstance().exitWithError(e);
            }
        }
        return dateTime;
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

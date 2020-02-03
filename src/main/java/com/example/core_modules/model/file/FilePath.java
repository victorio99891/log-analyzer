package com.example.core_modules.model.file;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.Objects;

@Getter
@Setter
public class FilePath {

    private String name;
    private FileExtension extension;
    private String fullPath;
    private String relativePath;
    private DateTime creationDate;

    public FilePath() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getExtension(), getFullPath(), getRelativePath());
    }
}

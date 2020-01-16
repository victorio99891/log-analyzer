package com.example.core_modules.model.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FilePath {

    private String name;
    private FileExtension extension;
    private String fullPath;
    private String relativePath;

    public FilePath(String name, FileExtension extension, String fullPath, String relativePath) {
        this.name = name;
        this.extension = extension;
        this.fullPath = fullPath;
        this.relativePath = relativePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilePath)) return false;
        FilePath filePath = (FilePath) o;
        return getName().equals(filePath.getName()) &&
                getExtension() == filePath.getExtension() &&
                getFullPath().equals(filePath.getFullPath()) &&
                getRelativePath().equals(filePath.getRelativePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getExtension(), getFullPath(), getRelativePath());
    }
}

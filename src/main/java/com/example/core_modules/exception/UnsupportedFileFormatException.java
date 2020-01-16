package com.example.core_modules.exception;

import java.io.IOException;

public final class UnsupportedFileFormatException extends IOException {
    public UnsupportedFileFormatException(String message) {
        super(message);
    }
}

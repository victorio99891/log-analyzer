package com.example.cli.exception;

public final class CliCommandDoublePassedException extends RuntimeException {
    public CliCommandDoublePassedException(String message) {
        super(message);
    }
}

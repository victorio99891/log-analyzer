package com.example.cli.exception;

public final class CliCommandNotFoundException extends Exception {
    public CliCommandNotFoundException(String message) {
        super(message);
    }
}

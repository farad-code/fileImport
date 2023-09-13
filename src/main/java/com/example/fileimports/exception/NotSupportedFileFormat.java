package com.example.fileimports.exception;

public class NotSupportedFileFormat extends RuntimeException{
    public NotSupportedFileFormat(String message) {
        super(message);
    }
}

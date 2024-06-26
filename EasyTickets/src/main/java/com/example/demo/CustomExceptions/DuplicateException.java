package com.example.demo.CustomExceptions;

// It used to indicate duplicate records, specifically when signing up a user with an email address that already exists.
public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
}
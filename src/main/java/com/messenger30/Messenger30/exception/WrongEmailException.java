package com.messenger30.Messenger30.exception;

public class WrongEmailException extends RuntimeException {
    public WrongEmailException(String message) {
        super(message);
    }
}
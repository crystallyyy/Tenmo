package com.techelevator.tenmo.exception;

public class TenmoException extends RuntimeException{
    public TenmoException() {
        super();
    }
    public TenmoException(String message) {
        super(message);
    }
    public TenmoException(String message, Exception cause) {
        super(message, cause);
    }
}

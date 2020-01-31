package com.energizeglobal.internship.util.exception;

/**
 * throws if server have sql error, or other errors.
 */
public class ServerSideException extends RuntimeException {
    public ServerSideException(Throwable cause) {
        super(cause);
    }

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException() {

    }
}

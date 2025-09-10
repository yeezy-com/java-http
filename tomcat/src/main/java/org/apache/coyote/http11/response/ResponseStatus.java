package org.apache.coyote.http11.response;

public enum ResponseStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    ;

    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String parseStatusLine() {
        return String.format("%d %s", code, message);
    }
}

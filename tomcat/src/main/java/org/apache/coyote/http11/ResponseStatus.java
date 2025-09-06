package org.apache.coyote.http11;

public enum ResponseStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    ;

    private final int code;

    ResponseStatus(int code) {
        this.code = code;
    }

    public String parseStatusLine() {
        return String.format("%d OK ", code);
    }
}

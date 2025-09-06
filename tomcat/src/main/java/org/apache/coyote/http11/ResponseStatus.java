package org.apache.coyote.http11;

public enum ResponseStatus {

    OK(200),
    ;

    private final int code;

    ResponseStatus(int code) {
        this.code = code;
    }

    public String parseStatusLine() {
        return String.format("OK %d ", code);
    }
}

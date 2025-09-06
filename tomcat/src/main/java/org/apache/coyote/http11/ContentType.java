package org.apache.coyote.http11;

public enum ContentType {
    HTML("html"),
    CSS("css"),
    JS("javascript"),
    PLAIN("plain");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }
}

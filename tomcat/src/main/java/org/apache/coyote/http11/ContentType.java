package org.apache.coyote.http11;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    PLAIN("", "text/plain");

    private final String fileType;
    private final String mimeType;

    ContentType(String fileType, String mimeType) {
        this.fileType = fileType;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}

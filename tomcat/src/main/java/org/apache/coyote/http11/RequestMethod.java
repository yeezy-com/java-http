package org.apache.coyote.http11;

public enum RequestMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    ;

    public boolean isGet() {
        return GET == this;
    }

    public boolean isPost() {
        return POST == this;
    }
}

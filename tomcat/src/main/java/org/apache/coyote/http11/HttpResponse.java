package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public final class HttpResponse {
    private final String contentType;
    private final String responseBody;

    public HttpResponse(String contentType, String responseBody) {
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String serveResponse() {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/" + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
            "",
            responseBody);
    }
}

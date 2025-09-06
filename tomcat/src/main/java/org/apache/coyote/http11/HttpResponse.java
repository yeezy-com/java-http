package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class HttpResponse {
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send200(final ContentType contentType, final String responseBody) throws IOException {
        String http = parseResponse(ResponseStatus.OK, contentType, responseBody);
        outputStream.write(http.getBytes());
        outputStream.flush();
    }

    public String makeResponse(final ResponseStatus status, final String contentType, final String responseBody) {
    private String parseResponse(final ResponseStatus status, final String contentType, final String responseBody) {
    public void send401(final ContentType contentType, final String responseBody) throws IOException {
        String http = parseResponse(ResponseStatus.UNAUTHORIZED, contentType, responseBody);
        outputStream.write(http.getBytes());
        outputStream.flush();
    }

    private String parseResponse(final ResponseStatus status,
                                 final ContentType contentType,
                                 final String responseBody
    ) {
        return String.join("\r\n",
            "HTTP/1.1 " + status.parseStatusLine(),
            "Content-Type: text/" + contentType.name() + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
            "",
            responseBody);
    }
}

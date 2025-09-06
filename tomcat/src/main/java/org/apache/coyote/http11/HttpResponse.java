package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpResponse {
    private Map<String, String> responseHeaders = new HashMap<>();

    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send200(final ContentType contentType, final String responseBody) throws IOException {
        addContentType(contentType);
        addContentLength(responseBody);
        String http = parseResponse(ResponseStatus.OK, responseBody);
        sendResponse(http);
    }

    public void send302(final String location, final ContentType contentType, final String responseBody)
        throws IOException {
        addContentType(contentType);
        addContentLength(responseBody);
        addHeader("Location", location);
        String http = parseResponse(ResponseStatus.FOUND, responseBody);
        sendResponse(http);
    }

    public void send401(final ContentType contentType, final String responseBody) throws IOException {
        addContentType(contentType);
        addContentLength(responseBody);
        String http = parseResponse(ResponseStatus.UNAUTHORIZED, responseBody);
        sendResponse(http);
    }

    private void sendResponse(String http) throws IOException {
        outputStream.write(http.getBytes());
        outputStream.flush();
    }

    private void addContentType(final ContentType contentType) {
        addHeader("Content-Type", "text/" + contentType.name().toLowerCase() + ";charset=utf-8 ");
    }

    private void addContentLength(final String responseBody) {
        addHeader("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
    }

    private void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    private String parseResponse(final ResponseStatus status,
                                 final String responseBody
    ) {
        StringBuilder response = new StringBuilder("HTTP/1.1 " + status.parseStatusLine() + "\r\n");
        for (String key : responseHeaders.keySet()) {
            response.append(key).append(": ").append(responseHeaders.get(key)).append(" ").append("\r\n");
        }
        response.append("\r\n").append(responseBody).append("\r\n");

        return response.toString();
    }
}

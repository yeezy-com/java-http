package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;

public class HttpResponse {
    private final Map<String, String> responseHeaders = new HashMap<>();
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    public void sendResponse(final ResponseStatus responseStatus,
                             final ContentType contentType,
                             final String responseBody) throws IOException {
        addHeader("Content-Type", contentType.getMimeType() + ";charset=utf-8");
        addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        String http = parseResponse(responseStatus, responseBody);

        writeOutputStream(http);
    }

    public void sendResponse(final ResponseStatus responseStatus) throws IOException {
        addHeader("Content-Length", String.valueOf(0));
        String http = parseResponse(responseStatus);

        writeOutputStream(http);
    }

    private void writeOutputStream(String http) throws IOException {
        outputStream.write(http.getBytes());
        outputStream.flush();
    }

    private String parseResponse(final ResponseStatus status) {
        StringBuilder response = parseResponseLine(status);
        parseResponseHeader(response);

        return response.toString();
    }

    private String parseResponse(final ResponseStatus status, final String responseBody) {
        StringBuilder response = parseResponseLine(status);
        parseResponseHeader(response);
        response.append("\r\n").append(responseBody);

        return response.toString();
    }

    private StringBuilder parseResponseLine(ResponseStatus status) {
        return new StringBuilder("HTTP/1.1 " + status.parseStatusLine() + " \r\n");
    }

    private void parseResponseHeader(StringBuilder response) {
        for (String key : responseHeaders.keySet()) {
            response.append(key).append(": ").append(responseHeaders.get(key)).append(" ").append("\r\n");
        }
    }
}

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

    private String responseBody;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    public void setResponseBody(final ContentType contentType, final String responseBody) {
        addHeader("Content-Type", contentType.getMimeType() + ";charset=utf-8");
        addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));

        this.responseBody = responseBody;
    }

    public void sendResponse(final ResponseStatus responseStatus) throws IOException {
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

        if (responseBody == null) {
            addHeader("Content-Length", String.valueOf(0));
            return response.toString();
        }

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

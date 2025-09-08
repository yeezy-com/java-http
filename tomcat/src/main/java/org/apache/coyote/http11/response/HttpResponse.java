package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;

public final class HttpResponse {
    private final Map<String, String> responseHeaders = new HashMap<>();
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send200(final ContentType contentType, final String responseBody) throws IOException {
        addHeader("Content-Type", contentType.getMimeType() + ";charset=utf-8");
        addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        String http = parseResponse(ResponseStatus.OK, responseBody);
        sendResponse(http);
    }

    public void send302(final String location, final String responseBody)
        throws IOException {
        addHeader("Content-Type", "text/html;charset=utf-8");
        addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        addHeader("Location", location);
        String http = parseResponse(ResponseStatus.FOUND, responseBody);
        sendResponse(http);
    }

    public void send401(final ContentType contentType, final String responseBody) throws IOException {
        addHeader("Content-Type", contentType.getMimeType() + ";charset=utf-8");
        addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        String http = parseResponse(ResponseStatus.UNAUTHORIZED, responseBody);
        sendResponse(http);
    }

    private void sendResponse(String http) throws IOException {
        outputStream.write(http.getBytes());
        outputStream.flush();
    }

    public void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    private String parseResponse(final ResponseStatus status,
                                 final String responseBody
    ) {
        StringBuilder response = new StringBuilder("HTTP/1.1 " + status.parseStatusLine() + "\r\n");
        for (String key : responseHeaders.keySet()) {
            response.append(key).append(": ").append(responseHeaders.get(key)).append(" ").append("\r\n");
        }
        response.append("\r\n").append(responseBody);

        return response.toString();
    }
}

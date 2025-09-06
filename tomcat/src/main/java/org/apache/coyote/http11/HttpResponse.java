package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class HttpResponse {
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send200(final String responseBody) throws IOException {
        String http = makeResponse(ResponseStatus.OK, "html", responseBody);
        outputStream.write(http.getBytes());
        outputStream.flush();
    }

    public String makeResponse(final ResponseStatus status, final String contentType, final String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 " + status.parseStatusLine(),
            "Content-Type: text/" + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
            "",
            responseBody);
    }
}

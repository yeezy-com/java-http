package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final Manager manager = SessionManager.getInstance();

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(final InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        );
        this.requestLine = new RequestLine(bufferedReader);
        this.requestHeader = new RequestHeader(bufferedReader);
        this.requestBody = new RequestBody(requestLine, requestHeader, bufferedReader);
    }

    public String getParam(final String key) {
        return requestLine.getParam(key);
    }

    public String getBody(final String key) {
        return requestBody.getBody(key);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isGetMethod() {
        return requestLine.isGet();
    }

    public boolean isPostMethod() {
        return requestLine.isPost();
    }

    public Session getSession(boolean status) throws IOException {
        if (requestHeader.existsCookie("JSESSIONID")) {
            String jsessionid = requestHeader.getCookie("JSESSIONID");

            if (manager.existsSession(jsessionid)) {
                return manager.findSession(jsessionid);
            }
        }

        if (status) {
            String id = UUID.randomUUID().toString();
            manager.add(new Session(id));
            return manager.findSession(id);
        }

        return null;
    }
}

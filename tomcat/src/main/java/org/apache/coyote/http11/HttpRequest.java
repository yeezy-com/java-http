package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final Map<String, String> params;
    private final RequestLine requestLine;

    public HttpRequest(final InputStream inputStream) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = bufferedReader.readLine();
            log.info("{}", requestLine);

            this.requestLine = new RequestLine(requestLine);
            this.params = getQueryStrings();
        } catch (IOException e) {
            throw new RuntimeException("요청을 읽는데 실패했습니다.");
        }
    }

    private Map<String, String> getQueryStrings() {
        Map<String, String> queryStrings = new HashMap<>();

        Optional<String> queryString = getQueryString();
        if (queryString.isEmpty()) {
            return new HashMap<>();
        }

        for (String keyValue : queryString.get().split("&")) {
            String[] keyValues = keyValue.split("=");
            if (keyValues.length == 1) {
                queryStrings.put(keyValues[0], "");
                continue;
            }

            queryStrings.put(keyValues[0], keyValues[1]);
        }

        return queryStrings;
    }

    private Optional<String> getQueryString() {
        int index = requestLine.getUri().indexOf("?");
        if (index == -1) {
            return Optional.empty();
        }

        return Optional.of(requestLine.getUri().substring(index + 1));
    }

    public String getParam(final String key) {
        return params.get(key);
    }

    public String path() {
        int index = requestLine.getUri().indexOf("?");
        if (index == -1) {
            return requestLine.getUri();
        }

        return requestLine.getUri().substring(0, index);
    }

    public boolean isGetMethod() {
        return requestLine.isGet();
    }

    public boolean isPostMethod() {
        return requestLine.isPost();
    }
}

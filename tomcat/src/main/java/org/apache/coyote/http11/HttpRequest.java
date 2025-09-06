package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Map<String, String> params;

    private String method;
    private String uri;

    public HttpRequest(final InputStream inputStream) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = bufferedReader.readLine();
            log.info("{}", requestLine);

            String[] requestLineToken = requestLine.split(" ");
            validateRequestLineIsStandard(requestLineToken);

            this.method = requestLineToken[0];
            this.uri = requestLineToken[1];
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
        int index = uri.indexOf("?");
        if (index == -1) {
            return Optional.empty();
        }

        return Optional.of(uri.substring(index + 1));
    }

    private void validateRequestLineIsStandard(String[] requestLineToken) {
        if (requestLineToken.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
    }

    public String getParam(final String key) {
        return params.get(key);
    }

    public String path() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    public String method() {
        return method;
    }

    public String uri() {
        return uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (HttpRequest) obj;
        return Objects.equals(this.method, that.method) &&
            Objects.equals(this.uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri);
    }

    @Override
    public String toString() {
        return "HttpRequest[" +
            "method=" + method + ", " +
            "uri=" + uri + ']';
    }

}

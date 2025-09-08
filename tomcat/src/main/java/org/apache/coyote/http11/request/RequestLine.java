package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestLine {
    private final Map<String, String> params = new HashMap<>();
    private final RequestMethod method;
    private final String uri;

    public RequestLine(final String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new UnsupportedOperationException("지원하지 않는 프로토콜입니다.");
        }
        String[] requestLineToken = requestLine.split(" ");
        validateRequestLineIsStandard(requestLineToken);

        this.method = RequestMethod.valueOf(requestLineToken[0].toUpperCase());
        this.uri = requestLineToken[1];
        parseQueryString();
    }

    private void parseQueryString() {
        Optional<String> queryString = getQueryString();
        if (queryString.isEmpty()) {
            return;
        }

        for (String keyValue : queryString.get().split("&")) {
            String[] keyValues = keyValue.split("=");
            if (keyValues.length == 1) {
                params.put(keyValues[0], "");
                continue;
            }

            params.put(keyValues[0], keyValues[1]);
        }
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

    public String getUri() {
        return uri;
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }
}

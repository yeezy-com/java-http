package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpRequest {

    private final Map<String, String> params = new ConcurrentHashMap<>();
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(final InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestHeader = new RequestHeader(bufferedReader);
        this.requestBody = new RequestBody(requestHeader, bufferedReader);
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
        int index = requestHeader.getUri().indexOf("?");
        if (index == -1) {
            return Optional.empty();
        }

        return Optional.of(requestHeader.getUri().substring(index + 1));
    }

    public String getParam(final String key) {
        return params.get(key);
    }

    public String getBody(final String key) {
        return requestBody.getBody(key);
    }

    public String path() {
        int index = requestHeader.getUri().indexOf("?");
        if (index == -1) {
            return requestHeader.getUri();
        }

        return requestHeader.getUri().substring(0, index);
    }

    public boolean isGetMethod() {
        return requestHeader.isGet();
    }

    public boolean isPostMethod() {
        return requestHeader.isPost();
    }
}

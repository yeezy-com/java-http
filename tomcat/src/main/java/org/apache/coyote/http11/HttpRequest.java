package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class HttpRequest {

    private final RequestHeader requestHeader;
    private final RequestBody requestBody;
    private final Map<String, String> params;

    public HttpRequest(final InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestHeader = new RequestHeader(bufferedReader);
        this.requestBody = new RequestBody(requestHeader, bufferedReader);
        this.params = getQueryStrings();
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
        int index = requestHeader.getUri().indexOf("?");
        if (index == -1) {
            return Optional.empty();
        }

        return Optional.of(requestHeader.getUri().substring(index + 1));
    }

    public String getParam(final String key) {
        return params.get(key);
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

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

    private final RequestHeader requestHeader;
    private final Map<String, String> body;
    private final Map<String, String> params;

    public HttpRequest(final InputStream inputStream) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.requestHeader = new RequestHeader(bufferedReader);

            // TODO: HttpMethod에 따라 분기 처리하기
            String length = requestHeader.getHeader("Content-Length");
            if (length != null) {
                int contentLength = Integer.parseInt(length);
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                this.body = getBody(requestBody);
            } else {
                this.body = new HashMap<>();
            }

            this.params = getQueryStrings();
        } catch (IOException e) {
            throw new RuntimeException("요청을 읽는데 실패했습니다.");
        }
    }

    private Map<String, String> getBody(final String requestBody) {
        Map<String, String> body = new HashMap<>();

        for (String keyValue : requestBody.split("&")) {
            String[] keyValues = keyValue.split("=");
            if (keyValues.length == 1) {
                body.put(keyValues[0], "");
                continue;
            }

            body.put(keyValues[0], keyValues[1]);
        }

        return body;
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

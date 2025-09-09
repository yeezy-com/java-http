package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private final Map<String, String> params = new HashMap<>();
    private final RequestMethod method;
    private final String uri;

    public RequestLine(final BufferedReader bufferedReader) {
        try {
            String requestLine = bufferedReader.readLine();
            log.info("{}", requestLine);

            if (requestLine == null || requestLine.isEmpty()) {
                throw new UnsupportedOperationException("지원하지 않는 프로토콜입니다.");
            }
            String[] requestLineToken = requestLine.split(" ");
            validateRequestLineIsStandard(requestLineToken);

            this.method = RequestMethod.valueOf(requestLineToken[0]);
            this.uri = requestLineToken[1];
            parseQueryString();
        } catch (IOException e) {
            throw new RuntimeException("요청 라인 파싱 중 오류가 발생했습니다.");
        }
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

    public String getPath() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }
}

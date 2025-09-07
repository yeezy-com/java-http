package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> body;

    public RequestBody(final RequestHeader requestHeader, final BufferedReader bufferedReader) {
        try {
            if (requestHeader.isGet()) {
                this.body = new HashMap<>();
                return;
            }

            String length = requestHeader.getHeader("Content-Length");
            int contentLength = Integer.parseInt(length);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);

            this.body = parseBody(requestBody);
        } catch (IOException e) {
            throw new RuntimeException("요청 처리 중 예외가 발생했습니다.");
        }
    }

    // TODO: Content-Type에 따라 파싱 방법 다르게 적용하기
    private Map<String, String> parseBody(final String requestBody) {
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

    public String getBody(final String key) {
        return body.get(key);
    }
}

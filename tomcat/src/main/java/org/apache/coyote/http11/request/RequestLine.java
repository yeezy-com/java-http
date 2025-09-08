package org.apache.coyote.http11.request;

public class RequestLine {
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
    }

    private void validateRequestLineIsStandard(String[] requestLineToken) {
        if (requestLineToken.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
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

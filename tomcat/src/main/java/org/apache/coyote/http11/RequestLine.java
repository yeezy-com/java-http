package org.apache.coyote.http11;

public class RequestLine {
    private final RequestMethod method;
    private final String uri;

    public RequestLine(final String requestLine) {
        String[] requestLineToken = requestLine.split(" ");
        validateRequestLineIsStandard(requestLineToken);

        this.method = RequestMethod.valueOf(requestLineToken[0]);
        this.uri = requestLineToken[1];
    }

    private void validateRequestLineIsStandard(String[] requestLineToken) {
        if (requestLineToken.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
    }

    public RequestMethod getMethod() {
        return method;
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

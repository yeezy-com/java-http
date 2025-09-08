package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeader {

    private static final Logger log = LoggerFactory.getLogger(RequestHeader.class);

    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private final RequestLine requestLine;

    public RequestHeader(final BufferedReader bufferedReader) {
        try {
            String requestLine = bufferedReader.readLine();
            log.info("{}", requestLine);
            this.requestLine = new RequestLine(requestLine);

            String tmpHeader = "";
            while (!(tmpHeader = bufferedReader.readLine()).isEmpty()) {
                int index = tmpHeader.indexOf(":");
                String key = tmpHeader.substring(0, index).trim();
                String value = tmpHeader.substring(index + 1).trim();

                headers.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("요청을 처리하는데 실패했습니다.");
        }
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getHeader(final String key) {
        return headers.get(key);
    }

    public String getUri() {
        return requestLine.getUri();
    }
}

package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeader {

    private static final Logger log = LoggerFactory.getLogger(RequestHeader.class);

    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookie httpCookie = new HttpCookie();

    public RequestHeader(final BufferedReader bufferedReader) {
        try {
            String tmpHeader = "";
            while (!(tmpHeader = bufferedReader.readLine()).isEmpty()) {
                int index = tmpHeader.indexOf(":");
                if (index == -1) {
                    throw new IllegalArgumentException("잘못된 형식의 HTTP 헤더입니다.");
                }

                String key = tmpHeader.substring(0, index).trim();
                String value = tmpHeader.substring(index + 1).trim();

                if (key.equalsIgnoreCase("cookie")) {
                    this.httpCookie.addCookie(value);
                    continue;
                }

                headers.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("요청을 처리하는데 실패했습니다.");
        }
    }

    public boolean existsCookie(final String key) {
        return httpCookie.existsKey(key);
    }
    
    public String getHeader(final String key) {
        return headers.get(key);
    }

    public String getCookie(String key) {
        return httpCookie.getValue(key);
    }
}

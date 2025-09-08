package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie() {
        cookies = new ConcurrentHashMap<>();
    }

    public void addCookie(final String rawCookie) {
        if (rawCookie.isEmpty()) {
            return;
        }

        String[] cookies = rawCookie.split(";");
        for (String cookie : cookies) {
            String key = cookie.split("=")[0];
            String value = cookie.split("=")[1];

            this.cookies.put(key, value);
        }
    }

    public boolean existsKey(final String key) {
        return cookies.containsKey(key);
    }
}

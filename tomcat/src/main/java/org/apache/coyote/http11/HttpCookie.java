package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie() {
        cookies = new HashMap<>();
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

    public static String ofJSessionId(String jSessionId) {
        return "JSESSIONID=" + jSessionId;
    }

    public boolean existsKey(final String key) {
        return cookies.containsKey(key);
    }

    public String getValue(final String key) {
        return cookies.get(key);
    }
}

package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.Test;

class HttpRequestTest {
    @Test
    void 쿼리_파라미터를_파싱할_수_있다() {
        final var testHtml = """
            GET /test?testone=1&testtwo=2 HTTP/1.1\r
            \r
            
            """;
        final var inputStream = new ByteArrayInputStream(testHtml.getBytes());
        final var request = new HttpRequest(inputStream);

        String param1 = request.getParam("testone");
        String param2 = request.getParam("testtwo");

        assertThat(param1).isEqualTo("1");
        assertThat(param2).isEqualTo("2");
    }

    @Test
    void URI에서_PATH를_추출할_수_있다() {
        final var testHtml = """
            GET /test?testone=1&testtwo=2 HTTP/1.1\r
            \r
            
            """;
        final var inputStream = new ByteArrayInputStream(testHtml.getBytes());
        final var request = new HttpRequest(inputStream);

        String path = request.getPath();

        assertThat(path).isEqualTo("/test");
    }

    @Test
    void getSession은_세션을_반환한다() throws IOException {
        final var sessionManager = SessionManager.getInstance();
        sessionManager.add(new Session("123"));
        final var testHtml = """
            GET /test HTTP/1.1\r
            Cookie: JSESSIONID=123\r
            \r
            
            """;
        final var inputStream = new ByteArrayInputStream(testHtml.getBytes());
        final var request = new HttpRequest(inputStream);

        Session session = request.getSession(true);

        assertThat(session.getId()).isEqualTo("123");
    }

    @Test
    void 세션이_없을때_getSession_true시_새로운_세션을_생성한다() throws IOException {
        final var sessionManager = SessionManager.getInstance();
        final var testHtml = """
            GET /test HTTP/1.1\r
            \r
            
            """;
        final var inputStream = new ByteArrayInputStream(testHtml.getBytes());
        final var request = new HttpRequest(inputStream);

        Session session = request.getSession(true);

        Session expected = sessionManager.findSession(session.getId());
        assertThat(session.getId()).isEqualTo(expected.getId());
    }

    @Test
    void 세션이_없을때_getSession_false시_새로운_세션을_생성하지_않는다() throws IOException {
        final var testHtml = """
            GET /test HTTP/1.1\r
            \r
            
            """;
        final var inputStream = new ByteArrayInputStream(testHtml.getBytes());
        final var request = new HttpRequest(inputStream);

        Session session = request.getSession(false);

        assertThat(session).isEqualTo(null);
    }
}

package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticFileLoader staticFileLoader;

    public Http11Processor(final Socket connection, final StaticFileLoader staticFileLoader) {
        this.connection = connection;
        this.staticFileLoader = staticFileLoader;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);

            if (httpRequest.isGetMethod()) {
                getMethodHandle(httpRequest, httpResponse);
                return;
            }

            if (httpRequest.isPostMethod()) {
                postMethodHandle(httpRequest, httpResponse);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getMethodHandle(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if ("/".equals(httpRequest.path())) {
            httpResponse.send200(ContentType.HTML, "Hello world!");
            return;
        }

        if ("/login".equals(httpRequest.path())) {
            if (httpRequest.existsKey("JSESSIONID")) {
                httpResponse.send302("/index.html", "");
                return;
            }

            httpResponse.send200(
                ContentType.HTML,
                new String(staticFileLoader.readAllFileWithUri(httpRequest.path() + ".html"))
            );
            return;
        }

        if ("/register".equals(httpRequest.path())) {
            httpResponse.send200(ContentType.HTML, new String(staticFileLoader.readAllFileWithUri("/register.html")));
            return;
        }

        int index = httpRequest.path().lastIndexOf(".");
        String extension = httpRequest.path().substring(index + 1);

        String staticFile = new String(staticFileLoader.readAllFileWithUri(httpRequest.path()));
        httpResponse.send200(ContentType.valueOf(extension.toUpperCase()), staticFile);
    }

    private void postMethodHandle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if ("/register".equals(httpRequest.path())) {
            String account = httpRequest.getBody("account");
            if (account == null || account.isEmpty()) {
                throw new IllegalArgumentException("잘못된 아이디입니다.");
            }

            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 회원입니다.");
            }

            String password = httpRequest.getBody("password");
            String email = httpRequest.getBody("email");
            User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            log.info("사용자 회원가입 완료: {}", newUser.getAccount());

            httpResponse.send302("/index.html", "");
            return;
        }

        if ("/login".equals(httpRequest.path())) {
            String account = httpRequest.getBody("account");
            String password = httpRequest.getBody("password");

            if (account != null && password != null) {
                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (user.isPresent() && user.get().checkPassword(password)) {
                    final Session session = httpRequest.getSession(true);
                    session.setAttribute("user", user.get());

                    log.info("{}", user.get());

                    httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
                    httpResponse.send302("/index.html", "");
                } else {
                    log.info("아이디 또는 비밀번호가 다릅니다.");
                    httpResponse.send401(
                        ContentType.HTML,
                        new String(staticFileLoader.readAllFileWithUri("/401.html"))
                    );
                }

                httpResponse.send400(ContentType.HTML, "아이디, 비밀번호는 필수입니다.");
            }
        }
    }
}

package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = bufferedReader.readLine();
            log.info("{}", requestLine);

            String[] requestLineToken = requestLine.split(" ");
            HttpResponse response = handle(new HttpRequest(requestLineToken[0], requestLineToken[1]));

            outputStream.write(response.serveResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if ("/".equals(httpRequest.path())) {
            return new HttpResponse("html", "Hello world!");
        }

        if ("/login".equals(httpRequest.path())) {
            Map<String, String> queryStrings = httpRequest.getQueryStrings();
            String account = queryStrings.get("account");
            String password = queryStrings.get("password");

            if (account != null && password != null) {
                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (user.isPresent() && user.get().checkPassword(password)) {
                    log.info("{}", user.get());
                } else {
                    log.info("아이디 또는 비밀번호가 다릅니다.");
                }
            }

            return new HttpResponse(
                "html",
                new String(staticFileLoader.readAllFileWithUri(httpRequest.path() + ".html"))
            );
        }

        int extensionIndex = httpRequest.uri().lastIndexOf(".");
        String extension = httpRequest.uri().substring(extensionIndex + 1);

        String staticFile = new String(staticFileLoader.readAllFileWithUri(httpRequest.uri()));
        return new HttpResponse(extension, staticFile);
    }
}

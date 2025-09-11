package com.techcourse.controller;

import com.techcourse.controller.util.StaticFileLoader;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PAGE = "/login.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getBody("account");
        String password = request.getBody("password");

        if (account != null && password != null) {
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent() && user.get().checkPassword(password)) {
                final Session session = request.getSession(true);
                session.setAttribute("user", user.get());

                log.info("{}", user.get());

                response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
                response.addHeader("Location", "/index.html");
                response.sendResponse(ResponseStatus.FOUND);
            } else {
                throw new UnauthorizedException(new String(StaticFileLoader.readAllFileWithUri("/401.html")));
            }
        } else {
            throw new IllegalArgumentException("아이디, 비밀번호는 필수입니다.");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getSession(false) != null) {
            response.addHeader("Location", "/index.html");
            response.sendResponse(ResponseStatus.FOUND);
            return;
        }

        response.setResponseBody(ContentType.HTML, new String(StaticFileLoader.readAllFileWithUri(LOGIN_PAGE)));
        response.sendResponse(ResponseStatus.OK);
    }

    @Override
    public boolean support(HttpRequest request) {
        return "/login".equals(request.getPath());
    }
}

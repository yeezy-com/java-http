package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StaticFileLoader;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    public static final String REGISTER_PAGE = "/register.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String account = request.getBody("account");
            validateAccountIsNotEmpty(account);

            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            validateUserIsExists(user);

            String password = request.getBody("password");
            String email = request.getBody("email");
            User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            log.info("사용자 회원가입 완료: {}", newUser.getAccount());

            response.addHeader("Location", "/index.html");
            response.sendResponse(ResponseStatus.FOUND);
        } catch (IllegalArgumentException e) {
            response.setResponseBody(ContentType.PLAIN, e.getMessage());
            response.sendResponse(ResponseStatus.BAD_REQUEST);
        }
    }

    private void validateUserIsExists(Optional<User> user) {
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    private void validateAccountIsNotEmpty(String account) {
        if (account == null || account.isEmpty()) {
            throw new IllegalArgumentException("잘못된 아이디입니다.");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponseBody(ContentType.HTML, new String(StaticFileLoader.readAllFileWithUri(REGISTER_PAGE)));
        response.sendResponse(ResponseStatus.OK);
    }
}

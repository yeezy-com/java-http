package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponseBody(ContentType.PLAIN, "Hello world!");
        response.sendResponse(ResponseStatus.OK);
    }
}

package com.techcourse.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StaticFileLoader;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        int index = request.getPath().lastIndexOf(".");
        if (index == -1) {
            response.setResponseBody(ContentType.PLAIN, "해당 정적 파일을 찾을 수 없습니다.");
            response.sendResponse(ResponseStatus.BAD_REQUEST);
            return;
        }
        String extension = request.getPath().substring(index + 1);

        String staticFile = new String(StaticFileLoader.readAllFileWithUri(request.getPath()));
        ContentType type = ContentType.from(extension);
        if (type == null) {
            response.setResponseBody(ContentType.PLAIN, "해당 정적 파일을 찾을 수 없습니다.");
            response.sendResponse(ResponseStatus.BAD_REQUEST);
            return;
        }

        response.setResponseBody(type, staticFile);
        response.sendResponse(ResponseStatus.OK);
    }
}

package com.techcourse.controller;

import com.techcourse.controller.util.StaticFileLoader;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;

public class StaticFileController extends AbstractController {

    public static final String DEFAULT_EXTENSION = ".html";
    public static final String EXTENSION_INDICATOR = ".";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        String extension = DEFAULT_EXTENSION;
        int index = path.lastIndexOf(EXTENSION_INDICATOR);

        if (index != -1) {
            extension = path.substring(index + 1);
        }

        String staticFile = new String(StaticFileLoader.readAllFileWithUri(path));
        ContentType type = ContentType.from(extension);

        if (type == null) {
            throw new IllegalArgumentException("지원하지 않는 정적 데이터입니다.");
        }

        response.setResponseBody(type, staticFile);
        response.sendResponse(ResponseStatus.OK);
    }

    @Override
    public boolean support(HttpRequest request) {
        return true;
    }
}

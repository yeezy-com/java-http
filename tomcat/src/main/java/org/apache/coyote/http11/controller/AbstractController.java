package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGetMethod()) {
            doGet(request, response);
            return;
        }

        if (request.isPostMethod()) {
            doPost(request, response);
            return;
        }

        response.sendResponse(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.sendResponse(ResponseStatus.METHOD_NOT_ALLOWED);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendResponse(ResponseStatus.METHOD_NOT_ALLOWED);
    }
}

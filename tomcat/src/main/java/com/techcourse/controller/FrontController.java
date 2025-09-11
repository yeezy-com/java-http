package com.techcourse.controller;

import com.techcourse.controller.util.StaticFileLoader;
import com.techcourse.exception.UnauthorizedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseStatus;

public class FrontController extends AbstractController {

    private static final List<Controller> controllers = new ArrayList<>();
    private static final FrontController instance = new FrontController();

    private FrontController() {
    }

    public static FrontController getInstance() {
        return instance;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Controller controller = getController(request);
            controller.service(request, response);
        } catch (UnauthorizedException e) {
            response.setResponseBody(ContentType.HTML, e.getMessage());
            response.sendResponse(ResponseStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            response.setResponseBody(ContentType.PLAIN, e.getMessage());
            response.sendResponse(ResponseStatus.BAD_REQUEST);
        } catch (FileNotFoundException e) {
            response.setResponseBody(ContentType.HTML, new String(StaticFileLoader.readAllFileWithUri("/404.html")));
            response.sendResponse(ResponseStatus.NOT_FOUND);
        } catch (Exception e) {
            response.sendResponse(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean support(HttpRequest request) {
        return true;
    }

    public Controller getController(final HttpRequest request) {
        for (Controller controller : controllers) {
            if (controller.support(request)) {
                return controller;
            }
        }

        return null;
    }

    public FrontController add(final Controller controller) {
        controllers.add(controller);
        return this;
    }
}

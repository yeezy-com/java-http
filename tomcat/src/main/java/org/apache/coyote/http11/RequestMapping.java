package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final List<Controller> controllers = new ArrayList<>();

    public static RequestMapping getInstance() {
        return new RequestMapping();
    }

    public RequestMapping add(final Controller controller) {
        controllers.add(controller);
        return this;
    }

    public Controller getController(final HttpRequest request) {
        for (Controller controller : controllers) {
            if (controller.support(request)) {
                return controller;
            }
        }

        return null;
    }

    private RequestMapping() {
    }
}

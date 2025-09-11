package com.techcourse;

import com.techcourse.controller.FrontController;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticFileController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        FrontController instance = FrontController.getInstance()
            .add(new HomeController())
            .add(new LoginController())
            .add(new RegisterController())
            .add(new StaticFileController());
        RequestMapping.getInstance()
            .add(instance);
        tomcat.start();
    }
}

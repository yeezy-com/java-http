package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final RequestMapping requestMapping = RequestMapping.getInstance();
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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
            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);

            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

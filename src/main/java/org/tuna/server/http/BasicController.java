package org.tuna.server.http;

import org.tuna.server.http.v2.constants.Const;
import org.tuna.server.http.v2.annotation.Controller;
import org.tuna.server.http.v2.server.HttpServer;
import org.tuna.server.http.v2.annotation.Route;

@Controller("BasicController")
public class BasicController {
    public static void main(String[] args) {
        new HttpServer(8080, new BasicController());
    }

    public BasicController() {}

    @Route(method = Const.GET, path = "/")
    public String helloWorld() {
        return "Hello, World";
    }
}

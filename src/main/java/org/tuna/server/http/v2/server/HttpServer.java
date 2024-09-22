package org.tuna.server.http.v2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {
    private final static Logger LOGGER = Logger.getLogger(HttpServer.class.getName());
    private ServerSocket serverSocket;

    public HttpServer(Integer port, Object controllerClass) {
        try{
            serverSocket = new ServerSocket(port);

            ExecutorService service = Executors.newCachedThreadPool();

            while (true) {
                Socket socket = serverSocket.accept();

                RunnableHandler runnableHandler = new RunnableHandler(socket, controllerClass);

                service.execute(runnableHandler);
            }
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "An exception occurred: ",e);
        }
    }
}
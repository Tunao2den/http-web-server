package org.tuna.server.http.v2.server;

import org.tuna.server.http.v2.annotation.AnnotationProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {
    private final static Logger LOGGER = Logger.getLogger(HttpServer.class.getName());
    private Object controllerClass;
    private AnnotationProcessor annotationProcessor;
    private HttpRequestParser httpRequestParser;
    private ServerSocket serverSocket;
    private Socket socket;
    private Object output;
    public BufferedReader in;
    private PrintWriter out;
    private String httpResponse;


    public HttpServer(Integer port, Object controllerClass) {
        try{
            this.controllerClass = controllerClass;
            this.annotationProcessor = new AnnotationProcessor(controllerClass);

            serverSocket = new ServerSocket(port);

            startListening();

        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "An exception occurred: ",e);
        }
    }

    private void startListening() {
        try {
            while (true){
                socket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());

                in.mark(100);
                String requestLine = in.readLine();

                if (requestLine == null ) {
                    String optionsResponse = "HTTP/1.1 200 OK\r\n" +
                            "Allow: GET, POST, OPTIONS\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n";
                    out.write(optionsResponse);
                    out.flush();
                    socket.close();
                } else {
                    in.reset();

                    httpRequestParser = new HttpRequestParser(in);
                    LOGGER.log(Level.SEVERE, httpResponse);

                    if (checkPathExists()){
                        invokeMatchingMethodAndAssignOutput();
                        responseBuilder();
                    } else {
                        setHttpResponseNotFound();
                    }
                    out.write(httpResponse);
                    out.flush();
                    socket.close();
                }
            }
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "An exception occurred: ",e);
        }
    }

    private void setHttpResponseNotFound() {
        httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
    }

    private boolean checkPathExists() {
        if (annotationProcessor.checkClassIsAnnotated()){
            Method method = annotationProcessor.findMatchingFunction(httpRequestParser.getMethod(), httpRequestParser.getPath());
            if (method != null){
                return true;
            }
        }

        return false;
    }

    private void invokeMatchingMethodAndAssignOutput() {
        try {
            if (annotationProcessor.checkClassIsAnnotated()){
                Method method = annotationProcessor.findMatchingFunction(httpRequestParser.getMethod(), httpRequestParser.getPath());
                if (method != null){
                    output =  method.invoke(controllerClass);
                } else {
                    setHttpResponseNotFound();
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void responseBuilder() {
        String message = output.toString();

         httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 23\r\n" +
                "\r\n" +
                message;
    }
}
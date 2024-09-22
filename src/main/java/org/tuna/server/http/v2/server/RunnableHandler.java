package org.tuna.server.http.v2.server;

import org.tuna.server.http.v2.annotation.AnnotationProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunnableHandler implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(HttpServer.class.getName());
    private Socket socket;
    HttpRequestParser httpRequestParser = new HttpRequestParser();
    private String httpResponse;
    private Object controllerClass;
    private AnnotationProcessor annotationProcessor;
    private Object output;

    public RunnableHandler(Socket socket, Object controllerClass) {
        this.socket = socket;
        this.controllerClass = controllerClass;
        this.annotationProcessor = new AnnotationProcessor(controllerClass);
    }

    @Override
    public void run() {
        startListening(socket);
        System.out.println("Current thread: " + Thread.currentThread().getName() + " message: " + httpResponse);
    }

    private void startListening(Socket socket) {
        try {
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream());

            BufferedReader toParser = in;

            in.mark(100);
            String requestLine = in.readLine();

            if (requestLine == null) {
                out.write("Connection Accepted");
                out.flush();
                socket.close();
            } else {
                in.reset();
                httpRequestParser.parseAll(toParser);

                if (checkPathExists()) {
                    invokeMatchingMethodAndAssignOutput();
                    responseBuilder();
                } else {
                    setHttpResponseNotFound();
                }
                out.write(httpResponse);
                out.flush();
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An exception occurred: ", e);
        }
    }

    private void setHttpResponseNotFound() {
        httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
    }

    synchronized private boolean checkPathExists() {
        boolean isAnnotated = annotationProcessor.checkClassIsAnnotated();
        String requestMethod = httpRequestParser.getMethod();
        String requestPath = httpRequestParser.getPath();

        if (isAnnotated) {
            Method method = annotationProcessor.findMatchingFunction(requestMethod, requestPath);
            if (method != null) {
                return true;
            }
        }
        return false;
    }

    private void invokeMatchingMethodAndAssignOutput() {
        try {
            boolean isAnnotated = annotationProcessor.checkClassIsAnnotated();
            String requestMethod = httpRequestParser.getMethod();
            String requestPath = httpRequestParser.getPath();

            if (isAnnotated) {
                Method method = annotationProcessor.findMatchingFunction(requestMethod, requestPath);
                if (method != null) {
                    output = method.invoke(controllerClass);
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

    private void cases() {
        String requestMethod = httpRequestParser.getMethod();

        switch (requestMethod) {
            case "OPTIONS":
                break;

            case "GET":
                break;

            case "POST":
                break;
        }
    }
}
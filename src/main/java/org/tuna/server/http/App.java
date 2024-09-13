package org.tuna.server.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    private static boolean checkRegexBase(String input) {
        String regex = "^/+$";
        return input.matches(regex);
    }
    private static boolean checkRegexVariable(String input) {
        String regex = "^/echo/[^/]+$";
        return input.matches(regex);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4221);
        while (true){

            Socket socket = serverSocket.accept(); // Wait for connection from client.

            try {
                serverSocket.setReuseAddress(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                String requestLine = in.readLine();
                System.out.println("Request line: " + requestLine);

                String[] words;
                String endpoint = "";
                if (requestLine != null){
                    words = requestLine.split("\\s+");
                    endpoint = words[1];
                }

                String httpResponse;
                if (checkRegexBase(endpoint)){
                    httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
                } else if (checkRegexVariable(endpoint)) {
                    String[] messages =endpoint.split("/");
                    String message = messages[2];
                    int length = message.length();

                    httpResponse = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: " + length + "\r\n\r\n" +
                            message;
                } else {
                    httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                }

                out.write(httpResponse);
                out.flush();
                socket.close();

                System.out.println("accepted new connection");
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
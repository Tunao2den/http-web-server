package org.tuna.server.http.v2;

import org.tuna.server.http.v2.server.HttpRequestParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class test {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4221);

        while (true){
            Socket socket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

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

                HttpRequestParser httpRequest = new HttpRequestParser(in);

                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 23\r\n" +
                        "\r\n" +
                        "<h1>Hello, World!</h1>";

                out.write(httpResponse);
                out.flush();
                socket.close();
            }
        }
    }
}

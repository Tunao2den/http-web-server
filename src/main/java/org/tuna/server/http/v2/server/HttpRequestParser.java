package org.tuna.server.http.v2.server;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class HttpRequestParser {
    private static final Logger LOGGER = Logger.getLogger(HttpRequestParser.class.getName());
    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequestParser(){}

    public void parseAll(BufferedReader in) {
        try {
            if(in.readLine() != null){
                in.reset();
                parseStartLine(in);
                parseHeaders(in);
                parseBody(in);
            } else {
                throw new IOException("HTTP start line is null");
            }

        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "An exception occurred", e);
        }
    }

    private void parseStartLine(BufferedReader in) {
        try {
            String startLine = in.readLine();

            String[] startLineTokens = startLine.split("\\s+");

            if (startLineTokens.length != 3) {
                throw new IOException("Invalid HTTP start line, expected 3 lines");
            }

            setMethod(startLineTokens[0]);
            setPath(startLineTokens[1]);
            setHttpVersion(startLineTokens[2]);

        } catch (IOException e){
            LOGGER.log(Level.SEVERE,"IOException: ",e);
        }
    }

    private void parseHeaders(BufferedReader in) {
        try {
            Map<String, String> headers = new HashMap<>();
            String line;

            while ((line = in.readLine()) != null && !line.isEmpty()) {

                if (!line.contains(":")) {
                    throw new IOException("Invalid header line: " + line);
                }

                String[] headerTokens = line.split(": ", 2);
                if (headerTokens.length == 2) {
                    getHeaders().put(headerTokens[0], headerTokens[1]);
                }

                String headerName = headerTokens[0].trim();
                String headerValue = headerTokens[1].trim();

                headers.put(headerName, headerValue);
            }
        } catch (IOException e){
            LOGGER.log(Level.SEVERE,"IOException: ",e);
        }
    }

    private void parseBody(BufferedReader in) {
        try {
            if (getHeaders().containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(getHeaders().get("Content-Length"));
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars);
                setBody(new String(bodyChars));
            }
        } catch (IOException e){
            LOGGER.log(Level.SEVERE,"IOException: ",e);
        }
    }
}
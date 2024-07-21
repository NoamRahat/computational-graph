package graph;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser;
import servlets.Servlet;

public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n\r\nTopic Displayed";
        toClient.write(response.getBytes());
    }

    @Override
    public void close() throws IOException {
        // Cleanup resources if needed
    }
}

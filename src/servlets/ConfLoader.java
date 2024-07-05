package servlets;
import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser;


public class ConfLoader implements Servlet {
        @Override
        public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
                String response = "HTTP/1.1 200 OK\r\n\r\nConfiguration Loaded";
                toClient.write(response.getBytes());
        }

        @Override
        public void close() throws IOException {
                // Cleanup resources if needed
        }
}
    
    
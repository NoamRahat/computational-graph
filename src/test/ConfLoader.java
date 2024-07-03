package test;
import java.io.IOException;
import java.io.OutputStream;


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
    
    
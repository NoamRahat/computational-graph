package servlets;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser;

public class HtmlLoader implements Servlet{
        private String htmlFolder;

        public HtmlLoader(String htmlFolder) {
                this.htmlFolder = htmlFolder;
        }

        @Override
        public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
                String response = "HTTP/1.1 200 OK\r\n\r\nHTML Loaded from " + htmlFolder;
                toClient.write(response.getBytes());
        }

        @Override
        public void close() throws IOException {
                // Cleanup resources if needed
        }
}

package servlets;

import server.RequestParser.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlLoader implements Servlet {
    private final String htmlFolder;

    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String fileName = ri.getUriSegments()[ri.getUriSegments().length - 1];
        Path filePath = Paths.get(htmlFolder, fileName);

        if (Files.exists(filePath)) {
            byte[] content = Files.readAllBytes(filePath);
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n";
            toClient.write(response.getBytes());
            toClient.write(content);
        } else {
            String notFound = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<html><body><h1>404 Not Found</h1></body></html>";
            toClient.write(notFound.getBytes());
        }
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}

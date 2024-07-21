package servlets;

import configs.GenericConfig;
import graph.Graph;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ConfLoader implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String configContent = new String(ri.getContent(), StandardCharsets.UTF_8);

        GenericConfig config = new GenericConfig();
        config.setConfFile(configContent);
        config.create();

        Graph graph = new Graph();
        graph.createFromTopics();

        String graphHtml = String.join("\n", HtmlGraphWriter.getGraphHTML(graph));

        String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + graphHtml;
        toClient.write(response.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}

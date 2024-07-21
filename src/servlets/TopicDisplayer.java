package servlets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String topic = ri.getParameters().get("topic");
        String messageContent = ri.getParameters().get("message");

        TopicManagerSingleton.get().getTopic(topic).publish(new Message(messageContent));

        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><body><table border='1'><tr><th>Topic</th><th>Last Value</th></tr>");

        for (Topic t : TopicManagerSingleton.get().getTopics()) {
            htmlResponse.append("<tr><td>").append(t.getName()).append("</td><td>")
                        .append(t.getLastMessage().asText()).append("</td></tr>");
        }

        htmlResponse.append("</table></body></html>");

        String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + htmlResponse.toString();
        toClient.write(response.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }
}

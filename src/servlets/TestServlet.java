package servlets;
import server.RequestParser.RequestInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class TestServlet implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        PrintWriter out = new PrintWriter(toClient);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println("Content-Length: 14");
        out.println();
        out.println("Hello, World!");
        out.flush();
    }

    @Override
    public void close() throws IOException {
        // No specific resource to close in this example

    }
}
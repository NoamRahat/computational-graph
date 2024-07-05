package servlets;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser;


public class SubServlet implements Servlet {
    @Override
    public void handle(RequestParser.RequestInfo requestInfo, OutputStream out) {
        try {
            System.out.println("SubServlet handling request");

            // Generate a response (for example, a simple addition result)
            String body = "Result: " + (10 + 3) + "\r\n";
            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: text/plain\r\n" +
                              "Content-Length: " + body.length() + "\r\n" +
                              "\r\n" +
                              body;

            System.out.println("Response:\n" + response);

            out.write(response.getBytes());
            out.flush();

            System.out.println("SubServlet sent response");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
package servlets;

import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;

public class SubServlet implements Servlet {
    @Override
    public void handle(RequestParser.RequestInfo requestInfo, OutputStream out) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                          "Content-Type: text/plain\r\n" +
                          "Content-Length: 12\r\n" +
                          "\r\n" +
                          "Result: 13\r\n";
        out.write(response.getBytes());
        out.flush();
    }

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}

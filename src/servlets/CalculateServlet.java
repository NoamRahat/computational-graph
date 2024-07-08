package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import server.RequestParser.RequestInfo;

public class CalculateServlet implements Servlet {

    @Override
    public void handle(RequestInfo requestInfo, OutputStream response) throws IOException {
        Map<String, String> parameters = requestInfo.getParameters();
        int a = Integer.parseInt(parameters.getOrDefault("a", "0"));
        int b = Integer.parseInt(parameters.getOrDefault("b", "0"));
        String operation = parameters.getOrDefault("op", "subtract");
        int result;

        switch (operation) {
            case "add":
                result = a + b;
                break;
            case "multiply":
                result = a * b;
                break;
            case "divide":
                if (b != 0) {
                    result = a / b;
                } else {
                    result = 0; // או טיפול מיוחד בחילוק באפס
                }
                break;
            case "subtract":
            default:
                result = a - b;
                break;
        }

        String resultString = "Result: " + result;
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + resultString.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                resultString;
        response.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        response.flush();
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }
}

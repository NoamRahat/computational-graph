package test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import test.RequestParser.RequestInfo;

public class CalculatorServlet implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        int result = 0;
        String operation = ri.getParameters().get("operation");
        int a = Integer.parseInt(ri.getParameters().get("a"));
        int b = Integer.parseInt(ri.getParameters().get("b"));

        switch (operation) {
            case "add":
                result = a + b;
                break;
            case "subtract":
                result = a - b;
                break;
            case "multiply":
                result = a * b;
                break;
            case "divide":
                result = a / b;
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
        
        PrintWriter writer = new PrintWriter(toClient);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/plain");
        writer.println();
        writer.println("Result: " + result);
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this simple implementation
    }
}

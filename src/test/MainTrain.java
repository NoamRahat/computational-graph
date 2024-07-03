package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import test.RequestParser.RequestInfo;

public class MainTrain {

    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                            "Host: example.com\n" +
                            "Content-Length: 5\n"+
                            "\n" +
                            "filename=\"hello_world.txt\"\n"+
                            "\n" +
                            "hello world!\n"+
                            "\n" ;

        BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            } 
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            } 
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }        
    }

    public static void testServer() throws Exception {
        HTTPServer server = new MyHTTPServer(8080, 5);
        server.addServlet("GET", "/calculate", new CalculatorServlet());

        // Add debug statement to indicate server start
        System.out.println("Starting server...");
        server.start();

        // Create a client socket
        try (Socket client = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

            // Send a request to the server
            System.out.println("Sending request...");
            out.println("GET /calculate?operation=add&a=5&b=3 HTTP/1.1");
            out.println("Host: localhost");
            out.println();

            // Read the response
            System.out.println("Reading response...");
            StringBuilder response = new StringBuilder();
            String line;
            boolean headersRead = false;
            while ((line = in.readLine()) != null) {
                if (!headersRead && line.isEmpty()) {
                    headersRead = true;
                    continue; // Skip empty line after headers
                }
                response.append(line).append("\n");
            }

            // Check the response
            System.out.println("Response received:");
            System.out.println(response.toString());

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            // Close client resources
            System.out.println("Closing client...");
        }

        // Close server
        System.out.println("Closing server...");
        server.close();

        // Wait for threads to finish
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        threadPool.shutdown();
        if (!threadPool.awaitTermination(2, TimeUnit.SECONDS)) {
            System.out.println("Test failed: threads did not terminate in 2 seconds");
        } else {
            System.out.println("Server and threads closed successfully.");
        }
    }

    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try {
            testServer(); // 60 points
        } catch (Exception e) {
            System.out.println("Your server threw an exception (-60): " + e.getMessage());
        }
        System.out.println("done");
    }
}

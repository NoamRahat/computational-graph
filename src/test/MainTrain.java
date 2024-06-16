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

import test.MainTrain.TestServlet;
import test.RequestParser.RequestInfo;

import java.io.IOException;
import java.net.ServerSocket;



public class MainTrain { // RequestParser

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

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
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


    static class TestServlet implements Servlet {
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

    private static String sendHttpRequest(String method, String host, int port, String path) throws IOException {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(method + " " + path + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("Connection: close");
            out.println();
            out.flush();

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            return response.toString();
        }
    }

    private static void testServer() throws IOException {
        HTTPServer server = new MyHTTPServer(8080, 10);
        server.addServlet("GET", "/test", new TestServlet());
        server.start();

        try {
            Thread.sleep(1000); // Give the server a moment to start

            String response = sendHttpRequest("GET", "localhost", 8080, "/test");
            if (response.contains("Hello, World!")) {
                System.out.println("GET test passed.");
            } else {
                System.out.println("GET test failed.");
            }

            response = sendHttpRequest("GET", "localhost", 8080, "/invalid");
            if (!response.contains("HTTP/1.1 404 Not Found")) {
                System.out.println("Invalid URI test failed.");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }
    

    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }

}

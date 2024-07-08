package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import server.MyHTTPServer;
import server.RequestParser;
import servlets.SubServlet;

public class MainTrain {

    private static void testParseRequest() {
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                         "Host: example.com\n" +
                         "Content-Length: 5\n" +
                         "\n" +
                         "filename=\"hello_world.txt\"\n" +
                         "\n" +
                         "hello world!\n" +
                         "\n";

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
                for (String s : requestInfo.getUriSegments()) {
                    System.out.println(s);
                }
            }

            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename", "\"hello_world.txt\"");
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

        request = "GET /sub?a=10&b=3 HTTP/1.1\n" +
                  "Host: localhost\n" +
                  "Content-Length: 0\n" +
                  "\n";

        input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));

        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }

    public static void testServer() throws Exception {
        System.out.println("1num of threads " + Thread.activeCount());

        MyHTTPServer server = new MyHTTPServer(8080, 5);
        server.addServlet("GET", "/sub", new SubServlet());

        server.start();
        System.out.println("2num of threads " + Thread.activeCount());

        Thread.sleep(1000);

        int numOfThreads = Thread.activeCount();
        if (numOfThreads != 2) {
            return;
        }

        try {
            Socket client = new Socket("localhost", 8080);
            OutputStream out = client.getOutputStream();

            String request = "GET /sub?a=10&b=3 HTTP/1.1\r\n" +
                             "Host: localhost\r\n" +
                             "Content-Length: 0\r\n" +
                             "\r\n";
            out.write(request.getBytes());
            out.flush();

            Thread.sleep(1000);

            client.setSoTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String responseLine;
            StringBuilder response = new StringBuilder();
            try {
                while ((responseLine = in.readLine()) != null) {
                    response.append(responseLine).append("\r\n");
                    if (responseLine.startsWith("Result:")) {
                        System.out.println(responseLine);
                        break;
                    }
                }
                System.out.println("Full response:\n" + response.toString());
            } catch (SocketTimeoutException e) {
                System.out.println("Read timed out. No data received for 5 seconds.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.close();
            in.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);

        System.out.println(Thread.activeCount());
    }

    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try {
            testServer(); // 60 points
        } catch (Exception e) {
            System.out.println("Your server threw an exception (-60)");
        }
        System.out.println(Thread.activeCount());
        System.out.println("done");
    }
}

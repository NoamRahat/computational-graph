package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import server.MyHTTPServer;
import server.RequestParser;
import servlets.CalculateServlet;
import servlets.CalculatorServlet;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.SubServlet;
import servlets.TestServlet;


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


    public static void testServer() throws Exception {
        System.out.println("Starting testServer...");

        // validate that port is available
        int port = 8082;
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", port));
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Port " + port + " is already in use, please free the port and try again.");
            throw new Exception("Port already in use");
        }

        // create server
        MyHTTPServer server = new MyHTTPServer(port, 10);

        // add servlets
        server.addServlet("GET", "/calculate", new CalculateServlet());
        server.addServlet("GET", "/calculator", new CalculatorServlet());
        server.addServlet("GET", "/conf", new ConfLoader());
        server.addServlet("GET", "/html", new HtmlLoader("/path/to/html"));
        server.addServlet("GET", "/sub", new SubServlet());
        server.addServlet("GET", "/test", new TestServlet());

        // start server
        server.start();
        System.out.println("Server started...");

        // validate that server started correctly
        Thread.sleep(1000); // short delay to allow server to start
        int activeThreads = Thread.activeCount();
        System.out.println("Active threads after server start: " + activeThreads);
        if (activeThreads != 2) {
            throw new Exception("Server did not start correctly, active threads: " + activeThreads);
        }

        // create client and interact with server
        try (Socket client = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

            // send request to server
            out.println("GET /calculate?a=5&b=3 HTTP/1.1");
            out.println("Host: localhost");
            out.println();

            // read response from server
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine).append("\n");
                if (responseLine.isEmpty()) {
                    // read the body of the response
                    while (in.ready()) {
                        response.append((char) in.read());
                    }
                    break;
                }
            }

            System.out.println("Response from server: " + response.toString());

            // validate response
            if (!response.toString().contains("Result: 2")) {
                throw new Exception("Unexpected response from server: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Client-server interaction failed");
        }

        // close server
        server.close();
        System.out.println("Server closed...");

        // wait for server to close
        Thread.sleep(2000);
        activeThreads = Thread.activeCount();
        System.out.println("Active threads after server close: " + activeThreads);
        if (activeThreads != 1) {
            throw new Exception("Server did not close correctly, active threads: " + activeThreads);
        }

        System.out.println("testServer completed successfully");
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

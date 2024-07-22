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
import java.util.List;
import java.util.Map;
import java.util.Random;

import configs.Node;
import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import server.MyHTTPServer;
import server.RequestParser;
import servlets.CalculateServlet;
import servlets.CalculatorServlet;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.SubServlet;
import servlets.TestServlet;

public class MainTrain {

    // Test the parsing of an HTTP request
    private static void testParseRequest() {
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                         "Host: example.com\n" +
                         "Content-Length: 5\n"+
                         "\n" +
                         "filename=\"hello_world.txt\"\n"+
                         "\n" +
                         "hello world!\n"+
                         "\n";

        BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for (String s : requestInfo.getUriSegments()) {
                    System.out.println(s);
                }
            }

            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename", "\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }

    // Test the server functionality
    public static void testServer() throws Exception {
        System.out.println("Starting testServer...");

        int port = 8082;
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", port));
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Port " + port + " is already in use, please free the port and try again.");
            throw new Exception("Port already in use");
        }

        MyHTTPServer server = new MyHTTPServer(port, 10);
        server.addServlet("GET", "/calculate", new CalculateServlet());
        server.addServlet("GET", "/calculator", new CalculatorServlet());
        server.addServlet("GET", "/conf", new ConfLoader());
        server.addServlet("GET", "/html", new HtmlLoader("/path/to/html"));
        server.addServlet("GET", "/sub", new SubServlet());
        server.addServlet("GET", "/test", new TestServlet());

        server.start();
        System.out.println("Server started...");

        Thread.sleep(1000);
        int activeThreads = Thread.activeCount();
        System.out.println("Active threads after server start: " + activeThreads);
        if (activeThreads != 2) {
            throw new Exception("Server did not start correctly, active threads: " + activeThreads);
        }

        try (Socket client = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

            out.println("GET /calculate?a=5&b=3 HTTP/1.1");
            out.println("Host: localhost");
            out.println();

            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine).append("\n");
                if (responseLine.isEmpty()) {
                    while (in.ready()) {
                        response.append((char) in.read());
                    }
                    break;
                }
            }

            System.out.println("Response from server: " + response.toString());

            if (!response.toString().contains("Result: 2")) {
                throw new Exception("Unexpected response from server: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Client-server interaction failed");
        }

        server.close();
        System.out.println("Server closed...");

        Thread.sleep(2000);
        activeThreads = Thread.activeCount();
        System.out.println("Active threads after server close: " + activeThreads);
        if (activeThreads != 1) {
            throw new Exception("Server did not close correctly, active threads: " + activeThreads);
        }

        System.out.println("testServer completed successfully");
    }

    // Test the functionality of the Message class
    public static void testMessage() {
        String testString = "Hello";
        Message msgFromString = new Message(testString);
        if (!testString.equals(msgFromString.asText)) {
            System.out.println("Error: String constructor - asText does not match input string (-5)");
        }
        if (!Arrays.equals(testString.getBytes(), msgFromString.data)) {
            System.out.println("Error: String constructor - data does not match input string bytes (-5)");
        }
        if (!Double.isNaN(msgFromString.asDouble)) {
            System.out.println("Error: String constructor - asDouble should be NaN for non-numeric string (-5)");
        }
        if (msgFromString.date == null) {
            System.out.println("Error: String constructor - date should not be null (-5)");
        }
    }

    public static abstract class AAgent implements Agent {
        public void reset() {}
        public void close() {}
        public String getName() {
            return getClass().getName();
        }
    }

    static String tn = null;

    public static class TestAgent1 extends AAgent {
        double sum = 0;
        int count = 0;
        TopicManager tm = TopicManagerSingleton.get();

        public TestAgent1() {
            tm.getTopic("Numbers").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            count++;
            sum += msg.asDouble;

            if (count % 5 == 0) {
                tm.getTopic("Sum").publish(new Message(sum));
                count = 0;
            }
        }

		@Override
		public Message getLastMessage() {
			// TODO Auto-generated method stub
			return null;
		}
    }

    public static class TestAgent2 extends AAgent {
        double sum = 0;
        TopicManager tm = TopicManagerSingleton.get();

        public TestAgent2() {
            tm.getTopic("Sum").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            sum = msg.asDouble;
        }

        public double getSum() {
            return sum;
        }

		@Override
		public Message getLastMessage() {
			// TODO Auto-generated method stub
			return null;
		}
    }

    public static void testDirectAgentCallback() {
        TopicManager tm = TopicManagerSingleton.get();
        TestAgent2 agent = new TestAgent2();
        tm.getTopic("Sum").publish(new Message(42.0));
        try { Thread.sleep(100); } catch (InterruptedException e) { }
        if (agent.getSum() != 42.0) {
            System.out.println("Direct agent callback test failed (-10)");
        } else {
            System.out.println("Direct agent callback test passed");
        }
        agent.close();
    }

    public static void testAgents() {
        TopicManager tm = TopicManagerSingleton.get();
        TestAgent1 a = new TestAgent1();
        TestAgent2 a2 = new TestAgent2();
        double sum = 0;
        for (int c = 0; c < 3; c++) {
            Topic num = tm.getTopic("Numbers");
            Random r = new Random();
            for (int i = 0; i < 5; i++) {
                int x = r.nextInt(1000);
                num.publish(new Message(x));
                sum += x;
            }
            double result = a2.getSum();
            if (result != sum) {
                System.out.println("Your code published a wrong result (-10)");
            }
        }
        a.close();
        a2.close();
    }

    public static boolean hasCycles(List<Node> graph) {
        for (Node node : graph) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    public static void testCycles() {
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");

        a.addEdge(b);
        b.addEdge(c);
        c.addEdge(d);
        d.addEdge(e);
        if (hasCycles(Arrays.asList(a, b, c, d, e, f))) {
            System.out.println("Test 1 failed");
        }
        e.addEdge(c);
        if (!hasCycles(Arrays.asList(a, b, c, d, e, f))) {
            System.out.println("Test 2 failed");
        }
    }

    public static void main(String[] args) {
        try {
            testParseRequest();
            testServer();
            testMessage();
            testDirectAgentCallback();
            testAgents();
            testCycles();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        System.out.println("done");
    }
}

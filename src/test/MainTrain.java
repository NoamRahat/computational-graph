package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import configs.Config;
import configs.MathExampleConfig;
import configs.Node;
import configs.ParallelAgent;
import graph.Agent;
import graph.Graph;
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


public class MainTrain { // RequestParser

    static String tn=null;

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

    public static void testMessage() {

        // Test String constructor
        String testString = "Hello";
        Message msgFromString = new Message(testString);
        if (!testString.equals(msgFromString.asText)) {
            System.out.println("Error: String constructor - asText does not match input string (-5)");
        }
        if (!java.util.Arrays.equals(testString.getBytes(), msgFromString.data)) {
            System.out.println("Error: String constructor - data does not match input string bytes (-5)");
        }
        if (!Double.isNaN(msgFromString.asDouble)) {
            System.out.println("Error: String constructor - asDouble should be NaN for non-numeric string (-5)");
        }
        if (msgFromString.date == null) {
            System.out.println("Error: String constructor - date should not be null (-5)");
        }

    }

    public static  abstract class AAgent implements Agent{
        public void reset() {}
        public void close() {}
        public String getName(){
            return getClass().getName();
        }
    }

    public static class TestAgent1 extends AAgent{

        double sum=0;
        int count=0;
        TopicManager tm=TopicManagerSingleton.get();

        public TestAgent1(){
            tm.getTopic("Numbers").subscribe(this);
        }

        public static class TestAgent2 implements Agent{

            public void reset() {
            }
            public void close() {
            }
            public String getName(){
                return getClass().getName();
            }

            @Override
            public void callback(String topic, Message msg) {
                tn=Thread.currentThread().getName();
            }

        }

        @Override
        public void callback(String topic, Message msg) {
            count++;
            sum+=msg.asDouble;

            if(count%5==0){
                tm.getTopic("Sum").publish(new Message(sum));
                count=0;
            }

        }
    }

    public static class TestAgent2 extends AAgent{

        double sum=0;
        TopicManager tm=TopicManagerSingleton.get();

        public TestAgent2(){
            tm.getTopic("Sum").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            sum=msg.asDouble;
        }

        public double getSum(){
            return sum;
        }
    }

    public static void testAgents(){
        TopicManager tm=TopicManagerSingleton.get();
        TestAgent1 a=new TestAgent1();
        TestAgent2 a2=new TestAgent2();
        double sum=0;
        for(int c=0;c<3;c++){
            Topic num=tm.getTopic("Numbers");
            Random r=new Random();
            for(int i=0;i<5;i++){
                int x=r.nextInt(1000);
                num.publish(new Message(x));
                sum+=x;
            }
            double result=a2.getSum();
            if(result!=sum){
                System.out.println("your code published a wrong result (-10)");
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

    public static void testCycles(){
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");

        a.addEdge(b);
        b.addEdge(c);
        c.addEdge(d);

        // Create a graph
        List<Node> graph = new ArrayList<>();
        graph.add(a);
        graph.add(b);
        graph.add(c);
        graph.add(d);

        // Check if the graph has cycles
        boolean hasCycles = hasCycles(graph);
        if(hasCycles)
            System.out.println("wrong answer for hasCycles when there are no cycles (-20)");

        d.addEdge(a);
        hasCycles = hasCycles(graph);
        if(!hasCycles)
            System.out.println("wrong answer for hasCycles when there is a cycle (-10)");

    }

    public static class GetAgent implements Agent{

        public Message msg;
        public GetAgent(String topic){
            TopicManagerSingleton.get().getTopic(topic).subscribe(this);
        }

        @Override
        public String getName() { return "Get Agent";}

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            this.msg=msg;
        }

        @Override
        public void close() {}
    }

    public static void testBinGraph(){
        TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c=new MathExampleConfig();
        c.create();

        GetAgent ga=new GetAgent("R3");

        Random r=new Random();
        int x=1+r.nextInt(100);
        int y=1+r.nextInt(100);
        tm.getTopic("A").publish(new Message(x));
        tm.getTopic("B").publish(new Message(y));
        double rslt=(x+y)*(x-y);

        if (Math.abs(rslt - ga.msg.asDouble)>0.05)
            System.out.println("your BinOpAgents did not produce the desired result (-20)");
    }

    public static void testTopicsGraph(){
        TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c=new MathExampleConfig();
        c.create();
        Graph g=new Graph();
        g.createFromTopics();

        if(g.size()!=8)
            System.out.println("the graph you created from topics is not in the right size (-10)");

        List<String> l=Arrays.asList("TA","TB","Aplus","Aminus","TR1","TR2","Amul","TR3");
        boolean b=true;
        for(Node n  : g){
            b&=l.contains(n.getName());
        }
        if(!b)
            System.out.println("the graph you created from topics has wrong names to Nodes (-10)");

        if (g.hasCycles())
            System.out.println("Wrong result in hasCycles for topics graph without cycles (-10)");

        GetAgent ga=new GetAgent("R3");
        tm.getTopic("A").addPublisher(ga); // cycle
        g.createFromTopics();

        if (!g.hasCycles())
            System.out.println("Wrong result in hasCycles for topics graph with a cycle (-10)");
    }

    public static void main(String[] args) {
        TopicManager tm=TopicManagerSingleton.get();
        int tc=Thread.activeCount();
        ParallelAgent pa=new ParallelAgent(new TestAgent2(), 10);
        tm.getTopic("A").subscribe(pa);

        if (Thread.activeCount()!=tc+1){
            System.out.println("your ParallelAgent does not open a thread (-10)");
        }


        tm.getTopic("A").publish(new Message("a"));
        try { Thread.sleep(100);} catch (InterruptedException e) {}
        if(tn==null){
            System.out.println("your ParallelAgent didn't run the wrapped agent callback (-20)");
        }else{
            if(tn.equals(Thread.currentThread().getName())){
                System.out.println("the ParallelAgent does not run the wrapped agent in a different thread (-10)");
            }
            String last=tn;
            tm.getTopic("A").publish(new Message("a"));
            try { Thread.sleep(100);} catch (InterruptedException e) {}
            if(!last.equals(tn))
                System.out.println("all messages should be processed in the same thread of ParallelAgent (-10)");
        }

        pa.close();

        testCycles();
        testBinGraph();
        testTopicsGraph();
        testMessage();
        testAgents();
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }

}

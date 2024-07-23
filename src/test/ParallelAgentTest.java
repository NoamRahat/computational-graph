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
import configs.ParallelAgent;
import servlets.CalculateServlet;
import servlets.CalculatorServlet;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.SubServlet;
import servlets.TestServlet;

public class ParallelAgentTest {
    public static void main(String[] args) {
        testParallelAgentInitialization();
        testCallbackMethod();
        testMessageProcessing();
        testThreadManagement();
        testCloseMethod();
    }

    public static void testParallelAgentInitialization() {
        Agent agent = new MockAgent("TestAgent");
        ParallelAgent parallelAgent = new ParallelAgent(agent, 10);

        if (parallelAgent != null) {
            System.out.println("testParallelAgentInitialization passed");
        } else {
            System.out.println("testParallelAgentInitialization failed");
        }
    }

    public static void testCallbackMethod() {
        Agent agent = new MockAgent("TestAgent");
        ParallelAgent parallelAgent = new ParallelAgent(agent, 10);

        Message msg = new Message("test message");
        parallelAgent.callback("TestTopic", msg);

        // Since we can't directly inspect the queue, we will verify indirectly
        // Ensure the message is eventually processed by the agent's callback
        try {
            Thread.sleep(100); // wait for the message to be processed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MockAgent mockAgent = (MockAgent) agent;
        if ("test message".equals(mockAgent.getLastMessage().asText())) {
            System.out.println("testCallbackMethod passed");
        } else {
            System.out.println("testCallbackMethod failed");
        }
    }

    public static void testMessageProcessing() {
        Agent agent = new MockAgent("TestAgent");
        ParallelAgent parallelAgent = new ParallelAgent(agent, 10);

        Message msg1 = new Message("first message");
        Message msg2 = new Message("second message");

        parallelAgent.callback("TestTopic", msg1);
        parallelAgent.callback("TestTopic", msg2);

        try {
            Thread.sleep(200); // wait for messages to be processed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MockAgent mockAgent = (MockAgent) agent;
        if ("second message".equals(mockAgent.getLastMessage().asText())) {
            System.out.println("testMessageProcessing passed");
        } else {
            System.out.println("testMessageProcessing failed");
        }
    }

    public static void testThreadManagement() {
        Agent agent = new MockAgent("TestAgent");
        ParallelAgent parallelAgent = new ParallelAgent(agent, 10);

        // This test is more conceptual since we can't directly inspect threads easily
        // We check that the thread seems to process messages and sleeps when there are none
        Message msg = new Message("test message");
        parallelAgent.callback("TestTopic", msg);

        try {
            Thread.sleep(100); // wait for the message to be processed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MockAgent mockAgent = (MockAgent) agent;
        if ("test message".equals(mockAgent.getLastMessage().asText())) {
            System.out.println("testThreadManagement passed");
        } else {
            System.out.println("testThreadManagement failed");
        }
    }

    public static void testCloseMethod() {
        Agent agent = new MockAgent("TestAgent");
        ParallelAgent parallelAgent = new ParallelAgent(agent, 10);

        parallelAgent.close();

        // Ensure that after closing, no new messages are processed
        Message msg = new Message("test message after close");
        parallelAgent.callback("TestTopic", msg);

        try {
            Thread.sleep(100); // wait for the message to be (not) processed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MockAgent mockAgent = (MockAgent) agent;
        if (mockAgent.getLastMessage() == null) {
            System.out.println("testCloseMethod passed");
        } else {
            System.out.println("testCloseMethod failed");
        }
    }

    static class MockAgent implements Agent {
        private String name;
        private Message lastMessage;

        public MockAgent(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            lastMessage = msg;
        }

        @Override
        public void close() {}

        public Message getLastMessage() {
            return lastMessage;
        }

        public void clearLastMessage() {
            lastMessage = null;
        }
    }
}

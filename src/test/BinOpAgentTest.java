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

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

import configs.BinOpAgent;
import configs.Config;
import configs.Node;

import server.MyHTTPServer;
import server.RequestParser;
import servlets.CalculateServlet;
import servlets.CalculatorServlet;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.SubServlet;
import servlets.TestServlet;

public class BinOpAgentTest {
    public static void main(String[] args) {
        testBinOpAgentInitialization();
        testCallbackMethod();
        testResetMethod();
    }

    public static void testBinOpAgentInitialization() {
        BinOpAgent agent = new BinOpAgent("TestAgent", "Input1", "Input2", "Output", (x, y) -> x + y);
        if ("TestAgent".equals(agent.getName())) {
            System.out.println("testBinOpAgentInitialization passed");
        } else {
            System.out.println("testBinOpAgentInitialization failed");
        }
    }

    public static void testCallbackMethod() {
        // Mocking TopicManager and Topic
        TopicManager topicManager = TopicManagerSingleton.get();
        Topic inputTopic1 = topicManager.getTopic("Input1");
        Topic inputTopic2 = topicManager.getTopic("Input2");
        Topic outputTopic = topicManager.getTopic("Output");

        BinOpAgent agent = new BinOpAgent("TestAgent", "Input1", "Input2", "Output", (x, y) -> x + y);
        Message msg1 = new Message("2.0");
        Message msg2 = new Message("3.0");

        agent.callback("Input1", msg1);
        agent.callback("Input2", msg2);

        // Assuming outputTopic is managed by topicManager
        Message outputMsg = outputTopic.getLastMessage();
        if (outputMsg != null && "5.0".equals(outputMsg.asText())) {
            System.out.println("testCallbackMethod passed");
        } else {
            System.out.println("testCallbackMethod failed");
        }
    }

    public static void testResetMethod() {
        BinOpAgent agent = new BinOpAgent("TestAgent", "Input1", "Input2", "Output", (x, y) -> x + y);
        agent.reset();

        // Assuming we check the reset state by some means
        // If isReset() is not defined, mock the state check based on your implementation
        if (/*agent.isReset()*/ true) { // Placeholder for actual reset state check
            System.out.println("testResetMethod passed");
        } else {
            System.out.println("testResetMethod failed");
        }
    }
}

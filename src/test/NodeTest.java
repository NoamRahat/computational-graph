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

public class NodeTest {
    public static void main(String[] args) {
        testNodeInitialization();
        testGettersAndSetters();
        testAddEdge();
        testHasCycles();
    }

    public static void testNodeInitialization() {
        Node node = new Node("TestNode");
        if ("TestNode".equals(node.getName())) {
            System.out.println("testNodeInitialization passed");
        } else {
            System.out.println("testNodeInitialization failed");
        }
    }

    public static void testGettersAndSetters() {
        Node node = new Node("TestNode");
        node.setName("NewName");
        if ("NewName".equals(node.getName())) {
            System.out.println("testGettersAndSetters name passed");
        } else {
            System.out.println("testGettersAndSetters name failed");
        }

        Message msg = new Message("test message");
        node.setMessage(msg);
        if ("test message".equals(node.getMessage().asText())) {
            System.out.println("testGettersAndSetters message passed");
        } else {
            System.out.println("testGettersAndSetters message failed");
        }
    }

    public static void testAddEdge() {
        Node node1 = new Node("Node1");
        Node node2 = new Node("Node2");
        node1.addEdge(node2);

        // Assuming a method getEdges() exists for testing purpose
        if (node1.getEdges().contains(node2)) {
            System.out.println("testAddEdge passed");
        } else {
            System.out.println("testAddEdge failed");
        }
    }

    public static void testHasCycles() {
        Node node1 = new Node("Node1");
        Node node2 = new Node("Node2");
        Node node3 = new Node("Node3");

        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node1);

        if (node1.hasCycles()) {
            System.out.println("testHasCycles passed");
        } else {
            System.out.println("testHasCycles failed");
        }
    }
}

package test;


import graph.Graph;
import configs.Node;

public class GraphTest {
    public static void main(String[] args) {
        System.out.println("Starting Graph tests...");
        testGraphInitialization();
        testHasCycles();
        testCreateFromTopics();
        System.out.println("Graph tests completed.");
    }

    public static void testGraphInitialization() {
        System.out.println("Running testGraphInitialization...");
        Graph graph = new Graph();
        if (graph != null) {
            System.out.println("testGraphInitialization passed");
        } else {
            System.out.println("testGraphInitialization failed");
        }
    }

    public static void testHasCycles() {
        System.out.println("Running testHasCycles...");
        Graph graph = new Graph();
        Node node1 = new Node("Node1");
        Node node2 = new Node("Node2");
        Node node3 = new Node("Node3");

        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node1);

        // Add nodes directly to the graph
        graph.add(node1);
        graph.add(node2);
        graph.add(node3);

        System.out.println("Testing for cycles...");
        if (graph.hasCycles()) {
            System.out.println("testHasCycles passed");
        } else {
            System.out.println("testHasCycles failed");
        }
    }

    public static void testCreateFromTopics() {
        System.out.println("Running testCreateFromTopics...");
        // Mocking the TopicManager
        Graph graph = new Graph();
        try {
            graph.createFromTopics();
            System.out.println("testCreateFromTopics passed");
        } catch (Exception e) {
            System.out.println("testCreateFromTopics failed: " + e.getMessage());
        }
    }
}

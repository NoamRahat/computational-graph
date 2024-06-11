package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import test.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node> {

    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        for (Node node : this) {
            if (!visited.contains(node) && hasCycles(node, visited, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycles(Node node, Set<Node> visited, Set<Node> stack) {
        if (stack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }
        visited.add(node);
        stack.add(node);
        for (Node neighbor : node.getEdges()) {
            if (hasCycles(neighbor, visited, stack)) {
                return true;
            }
        }
        stack.remove(node);
        return false;
    }

    public void createFromTopics() {
        TopicManager tm = TopicManagerSingleton.get();
        for (Topic topic : tm.getTopics()) {
            Node topicNode = new Node("T" + topic.name);
            this.add(topicNode);
            for (Agent agent : topic.subs) {
                Node agentNode = findOrCreateNode("A" + agent.getName());
                topicNode.addEdge(agentNode);
            }
            for (Agent agent : topic.pubs) {
                Node agentNode = findOrCreateNode("A" + agent.getName());
                agentNode.addEdge(topicNode);
            }
        }
    }

    private Node findOrCreateNode(String name) {
        for (Node node : this) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        Node newNode = new Node(name);
        this.add(newNode);
        return newNode;
    }
}

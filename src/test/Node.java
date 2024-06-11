package test;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.msg = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public Message getMessage() {
        return msg;
    }

    public void setMessage(Message msg) {
        this.msg = msg;
    }

    public void addEdge(Node node) {
        edges.add(node);
    }

    public boolean hasCycles() {
        return hasCycles(new ArrayList<>());
    }

    private boolean hasCycles(List<Node> visited) {
        if (visited.contains(this)) {
            return true;
        }
        visited.add(this);
        for (Node node : edges) {
            if (node.hasCycles(new ArrayList<>(visited))) {
                return true;
            }
        }
        return false;
    }
}

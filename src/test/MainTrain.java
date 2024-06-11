package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import test.TopicManagerSingleton.TopicManager;

public class MainTrain {

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
        testCycles();
        testBinGraph();
        testTopicsGraph();
        System.out.println("done");
    }

}

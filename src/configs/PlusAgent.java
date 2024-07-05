package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class PlusAgent implements Agent {
    private String name;
    private String[] subs;
    private String[] pubs;
    private double x, y;

    public PlusAgent(String[] subs, String[] pubs) {
        this.name = "PlusAgent";
        this.subs = subs;
        this.pubs = pubs;
        this.x = 0.0;
        this.y = 0.0;

        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(subs[1]).subscribe(this);
        tm.getTopic(pubs[0]).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(subs[0])) {
            x = msg.asDouble;
        } else if (topic.equals(subs[1])) {
            y = msg.asDouble;
        }

        if (!Double.isNaN(x) && !Double.isNaN(y)) {
            double result = x + y;
            TopicManager tm = TopicManagerSingleton.get();
            tm.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(subs[1]).unsubscribe(this);
        tm.getTopic(pubs[0]).removePublisher(this);
    }
}

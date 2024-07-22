package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent {
    private String name;
    private String[] subs;
    private String[] pubs;
    private double value;

    public IncAgent(String[] subs, String[] pubs) {
        this.name = "IncAgent";
        this.subs = subs;
        this.pubs = pubs;
        this.value = 0.0;

        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(pubs[0]).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        value = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(subs[0])) {
            value = msg.asDouble;
        }

        if (!Double.isNaN(value)) {
            double result = value + 1;
            TopicManager tm = TopicManagerSingleton.get();
            tm.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(pubs[0]).removePublisher(this);
    }

	@Override
	public Message getLastMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}

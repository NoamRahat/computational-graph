package graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    final List<Agent> subs;
    final List<Agent> pubs;
    public Message lastmessageTopic;

    Topic(String name){
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastmessageTopic = new Message("empty!");
    }

    public void subscribe(Agent a){
        subs.add(a);
    }

    public void unsubscribe(Agent a){
        subs.remove(a);
    }

    public void publish(Message m){
    	this.lastmessageTopic = m;
        for (Agent a : subs) {
            a.callback(name, m);
        }
    }

    public void addPublisher(Agent a){
        pubs.add(a);
    }

    public void removePublisher(Agent a){
        pubs.remove(a);
    }

    public Message getLastMessage(){
        return this.lastmessageTopic;
    }

    public String getName() {
        return name;
    }
}
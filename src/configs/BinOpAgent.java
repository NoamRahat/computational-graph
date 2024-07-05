package configs;

import java.util.function.BinaryOperator;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent {
    private String name;
    private String input1;
    private String input2;
    private String output;
    private BinaryOperator<Double> operation;
    private Double value1;
    private Double value2;

    public BinOpAgent(String name, String input1, String input2, String output, BinaryOperator<Double> operation) {
        this.name = name;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.operation = operation;
        this.value1 = 0.0;
        this.value2 = 0.0;

        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(input1).subscribe(this);
        tm.getTopic(input2).subscribe(this);
        tm.getTopic(output).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        value1 = 0.0;
        value2 = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(input1)) {
            value1 = msg.asDouble;
        } else if (topic.equals(input2)) {
            value2 = msg.asDouble;
        }

        if (value1 != null && value2 != null) {
            double result = operation.apply(value1, value2);
            TopicManager tm = TopicManagerSingleton.get();
            tm.getTopic(output).publish(new Message(result));
        }
    }

    @Override
    public void close() {
        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(input1).unsubscribe(this);
        tm.getTopic(input2).unsubscribe(this);
        tm.getTopic(output).removePublisher(this);
    }
}

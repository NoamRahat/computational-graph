package test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Random;

import test.TopicManagerSingleton.TopicManager;

/**
 * The `test1` class is a test class that contains various test methods for testing the behavior of the `Message` class,
 * `Agent` interface, and `TopicManager` class. It includes methods for testing message constructors, agent behavior,
 * agent subscription, agent callback, agent publish/subscribe, getting topics, and clearing topics.
 */
public class test1 {

    public static void test1main(String[] args) {
        testMessageConstructors();
        testAgentBehavior();
        System.out.println("done");
    }

    public static void testMessageConstructors() {
        testStringConstructor();
        testByteArrayConstructor();
        testDoubleConstructor();
    }

    public static void testStringConstructor() {
        String testString = "Hello";
        Message msgFromString = new Message(testString);

        assert testString.equals(msgFromString.asText) : "String constructor - asText does not match input string (-5)";
        assert java.util.Arrays.equals(testString.getBytes(), msgFromString.data) : "String constructor - data does not match input string bytes (-5)";
        assert Double.isNaN(msgFromString.asDouble) : "String constructor - asDouble should be NaN for non-numeric string (-5)";
        assert msgFromString.date != null : "String constructor - date should not be null (-5)";
    }

    public static void testByteArrayConstructor() {
        byte[] testBytes = {65, 66, 67}; // ABC in ASCII
        Message msgFromBytes = new Message(testBytes);

        assert java.util.Arrays.equals(testBytes, msgFromBytes.data) : "Byte array constructor - data does not match input bytes (-5)";
        assert "ABC".equals(msgFromBytes.asText) : "Byte array constructor - asText does not match input bytes (-5)";
        assert Double.isNaN(msgFromBytes.asDouble) : "Byte array constructor - asDouble should be NaN for non-numeric bytes (-5)";
        assert msgFromBytes.date != null : "Byte array constructor - date should not be null (-5)";
    }

    public static void testDoubleConstructor() {
        double testDouble = 123.45;
        Message msgFromDouble = new Message(testDouble);

        assert testDouble == msgFromDouble.asDouble : "Double constructor - asDouble does not match input double (-5)";
        assert "123.45".equals(msgFromDouble.asText) : "Double constructor - asText does not match input double (-5)";
        assert java.util.Arrays.equals("123.45".getBytes(), msgFromDouble.data) : "Double constructor - data does not match input double bytes (-5)";
        assert msgFromDouble.date != null : "Double constructor - date should not be null (-5)";
    }

    public static abstract class AAgent implements Agent {
        public void reset() {
            invokePublicMethod("reset");
        }

        public void close() {
            invokePublicMethod("close");
        }

        private void invokePublicMethod(String methodName) {
            try {
                Method[] methods = this.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName) && Modifier.isPublic(method.getModifiers())) {
                        method.invoke(this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return getClass().getName();
        }
    }

    public class TestAgent implements Agent {
        private String lastTopic;
        private Message lastMessage;

        @Override
        public void callback(String topic, Message msg) {
            this.lastTopic = topic;
            this.lastMessage = msg;
        }

        public String getLastTopic() {
            return lastTopic;
        }

        public Message getLastMessage() {
            return lastMessage;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }
    }

    public static class TestAgent1 extends AAgent {
        double sum = 0;
        int count = 0;
        TopicManager tm = TopicManagerSingleton.get();

        public TestAgent1() {
            tm.getTopic("Numbers").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            count++;
            sum += msg.asDouble;

            if (count % 5 == 0) {
                tm.getTopic("Sum").publish(new Message(sum));
                count = 0;
            }
        }
    }

    public static class TestAgent2 extends AAgent {
        double sum = 0;
        TopicManager tm = TopicManagerSingleton.get();

        public TestAgent2() {
            tm.getTopic("Sum").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            sum = msg.asDouble;
        }

        public double getSum() {
            return sum;
        }
    }

    public static void testAgentBehavior() {
        testAgentSubscription();
        testAgentCallback();
        testAgentPublishSubscribe();
        testGetTopics();
        testClearTopics();
    }

    public static void testAgentSubscription() {
        TopicManager tm = TopicManagerSingleton.get();
        Agent a1 = new TestAgent1();
        Topic t1 = tm.getTopic("topic1");

        t1.subscribe(a1);
        assert t1.subs.contains(a1) : "Agent a1 was not subscribed to topic t1 (-5)";

        t1.unsubscribe(a1);
        assert !t1.subs.contains(a1) : "Agent a1 was not unsubscribed from topic t1 (-5)";
    }

    public static void testAgentCallback() {
        MainTrain mainTrain = new MainTrain();
        MainTrain.TestAgent testAgent = mainTrain.new TestAgent();
        TopicManager tm = TopicManagerSingleton.get();
        Topic t1 = tm.getTopic("topic1");
        Message m = new Message("test");

        testAgent.callback("topic1", m);
        assert "topic1".equals(testAgent.getLastTopic()) : "callback was not called with the correct topic (-5)";
        assert m.equals(testAgent.getLastMessage()) : "callback was not called with the correct message (-5)";
    }

    public static void testAgentPublishSubscribe() {
        TopicManager tm = TopicManagerSingleton.get();
        Agent a1 = new TestAgent1();
        Agent a2 = new TestAgent2();
        Topic t1 = tm.getTopic("Numbers");

        t1.subscribe(a1);
        t1.publish(new Message(1.0));
        t1.publish(new Message(2.0));
        t1.publish(new Message(3.0));
        t1.publish(new Message(4.0));
        t1.publish(new Message(5.0));

        Topic t2 = tm.getTopic("Sum");
        t2.subscribe(a2);

        assert ((TestAgent2) a2).getSum() == 15.0 : "Sum did not match expected value after publishing 5 numbers (-5)";
    }

    public static void testGetTopics() {
        TopicManager tm = TopicManagerSingleton.get();
        Topic t1 = tm.getTopic("topic1");
        Topic t2 = tm.getTopic("topic2");
        Collection<Topic> topics = tm.getTopics();

        assert topics.contains(t1) && topics.contains(t2) : "getTopics did not return all topics (-5)";
    }

    public static void testClearTopics() {
        TopicManager tm = TopicManagerSingleton.get();
        Topic t1 = tm.getTopic("topic1");
        Topic t2 = tm.getTopic("topic2");

        tm.clear();
        Collection<Topic> topics = tm.getTopics();

        assert topics.isEmpty() : "clear did not remove all topics (-5)";
    }
}
public static void test1() {
    testMessageConstructors();
    testAgentBehavior();
    System.out.println("done");
}
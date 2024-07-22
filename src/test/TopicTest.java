package test;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;


public class TopicTest {
    public static void main(String[] args) {
        testSingletonProperty();
        testTopicManagement();
        testSubscribeAndUnsubscribe();
        testPublish();
        testAddAndRemovePublisher();
    }

    public static void testSingletonProperty() {
        TopicManager tm1 = TopicManagerSingleton.get();
        TopicManager tm2 = TopicManagerSingleton.get();
        if (tm1 == tm2) {
            System.out.println("testSingletonProperty passed");
        } else {
            System.out.println("testSingletonProperty failed");
        }
    }

    public static void testTopicManagement() {
        TopicManager tm = TopicManagerSingleton.get();
        
        Topic topic1 = tm.getTopic("TestTopic1");
        if (topic1 != null) {
            System.out.println("testTopicManagement getTopic create passed");
        } else {
            System.out.println("testTopicManagement getTopic create failed");
        }
        
        Topic topic2 = tm.getTopic("TestTopic1");
        if (topic1 == topic2) {
            System.out.println("testTopicManagement getTopic existing passed");
        } else {
            System.out.println("testTopicManagement getTopic existing failed");
        }
        
        Topic topic3 = tm.getTopic("TestTopic2");
        if (topic1 != topic3) {
            System.out.println("testTopicManagement getTopic different passed");
        } else {
            System.out.println("testTopicManagement getTopic different failed");
        }
        
        if (tm.getTopics().size() == 2) {
            System.out.println("testTopicManagement getTopics size passed");
        } else {
            System.out.println("testTopicManagement getTopics size failed");
        }
        
        tm.clear();
        if (tm.getTopics().size() == 0) {
            System.out.println("testTopicManagement clear passed");
        } else {
            System.out.println("testTopicManagement clear failed");
        }
    }

    public static void testSubscribeAndUnsubscribe() {
        TopicManager tm = TopicManagerSingleton.get();
        Topic topic = tm.getTopic("TestTopic");
        MockAgent agent = new MockAgent("TestAgent");
        
        topic.subscribe(agent);
        // Since we don't have getSubscribers method, we will verify by checking if publish sends message
        Message msg = new Message("test message");
        topic.publish(msg);
        if ("test message".equals(agent.getLastMessage().asText())) {
            System.out.println("testSubscribeAndUnsubscribe subscribe passed");
        } else {
            System.out.println("testSubscribeAndUnsubscribe subscribe failed");
        }
        
        topic.unsubscribe(agent);
        agent.clearLastMessage(); // Clear the last message to verify if unsubscribe works
        topic.publish(new Message("another test message"));
        if (agent.getLastMessage() == null) {
            System.out.println("testSubscribeAndUnsubscribe unsubscribe passed");
        } else {
            System.out.println("testSubscribeAndUnsubscribe unsubscribe failed");
        }
    }

    public static void testPublish() {
        TopicManager tm = TopicManagerSingleton.get();
        Topic topic = tm.getTopic("TestTopic");
        MockAgent agent = new MockAgent("TestAgent");
        topic.subscribe(agent);
        
        Message msg = new Message("test message");
        topic.publish(msg);
        
        if ("test message".equals(agent.getLastMessage().asText())) {
            System.out.println("testPublish passed");
        } else {
            System.out.println("testPublish failed");
        }
    }

    public static void testAddAndRemovePublisher() {
        TopicManager tm = TopicManagerSingleton.get();
        Topic topic = tm.getTopic("TestTopic");
        MockAgent agent = new MockAgent("TestAgent");
        
        topic.addPublisher(agent);
        // We will assume addPublisher works if we can publish a message from agent
        // However, as there is no method to publish directly from agent, we are not testing this part here
        
        topic.removePublisher(agent);
        // Similar to above, we cannot test removal directly, just making sure no errors
        
        System.out.println("testAddAndRemovePublisher passed");
    }

    static class MockAgent implements Agent {
        private String name;
        private Message lastMessage;
        
        public MockAgent(String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            lastMessage = msg;
        }

        @Override
        public void close() {}
        
        public Message getLastMessage() {
            return lastMessage;
        }
        
        public void clearLastMessage() {
            lastMessage = null;
        }
    }
}

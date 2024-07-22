package test;

import graph.Agent;
import graph.Message;


public class AgentTest {
    public static void main(String[] args) {
        testAgentInterfaceMethods();
    }

    public static void testAgentInterfaceMethods() {
        MockAgent agent = new MockAgent("TestAgent");
        if ("TestAgent".equals(agent.getName())) {
            System.out.println("testAgentInterfaceMethods getName passed");
        } else {
            System.out.println("testAgentInterfaceMethods getName failed");
        }
        
        agent.reset();
        if (agent.isReset()) {
            System.out.println("testAgentInterfaceMethods reset passed");
        } else {
            System.out.println("testAgentInterfaceMethods reset failed");
        }
        
        Message msg = new Message("test message");
        agent.callback("TestTopic", msg);
        if ("test message".equals(agent.getLastMessage().asText())) {
            System.out.println("testAgentInterfaceMethods callback passed");
        } else {
            System.out.println("testAgentInterfaceMethods callback failed");
        }
        
        agent.close();
        if (agent.isClosed()) {
            System.out.println("testAgentInterfaceMethods close passed");
        } else {
            System.out.println("testAgentInterfaceMethods close failed");
        }
    }

    static class MockAgent implements Agent {
        private String name;
        private boolean reset;
        private boolean closed;
        private Message lastMessage;
        
        public MockAgent(String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void reset() {
            reset = true;
        }

        @Override
        public void callback(String topic, Message msg) {
            lastMessage = msg;
        }

        @Override
        public void close() {
            closed = true;
        }
        
        public boolean isReset() {
            return reset;
        }
        
        public boolean isClosed() {
            return closed;
        }
        
        public Message getLastMessage() {
            return lastMessage;
        }
    }
}

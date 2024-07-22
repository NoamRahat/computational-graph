package test;

import graph.Message;

public class MessageTest {
    public static void main(String[] args) {
        testConstructors();
        testConversions();
    }



    public static void testConstructors() {
        byte[] data = {1, 2, 3};
        Message msg1 = new Message(data);
        if (msg1.date != null) {
            System.out.println("testConstructors with byte[] passed");
        } else {
            System.out.println("testConstructors with byte[] failed");
        }
        
        Message msg2 = new Message("test");
        if (msg2.date != null) {
            System.out.println("testConstructors with String passed");
        } else {
            System.out.println("testConstructors with String failed");
        }
        
        Message msg3 = new Message(123.45);
        if (msg3.date != null) {
            System.out.println("testConstructors with double passed");
        } else {
            System.out.println("testConstructors with double failed");
        }
    }

    public static void testConversions() {
        Message msg1 = new Message("test");
        if ("test".equals(msg1.asText())) {
            System.out.println("testConversions asText passed");
        } else {
            System.out.println("testConversions asText failed");
        }
        
        Message msg2 = new Message(123.45);
        if (123.45 == msg2.asDouble) {
            System.out.println("testConversions asDouble passed");
        } else {
            System.out.println("testConversions asDouble failed");
        }
        
        Message msg3 = new Message("not a number");
        if (Double.isNaN(msg3.asDouble)) {
            System.out.println("testConversions invalid double passed");
        } else {
            System.out.println("testConversions invalid double failed");
        }
    }
}


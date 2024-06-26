package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private final Agent agent;
    private final BlockingQueue<MessageTask> queue;
    private volatile boolean running;
    private final Thread workerThread;

    private static class MessageTask {
        String topic;
        Message msg;

        MessageTask(String topic, Message msg) {
            this.topic = topic;
            this.msg = msg;
        }
    }

    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.running = true;

        this.workerThread = new Thread(() -> {
            while (running || !queue.isEmpty()) {
                try {
                    MessageTask task = queue.take();
                    agent.callback(task.topic, task.msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        this.workerThread.start();
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }

    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new MessageTask(topic, msg));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        running = false;
        workerThread.interrupt();
        agent.close();
    }
}

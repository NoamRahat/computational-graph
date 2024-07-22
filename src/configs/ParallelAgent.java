package configs;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import graph.Agent;
import graph.Message;

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
            try {
                while (running || !queue.isEmpty()) {
                    MessageTask task = queue.take();
                    agent.callback(task.topic, task.msg);
                }
            } catch (InterruptedException e) {
                // Allow thread to exit
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
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        agent.close();
    }

	@Override
	public Message getLastMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}

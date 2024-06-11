package test;

import test.TopicManagerSingleton.TopicManager;

public class MainTrain { // just a simple tests about the parallel agent to get you going...

    static String tn=null;

    public static class TestAgent1 implements Agent{

        public void reset() {
        }
        public void close() {
        }
        public String getName(){
            return getClass().getName();
        }

        @Override
        public void callback(String topic, Message msg) {
            tn=Thread.currentThread().getName();
        }

    }

    public static void main(String[] args) {
        TopicManager tm=TopicManagerSingleton.get();
        int tc=Thread.activeCount();
        ParallelAgent pa=new ParallelAgent(new TestAgent1(), 10);
        tm.getTopic("A").subscribe(pa);

        if (Thread.activeCount()!=tc+1){
            System.out.println("your ParallelAgent does not open a thread (-10)");
        }


        tm.getTopic("A").publish(new Message("a"));
        try { Thread.sleep(100);} catch (InterruptedException e) {}
        if(tn==null){
            System.out.println("your ParallelAgent didn't run the wrapped agent callback (-20)");
        }else{
            if(tn.equals(Thread.currentThread().getName())){
                System.out.println("the ParallelAgent does not run the wrapped agent in a different thread (-10)");
            }
            String last=tn;
            tm.getTopic("A").publish(new Message("a"));
            try { Thread.sleep(100);} catch (InterruptedException e) {}
            if(!last.equals(tn))
                System.out.println("all messages should be processed in the same thread of ParallelAgent (-10)");
        }

        pa.close();


        System.out.println("done");
    }
}

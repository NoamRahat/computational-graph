package test;

import java.util.Random;


public class MainTrain {
    
    public static void main(String[] args) {
        int c=Thread.activeCount();
        GenericConfig gc=new GenericConfig();
        gc.setConfFile("C:\\Users\\neriy\\simple.conf"); // change to the exact loaction where you put the file.
        gc.create();

        if(Thread.activeCount()!=c+2){
            System.out.println("the configuration did not create the right number of threads (-10)");
        }
        
        double result[]={0.0};

        TopicManagerSingleton.get().getTopic("D").subscribe(new Agent() {
            
            @Override
            public String getName() {
                return "";
            }
            
            @Override
            public void reset() {
            }
            
            @Override
            public void callback(String topic, Message msg) {
                result[0]=msg.asDouble;                
            }
            
            @Override
            public void close() {
            }
            
        });

        Random r=new Random();
        for(int i=0;i<9;i++){
            int x,y;
            x=r.nextInt(1000);
            y=r.nextInt(1000);
            TopicManagerSingleton.get().getTopic("A").publish(new Message(x));
            TopicManagerSingleton.get().getTopic("B").publish(new Message(y));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

            if(result[0]!=x+y+1){
                System.out.println("your agents did not produce the desierd result (-10)");
            }
        }

        gc.close();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}

        if(Thread.activeCount()!=c){
            System.out.println("your code did not close all threads (-10)");
        }

        System.out.println("done");
        
    }
}

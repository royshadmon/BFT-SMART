package bftsmart.demo.counter;

import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.util.Properties;
import java.util.Queue;

public class ClientConsumer implements Runnable {
    protected Queue<Double> consume_queue;
    protected String consumeFromConfig;

    public ClientConsumer(Properties conf, String consumeFromConfig, Queue<Double> consume_q) {
        this.consume_queue = consume_q;
        this.consumeFromConfig = consumeFromConfig;
    }

    @Override
    public void run() {
        ServiceProxy readCounterProxy = new ServiceProxy(2, this.consumeFromConfig);
        ByteArrayOutputStream out = new ByteArrayOutputStream(4);
        int inc = -1;
        System.out.println("SENDING REQUEST");
        try {
            new DataOutputStream(out).writeDouble(inc); // sending 0 is a read request
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double newValue;
        while (true) {

            System.out.println("IN CLIENT CONSUMER SERVICE");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            byte[] reply = readCounterProxy.invokeOrdered(out.toByteArray());


//            byte[] reply = (inc == 0) ?
//                    readCounterProxy.invokeUnordered(out.toByteArray()) :
//                    readCounterProxy.invokeOrdered(out.toByteArray()); //magic happens here

            if (reply != null) {
                try {
                    newValue = new DataInputStream(new ByteArrayInputStream(reply)).readDouble();
                    System.out.println("RECEIVED REPLY CLIENT CONSUMER " + newValue);
                    consume_queue.offer(newValue); // offer returns true or false on success, add will throw an exception if it fails
                } catch (IOException e) {
                    System.out.println("NO VALUE RECEIVED");
                    throw new RuntimeException(e);
                }
//                System.out.println(", returned value: " + newValue);
                // Check if returned value is a new value and add new value to queue


            }
        }
    }
}

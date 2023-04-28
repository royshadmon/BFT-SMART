package bftsmart.demo.counter;

import bftsmart.demo.counter.helperFunctions.ProcessLayerConfig;
import bftsmart.tom.ServiceProxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Queue;

public class ClientProducer implements Runnable {
    protected String produceConfig;
    protected Queue<Double> consume_queue;
    public ClientProducer(Properties conf, String produceToConfig, Queue<Double> consume_q) {
        this.produceConfig = produceToConfig;
        this.consume_queue = consume_q;
    }

    @Override
    public void run() {
        System.out.println("IN CLIENT PRODUCER RUN");
        try {
            process_response();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void process_response() throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            System.out.println("SIZE OF QUEUE IN CLIENT PRODUCER " + this.consume_queue.size());
        }
//        if (config_output == null) {
//            new ProcessLayerConfig(config_output);
//        }
//        System.out.println("HEREHERHERHERHERHERHERHEHREHREHRHERHERHEHREHRE " + config_output);
//        double val;
//        ServiceProxy counterProxy_forward = new ServiceProxy(Integer.parseInt(id), config_output);
//        while (true) {
//            if (consume_queue.isEmpty()) {
//                try {
//                    Thread.sleep(1000);
//                    continue;
//                } catch (InterruptedException e) {
//                    System.out.println("Thread interrupted");
//                }
//
//            } else {
//                try {
//                    val = queue.poll(); // ensures that we process data in FIFO
//                    ByteArrayOutputStream out_forward = new ByteArrayOutputStream(4);
//                    new DataOutputStream(out_forward).writeDouble(val);
//                    byte[] reply_forward = (val == 0) ?
//                            counterProxy_forward.invokeUnordered(out_forward.toByteArray()) :
//                            counterProxy_forward.invokeOrdered(out_forward.toByteArray());
//                    System.out.println("FORWARD REPLY " + reply_forward);
//                } catch (IOException | NumberFormatException e) {
//                    System.out.println("IN EXCEPTION");
//                    counterProxy_forward.close();
//                }
//
//            }
//        }
    }
}

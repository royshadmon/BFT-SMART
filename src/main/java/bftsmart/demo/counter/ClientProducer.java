package bftsmart.demo.counter;

import bftsmart.demo.counter.helperFunctions.ProcessLayerConfig;
import bftsmart.tom.ServiceProxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.Queue;

public class ClientProducer implements Runnable {
    private final String produce_to;
//    protected String produceConfig;
    protected Queue<Double> consume_queue;
//    public ClientProducer(Properties conf, String produceToConfig, Queue<Double> consume_q) {
    public ClientProducer(Properties client_config, Queue<Double> consume_q) {
        this.produce_to = client_config.getProperty("produce_to");
        this.consume_queue = consume_q;

//        this.produceConfig = produceToConfig;
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
        double val;
        int iter= 1;
        ServiceProxy counterProxy_forward = new ServiceProxy(10, this.produce_to);
        System.out.println("OUTPUTTING TO " + this.produce_to);
        while (true) {
            System.out.println("IN CLIENT PRODUCER SERVICE");
            System.out.println("SIZE OF QUEUE IN CLIENT PRODUCER " + this.consume_queue.size());
            if (consume_queue.isEmpty()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                }

            } else {
                try {
                    val = consume_queue.poll(); // ensures that we process data in FIFO
                    System.out.println("OUT VALUE IS " + val);
                    val += iter;
                    ReturnObject ro = new ReturnObject(1, val);
                    ByteArrayOutputStream out_forward = new ByteArrayOutputStream();
                    ObjectOutputStream objOutputStream = new ObjectOutputStream(out_forward);
                    objOutputStream.writeObject(ro);
                    objOutputStream.flush(); // ensures all data is written to ByteArrayOutputStream
//                    new DataOutputStream(out_forward).writeDouble(val);
//                    DataOutputStream dataOutputStream = new DataOutputStream(out_forward);
//                    dataOutputStream.writeObject(ro);
//                    byte[] reply_forward = (val == 0) ?
//                            counterProxy_forward.invokeUnordered(out_forward.toByteArray()) :
//                            counterProxy_forward.invokeOrdered(out_forward.toByteArray());
                    byte[] reply_forward =
                            counterProxy_forward.invokeOrdered(out_forward.toByteArray());
                    System.out.println("FORWARD REPLY " + reply_forward);
                } catch (IOException | NumberFormatException e) {
                    System.out.println("IN EXCEPTION");
                    counterProxy_forward.close();
                }

            }
        }



//            Thread.sleep(1000);
//            System.out.println("SIZE OF QUEUE IN CLIENT PRODUCER " + this.consume_queue.size());

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

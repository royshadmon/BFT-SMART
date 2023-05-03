package bftsmart.demo.counter;

import bftsmart.demo.counter.helperFunctions.ProcessLayerConfig;
import bftsmart.tom.ServiceProxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

public class ClientProducer implements Runnable {
    private final String produce_to;
    private final Map<String, Queue<Double>> consume_queue_map;
    private final String[] stream_ids;
    private final int produce_rate;
    private final String produce_stream_id;

    //    protected Queue<Double> consume_queue;
    public ClientProducer(Properties client_config, Map<String, Queue<Double>> consume_queue_map) {
        this.produce_to = client_config.getProperty("produce_to");
        this.consume_queue_map = consume_queue_map;
        this.stream_ids = client_config.getProperty("consume_from.stream_ids").split("\\,");
        this.produce_rate = Integer.parseInt(client_config.getProperty("produce.rate"));
        this.produce_stream_id = client_config.getProperty("produce.stream_id");
    }

    @Override
    public void run() {
        System.out.println("IN CLIENT PRODUCER RUN");
        try {
            process_response();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void process_response() throws InterruptedException, IOException {
        double val;
        int iter= 1;
        int sleep_time = this.produce_rate * 1000;
        ServiceProxy counterProxy_forward = new ServiceProxy(10, this.produce_to);
        System.out.println("OUTPUTTING TO " + this.produce_to);

        while (true) {
            boolean ready_to_process = true;
            System.out.println("IN CLIENT PRODUCER SERVICE");
            Thread.sleep(sleep_time);

            for (int i=0; i< stream_ids.length; i++) {
                System.out.println("QUEUE IN CLIENT PRODUCER " + this.stream_ids[i]);
                //                System.out.println("QUEUE IN CLIENT PRODUCER " + this.stream_ids[i] + " SIZE " + this.consume_queue_map.get(stream_ids[i]).size());
                if (consume_queue_map.get(stream_ids[i]).isEmpty()) {
                    ready_to_process = false;
                    System.out.println("CLIENT PRODUCER CANNOT PROCESS DATA, NO DATA IN QUEUE");

                    break;
                }
            }
            if (ready_to_process) {
                System.out.println("CLIENT PRODUCER PROCESSING DATA, DATA IN QUEUE");
                double[] func_params = new double[stream_ids.length];
                for (int i=0; i< stream_ids.length; i++) {
                    func_params[i] = this.consume_queue_map.get(this.stream_ids[i]).poll();
//                    val = consume_queue.poll(); // ensures that we process data in FIFO
                    System.out.println("OUT VALUE IS " + func_params[i]);
//                    val += iter;

                }
                RequestObject ro = new RequestObject(this.produce_stream_id, func_params);
                ByteArrayOutputStream out_forward = new ByteArrayOutputStream();
                ObjectOutputStream objOutputStream = new ObjectOutputStream(out_forward);
                objOutputStream.writeObject(ro);
                objOutputStream.flush(); // ensures all data is written to ByteArrayOutputStream
                byte[] reply_forward =
                        counterProxy_forward.invokeOrdered(out_forward.toByteArray());
                System.out.println("FORWARD REPLY " + reply_forward);
            }

        }
    }
}

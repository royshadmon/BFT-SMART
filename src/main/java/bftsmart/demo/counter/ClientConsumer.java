package bftsmart.demo.counter;

import bftsmart.demo.counter.helperFunctions.ReadFromCSV;
import bftsmart.tom.ServiceProxy;

import javax.xml.crypto.Data;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ClientConsumer implements Runnable {
    private final int client_id;
    private final String consume_from;
    private final String stream_id;
    private final Properties client_config;
    private Map<String, Queue<Double>> consume_queue_map;
    private final boolean isSource_data;
    private int consume_from_column_id;
    private final float consume_from_interval;
//    protected Queue<Double> consume_queue;

    private int last_seq_num_process;
    protected Queue<Double> src_data_queue;


//    public ClientConsumer(Properties conf, String consumeFromConfig, Queue<Double> consume_q) {
    public ClientConsumer(Properties client_config, Map<String,Queue<Double>> consume_q_map, int stream_index) {
        this.client_config = client_config;
        this.last_seq_num_process = 0;
        this.client_id = Integer.parseInt(client_config.getProperty("client_id").split("\\,")[stream_index]);
        this.consume_from = client_config.getProperty("consume_from").split("\\,")[stream_index]; // where to consume_from
        System.out.println(client_config);
        this.consume_from_interval = Float.parseFloat(client_config.getProperty("consume_from.interval").split("\\,")[stream_index]);
        this.isSource_data = Boolean.parseBoolean(client_config.getProperty("consume_from.isSource_data").split("\\,")[stream_index]); // if true, signifies that the consume_from field is a CSV file
        this.stream_id = client_config.getProperty("consume_from.stream_ids").split("\\,")[stream_index];
        System.out.println("CLIENT CONSUMER CONFIG FILE " + this.isSource_data);
        if (this.isSource_data)
            this.consume_from_column_id = Integer.parseInt(client_config.getProperty("consume_from.column_id").split("\\,")[stream_index]);

        this.consume_queue_map = consume_q_map;

//        this.consumeFromConfig = consumeFromConfig;
    }

    public void process_source_data() throws InterruptedException{
        System.out.println("IN CLIENT CONSUMER SOURCE DATA ");
        int seq_number = 1;
        double newValue;
        float sleep_time = this.consume_from_interval*1000;
        this.src_data_queue = ReadFromCSV.read_csv(this.consume_from, this.consume_from_column_id);
        while (true) {
            Thread.sleep((long) sleep_time);
            newValue = src_data_queue.poll(); // get data from source data
//            this.consume_queue.offer(newValue); // put data in consumer_queue
            this.consume_queue_map.computeIfAbsent(this.stream_id, k -> new ConcurrentLinkedQueue<>()).offer(newValue);

            //            seq_number += 1;
        }
    }

    public void process_from_replica_set() throws InterruptedException, IOException {
        System.out.println("IN CLIENT CONSUMER REPLICA SET ");
        double newValue;
        float sleep_time = this.consume_from_interval*1000;
        ServiceProxy readCounterProxy = new ServiceProxy(this.client_id, this.consume_from);
        int seq_num = 1;

        String r_id = this.client_config.getProperty("replica_set_id");
        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/royshadmon/Desktop/"+r_id+"-CONSUME.txt", true));
        LocalDateTime start, end;
        boolean response_received = true;
        start = LocalDateTime.now();
        double[] a = new double[1];
        while (true) {
            if (response_received)
                start = LocalDateTime.now();
                response_received = false;
                // prepare client request message
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                RequestObject ro = new RequestObject(this.stream_id, seq_num, a);
                ObjectOutputStream objOutputStream = new ObjectOutputStream(out);
                objOutputStream.writeObject(ro);
                objOutputStream.flush(); // ensures all data is written to ByteArrayOutputStream

            System.out.println("IN CLIENT CONSUMER SERVICE REQUESTING SEQ " + seq_num);

            Thread.sleep((long) sleep_time);


            // get most recent committed value
            byte[] reply = readCounterProxy.invokeUnordered(out.toByteArray());


            if (reply != null) {
                try {
//                    newValue = new DataInputStream(new ByteArrayInputStream(reply)).readDouble();
                    ReturnObject received_data = (ReturnObject) new ObjectInputStream(new ByteArrayInputStream(reply)).readObject();
                    System.out.println("RECEIVED (" + received_data.sequence_number +") REPLY CLIENT CONSUMER " + received_data.value);
                    // wait for correct sequence number
                    if (seq_num == received_data.sequence_number && received_data.value != null) {
                        System.out.println("I AM ABLE TO PROCESS REQUEST");
                        this.consume_queue_map.computeIfAbsent(this.stream_id, k -> new ConcurrentLinkedQueue<>()).offer(received_data.value);
//                        consume_queue.offer(received_data.value); // offer returns true or false on success, add will throw an exception if it fails
                        seq_num += 1;
                        end = LocalDateTime.now();
                        long totalTime = Duration.between(start, end).toMillis();
                        bw.write(totalTime + "\n");
                        bw.flush();
                        response_received = true;
                    }

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("NO VALUE RECEIVED");
//                    throw new RuntimeException(e);
                }
                //System.out.println(", returned value: " + newValue);
                // Check if returned value is a new value and add new value to queue

            }
        }
    }

    @Override
    public void run() {
        double newValue;
        float sleep_time = this.consume_from_interval*1000;
        // if data is source data and from a csv file
        System.out.println("IS SOURCE DATA " + this.isSource_data);
        if (this.isSource_data) {
            try {
                process_source_data();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        // Consume inputs from a replica set
        else {
            try {
                process_from_replica_set();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}


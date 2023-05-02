package bftsmart.demo.counter;

import bftsmart.demo.counter.helperFunctions.ReadFromCSV;
import bftsmart.tom.ServiceProxy;

import java.io.*;
import java.util.Properties;
import java.util.Queue;

public class ClientConsumer implements Runnable {
    private final int client_id;
    private final String consume_from;
    private boolean isSource_data = false;
    private int consume_from_column_id;
    private final int consume_from_interval;
    protected Queue<Double> consume_queue;

    protected Queue<Double> src_data_queue;


//    public ClientConsumer(Properties conf, String consumeFromConfig, Queue<Double> consume_q) {
    public ClientConsumer(Properties client_config, Queue<Double> consume_q) {
        this.client_id = Integer.parseInt(client_config.getProperty("client_id"));
        this.consume_from = client_config.getProperty("consume_from"); // where to consume_from
        System.out.println(client_config);
        this.consume_from_interval = Integer.parseInt(client_config.getProperty("consume_from_interval"));
        this.isSource_data = Boolean.parseBoolean(client_config.getProperty("consume_from.isSource_data")); // if true, signifies that the consume_from field is a CSV file
        System.out.println("CLIENT CONSUMER CONFIG FILE " + this.isSource_data);
        if (this.isSource_data)
            this.consume_from_column_id = Integer.parseInt(client_config.getProperty("consume_from.column_id"));

        this.consume_queue = consume_q;
//        this.consumeFromConfig = consumeFromConfig;
    }

    public void process_source_data() throws InterruptedException{
        System.out.println("IN CLIENT CONSUMER SOURCE DATA ");
        double newValue;
        long sleep_time = this.consume_from_interval*1000;
        this.src_data_queue = ReadFromCSV.read_csv(this.consume_from, this.consume_from_column_id);
        while (true) {
            Thread.sleep(sleep_time);
            newValue = src_data_queue.poll(); // get data from source data
            this.consume_queue.offer(newValue); // put data in consumer_queue
        }
    }

    public void process_from_replica_set() throws InterruptedException, IOException {
        System.out.println("IN CLIENT CONSUMER REPLICA SET ");
        double newValue;
        long sleep_time = this.consume_from_interval*1000;
        ServiceProxy readCounterProxy = new ServiceProxy(this.client_id, this.consume_from);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReturnObject ro = new ReturnObject(-1, -1);
        ObjectOutputStream objOutputStream = new ObjectOutputStream(out);
        objOutputStream.writeObject(ro);
        objOutputStream.flush(); // ensures all data is written to ByteArrayOutputStream


        while (true) {

            System.out.println("IN CLIENT CONSUMER SERVICE");
            try {
                Thread.sleep(sleep_time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // get most recent committed value
            byte[] reply = readCounterProxy.invokeUnordered(out.toByteArray());


            if (reply != null) {
                try {
//                    newValue = new DataInputStream(new ByteArrayInputStream(reply)).readDouble();
                    ReturnObject received_data = (ReturnObject) new ObjectInputStream(new ByteArrayInputStream(reply)).readObject();
                    System.out.println("RECEIVED (" + received_data.sequence_number +") REPLY CLIENT CONSUMER " + received_data.value);
                    consume_queue.offer(received_data.value); // offer returns true or false on success, add will throw an exception if it fails
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("NO VALUE RECEIVED");
                    throw new RuntimeException(e);
                }
                //System.out.println(", returned value: " + newValue);
                // Check if returned value is a new value and add new value to queue


            }
        }
    }

    @Override
    public void run() {
        double newValue;
        long sleep_time = this.consume_from_interval*1000;
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

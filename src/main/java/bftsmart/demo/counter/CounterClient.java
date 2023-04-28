/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package bftsmart.demo.counter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bftsmart.demo.counter.helperFunctions.ProcessLayerConfig;
import bftsmart.tom.ServiceProxy;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;


/**
 * Example client that updates a BFT replicated service (a counter).
 * 
 * @author alysson
 */
public class CounterClient implements Runnable {
    public static Queue<Double> consume_queue;

    protected String consume_config_output;
    protected String produce_config_output;

    public static int client_id;
    protected Properties conf;
    protected String config_input;
    protected String produce_to;


    public CounterClient(String consume_config, String produce_config) throws IOException, InterruptedException {
        System.out.println("IN COUNTER CLIENT CONSTRUCTOR");
        this.consume_queue = new ConcurrentLinkedQueue<>();
        this.consume_config_output = consume_config;
        this.produce_config_output = produce_config;

        ProcessLayerConfig consume_info = new ProcessLayerConfig(this.produce_config_output);
        this.conf = consume_info.getConfig();

        // get filepath of replica set to consume from
        this.config_input = conf.getProperty("consume_from");
        client_id = Integer.parseInt(conf.getProperty("client_id"));
        this.produce_to = conf.getProperty("produce_to");


        // Start background thread to produce data
//        ClientProducer c = new ClientProducer(conf, produce_to, consume_queue);
//        Thread t = new Thread(c);
//        t.start(); // Starts the run() function
//
//        ClientConsumer cc = new ClientConsumer(conf, config_input, consume_queue);
//        Thread t2 = new Thread(cc);
//        t2.start();

//        ServiceProxy readCounterProxy = new ServiceProxy(client_id, config_input);
//        ByteArrayOutputStream out = new ByteArrayOutputStream(4);
//        int inc = 0;
//        System.out.println("SENDING REQUEST");
//        new DataOutputStream(out).writeDouble(0); // sending 0 is a read request
//        while (true && t.isAlive()) {
//            Thread.sleep(2000);
//            byte[] reply = (inc == 0) ?
//                    readCounterProxy.invokeUnordered(out.toByteArray()) :
//                    readCounterProxy.invokeOrdered(out.toByteArray()); //magic happens here
//
//            if (reply != null) {
//                System.out.println("RECEIVED REPLY YAY");
//                double newValue = new DataInputStream(new ByteArrayInputStream(reply)).readDouble();
//                System.out.println(", returned value: " + newValue);
//                // Check if returned value is a new value and add new value to queue
//                consume_queue.offer((double) newValue); // offer returns true or false on success, add will throw an exception if it fails

//        if (reply != null) {
//            double newValue = new DataInputStream(new ByteArrayInputStream(reply)).readDouble();
//            System.out.println(", returned value: " + newValue);
//            queue.offer((double) newValue); // offer returns true or false on success, add will throw an exception if it fails

//            }
//        }
//        t.interrupt();

    }


//    public static void main(String[] args) throws IOException {
//        if (args.length < 2) {
//            System.out.println("Usage: java ... CounterClient <process id> <increment> [<number of operations>]");
//            System.out.println("       if <increment> equals 0 the request will be read-only");
//            System.out.println("       default <number of operations> equals 1000");
//            System.exit(-1);
//        }
//
////        Properties config = new Properties();
////        try {
////            FileInputStream inputStream = new FileInputStream("/Users/royshadmon/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties");
////            config.load(inputStream);
////            inputStream.close();
////            System.out.println("PROPERTY " + config.getProperty("replica_name"));
////            String functionName = config.getProperty("function");
////            String[] parts = functionName.split("\\.");
////            String className = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
////            String methodName = parts[parts.length - 1];
////            Class<?> cls = Class.forName(className);
////
////            String argsString = config.getProperty("function.args");
////            String[] func_args = argsString.split(",");
////            Class<?>[] argTypes = new Class<?>[func_args.length];
////            Object[] argValues = new Object[func_args.length];
////
////            for (int i = 0; i < func_args.length; i++) {
////                String[] arg_parts = func_args[i].split(":");
////                argTypes[i] = getClass(arg_parts[0]);
////                try {
////                    argValues[i] = arg_parts[1];
////                } catch (Exception e) {
////                    argValues[i] = null;
////                }
////
////
////            }
////
////
////            Method method = cls.getDeclaredMethod(methodName, argTypes);
////            Object obj = cls.newInstance();
////            method.invoke(obj,2.0);
//////            System.out.println("HALFED RESULT " + result);
////
////        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
////            System.out.println("Failed to load config");
////        } catch (InstantiationException e) {
////            throw new RuntimeException(e);
////        }
//
//
//        // Create concurrent linked queue
//
//        client_id = args[0];
//        String config_input = args[3];
//        config_output = args[4];
//        String data_file_name = System.getenv("HOME") + "/Centauri/data/may-june-2018.csv";
//        Queue<Double> src_data_queue = read_csv(data_file_name);
//
//
//        ServiceProxy counterProxy = new ServiceProxy(Integer.parseInt(args[0]), config_input);
//        System.out.println(args[0]);
//        try {
//
//            int inc = Integer.parseInt(args[1]);
//            int numberOfOps = (args.length > 2) ? Integer.parseInt(args[2]) : 1000;
//
//            // Start the background thread
//            CounterClient c = new CounterClient();
//            Thread t = new Thread(c);
//            t.start(); // Starts the run() function
//            double val;
//            for (int i = 0; i < src_data_queue.size(); i++) {
//                System.out.println("INC IS " + inc);
//                ByteArrayOutputStream out = new ByteArrayOutputStream(4);
//                val = src_data_queue.poll();
//                System.out.println("VAL IS " + val + " Out is " + out.toString());
//                new DataOutputStream(out).writeDouble(val);
//
//                System.out.print("Invocation " + i);
//                byte[] reply = (inc == 0) ?
//                        counterProxy.invokeUnordered(out.toByteArray()) :
//                        counterProxy.invokeOrdered(out.toByteArray()); //magic happens here
//
//                if (reply != null) {
//                    double newValue = new DataInputStream(new ByteArrayInputStream(reply)).readDouble();
//                    System.out.println(", returned value: " + newValue);
//                    queue.offer((double) newValue); // offer returns true or false on success, add will throw an exception if it fails
//
//                } else {
//                    System.out.println(", ERROR! Exiting.");
//                    break;
//                }
//            }
//        } catch (IOException | NumberFormatException e) {
//            counterProxy.close();
//        }
//    }

//    public static void process_data() {
//
//    }


//    public static void process_response(String id, String config_output) {
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
//    }

//    @Override
//    public void run(String clientConfigFile) {
//        System.out.println("IN COUNTER CLIENT RUN");
//
//        process_response(client_id, config_output);
//    }

//    public static Queue<Double> read_csv(String csvFilename) {
//        // create a csv reader
//        Queue<Double> src_data_queue = null;
//        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilename));
//             CSVReader csvReader = new CSVReader(reader)) {
//
//            // read one record at a time
//            String[] record;
//            // initialize queue
//            src_data_queue = new ConcurrentLinkedQueue<>();
//            while ((record = csvReader.readNext()) != null) {
//                try {
////                    System.out.println("User[" + String.join(", ", record) + "]");
//                    src_data_queue.offer(Double.parseDouble(record[5]));
//                }catch (NumberFormatException e) {
//                    System.out.println("Skipping column header");
//                }
//
//            }
//
//        } catch (IOException | CsvValidationException ex) {
//            ex.printStackTrace();
//        }
//        return src_data_queue;
//    }

//    private static Class<?> getClass(String className) throws ClassNotFoundException {
//        switch (className) {
//            case "int":
//                return int.class;
//            case "double":
//                return double.class;
//            default:
//                return Class.forName(className);
//        }
//    }

//    @Override
    public void run() {
        System.out.println("IN COUNTER CLIENT RUN2222");
        // Start background thread to produce data
        ClientProducer c = new ClientProducer(this.conf, this.produce_to, this.consume_queue);
        Thread t = new Thread(c);
        t.start(); // Starts the run() function

        ClientConsumer cc = new ClientConsumer(this.conf, this.config_input, this.consume_queue);
        Thread t2 = new Thread(cc);
        t2.start();
    }
}


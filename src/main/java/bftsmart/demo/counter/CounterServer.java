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

import bftsmart.demo.counter.helperFunctions.ProcessLayerConfig;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import org.bouncycastle.cert.ocsp.Req;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Example replica that implements a BFT replicated service (a counter).
 * If the increment > 0 the counter is incremented, otherwise, the counter
 * value is read.
 * 
 * @author alysson
 */

public final class CounterServer extends DefaultSingleRecoverable  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String replica_set_config;
    private final String config_abs_path;
    private final int server_id;
    private final Method replica_computation;
    private final Object obj;
    private final int num_func_args;
    private double counter = 0;
    private int iterations = 1;

    public static Map<String, Map<Integer, Double>> data_history;
    
//    public CounterServer(int id, String replicaConfigFile, String clientConfigFile) {
    public CounterServer(int server_id, ProcessLayerConfig clientConfig) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Properties clientConfigFile = clientConfig.getConfig();
        this.server_id = server_id;
        this.replica_set_config = clientConfigFile.getProperty("produce_to");
        this.config_abs_path = clientConfigFile.getProperty("config_abs_path");

        System.out.println("USING CONFIG FILE " + this.replica_set_config);
    	new ServiceReplica(server_id, this, this, replica_set_config, this.config_abs_path);

        System.out.println("REPLICA ID " + server_id);
        int currentLeaderId = this.stateManager.execManager.getCurrentLeader();
        System.out.println("CURRENT LEADER IS " + currentLeaderId);
        System.out.println("REPLICA ID IS " + this.config.processId);

        // set up replica computation function
        this.replica_computation = clientConfig.get_function_interface();
        this.obj = clientConfig.get_function_obj();
        this.num_func_args = clientConfig.get_num_func_args();
        this.data_history = new HashMap<>();
    }
            
    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
//        iterations++;
//        System.out.println("INVOKE UNORDERED -- (" + iterations + ") Counter current value: " + counter);
        try {
            RequestObject received_data = (RequestObject) new ObjectInputStream(new ByteArrayInputStream(command)).readObject();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.println("INVOKE UNORDERED -- REQUEST FOR SEQ (" + received_data.seq_num );
            // Get the requested value and return null if it does not exist
            Double requested_value = data_history.containsKey(received_data.stream_id) ? data_history.get(received_data.stream_id).getOrDefault(received_data.seq_num, null) : null;
            System.out.println("IN UNORDERED STREAM ID KEYS " + data_history.get(received_data.stream_id).containsKey(received_data.seq_num));
//            System.out.println("IN INVOKE " + received_data.sequence_number + " UNORDERED RETURNING VALUE " + requested_value);
            ReturnObject ro = new ReturnObject(received_data.stream_id, received_data.seq_num, requested_value); // TODO
            ObjectOutputStream objOutputStream = new ObjectOutputStream(out);
            objOutputStream.writeObject(ro);
            objOutputStream.flush(); // ensures all data is written to ByteArrayOutputStream

            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        } catch (ClassNotFoundException e) {
            System.out.println("CLASS NOT FOUND EXCEPTION");
            throw new RuntimeException(e);
        }
    }
  
    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        try {
            RequestObject received_data = (RequestObject) new ObjectInputStream(new ByteArrayInputStream(command)).readObject();
            logger.debug("RECEIVED ORDERED MESSAGE " +received_data );
            System.out.println("RECEIVED ORDERED MESSAGE " + received_data.values);
            System.out.println("NUM FUNC ARGS " + this.num_func_args);
            double[] func_inputs = new double[this.num_func_args];
            for (int i=0; i<received_data.values.length; i++) {
                func_inputs[i] = received_data.values[i];
                System.out.println("RECIEVED OREDED " + received_data.values[i]);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOutputStream = new ObjectOutputStream(out);
//
//            func_inputs[0] = received_data.value;

            counter = (double) this.replica_computation.invoke(this.obj, func_inputs);
            System.out.println("IN APP EXECUTE ORDERED STORING ITERATION " + iterations + "DATA " + counter);
            this.data_history.computeIfAbsent(received_data.stream_id, k -> new HashMap<>()).put(iterations, counter);
            System.out.println("SENDING  " + counter);
            
            System.out.println("(" + iterations + ") Counter was processed. Current value = " + counter);


            ReturnObject ro = new ReturnObject(received_data.stream_id, iterations, counter);
            objOutputStream.writeObject(ro);
            objOutputStream.flush(); // ensures all data is written to ByteArrayOutputStream
            iterations++;
            return out.toByteArray();

        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if(args.length < 2) {
            System.out.println("Use: java CounterServer <serverID> <abs-path-to-layer-property>");
            System.exit(-1);
        }
        int server_id = Integer.parseInt(args[0]);
        String config_file_path = args[1];
        ProcessLayerConfig client_config = new ProcessLayerConfig(config_file_path); // process config file

        new CounterServer(server_id, client_config);
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public void installSnapshot(byte[] state) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            counter = in.readInt();
            in.close();
            bis.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Error deserializing state: "
                    + e.getMessage());
        }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeDouble(counter);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ioe) {
            System.err.println("[ERROR] Error serializing state: "
                    + ioe.getMessage());
            return "ERROR".getBytes();
        }
    }

    public double computation(double v) {
        return v/2;
    }

    public static double half(double[] a) {
        if (a.length > 1) {
            System.out.println("TOO MANY INPUT PARAMETERS TO FUNCTION DOUBLE");
        }
        return a[0]/2;
    }

}


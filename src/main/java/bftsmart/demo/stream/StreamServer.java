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
package bftsmart.demo.stream;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.util.concurrent.TimeUnit; // used to make the replica server sleep;
import org.uncommons.maths.random.ExponentialGenerator; //used for the random number generator
import org.uncommons.maths.random.MersenneTwisterRNG;
import java.util.Random;
import java.util.*;
import java.io.FileInputStream;

/**
 * Example replica that implements a BFT replicated service (a stream).
 * If the increment > 0 the counter is incremented, otherwise, the counter
 * value is read.
 *
 * @author alysson
 */

public final class StreamServer extends DefaultSingleRecoverable  {

    private int counter = 0;
    private int iterations = 0;
    int seed = 56854;
//    Random rng = new MersenneTwisterRNG();
    Random num = new Random(seed);

    private static Properties prop = new Properties();
    private static String fileName = "config/system.config";
    private static double rate;
//    double rate = Double.valueOf(prop.getProperty("system.servers.rate"));
    private static ExponentialGenerator gen;


//    double latencyRate = Double.valueOf(rate);
//    double latencyRate = 0.5;

//    ExponentialGenerator gen = new ExponentialGenerator(0.5, num);


    public StreamServer(int id) {
//        Properties prop = new Properties();
//        String fileName = "config/system.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        }
        catch (IOException ex) {
            System.out.println("IN IOEXCEPTION READING CONFIG FILE");
        }
//        Random num = new Random();
        rate = Double.valueOf(prop.getProperty("system.servers.rate"));
        gen = new ExponentialGenerator(0.5, num);

    	new ServiceReplica(id, this, this, "");
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        iterations++;
        System.out.println("(" + iterations + ") Counter current value: " + counter);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(10);
            new DataOutputStream(out).writeInt(counter);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        iterations++;
        try {
            int increment = new DataInputStream(new ByteArrayInputStream(command)).readInt();
            counter += increment;

            System.out.println("(" + iterations + ") Counter was incremented. Current value = " + counter);
//            System.out.println("RATE IS " + rate);
            System.out.println();
            double sleepTime = gen.nextValue();
            System.out.println("RANDOM NUMBER IS " + Double.toString(sleepTime));
            long sT = (long) sleepTime;
            try {
                TimeUnit.SECONDS.sleep(sT);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream out = new ByteArrayOutputStream(10);
//            new DataOutputStream(out).writeInt(counter);

            new DataOutputStream(out).writeInt(increment);
            return out.toByteArray();
        } catch (IOException ex) {
            System.err.println("Invalid request received!");
            return new byte[0];
        }
    }

    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java CounterServer <processId>");
            System.exit(-1);
        }
        new StreamServer(Integer.parseInt(args[0]));
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
            out.writeInt(counter);
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
}

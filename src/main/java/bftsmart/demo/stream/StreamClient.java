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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.*;
import java.io.File;
import java.util.Scanner; // used to read from CSV file
//import java.util.Timer;
import java.math.BigInteger;

import bftsmart.tom.ServiceProxy;

/**
 * Example client that updates a BFT replicated service (a stream).
 *
 * @author alysson
 */
public class StreamClient {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java ... StreamClient <process id> <increment> [<number of operations>]");
            System.out.println("       if <increment> equals 0 the request will be read-only");
            System.out.println("       default <number of operations> equals 1000");
            System.exit(-1);
        }
        final long nanoSecondsPerSecond = 1000000;
        long stopWatchStartTime = 0;
        int numSimulations = 5;
        int inc = Integer.parseInt(args[1]);
        ServiceProxy counterProxy = new ServiceProxy(Integer.parseInt(args[0]));
        System.out.println("HELLO IS THE BUILD WORKING?");
        System.out.println(System.getProperty("user.dir"));

        int sensor = 1;
        for (int i=0; i<numSimulations; i++) {
            File myFile = new File("src/main/java/bftsmart/demo/stream/wind-data.csv");
            Scanner sc = new Scanner(myFile);
            sc.useDelimiter(",");


            while (sc.hasNext()) {
//                System.out.println(sc.next());
                try {
                    double value = Double.valueOf(sc.next());
//                    double value = 12.12;
                    System.out.println("THE VALUE IS " + Double.toString(value));
//                    int inc = Integer.parseInt(args[1]);
//                    int numberOfOps = (args.length > 2) ? Integer.parseInt(args[2]) : 1000;

//                    for (int i = 0; i < numberOfOps; i++) {

                    stopWatchStartTime = System.nanoTime();

                    ByteArrayOutputStream out = new ByteArrayOutputStream(10);
                    System.out.println("HERE1");
                    new DataOutputStream(out).writeDouble(value);
                    System.out.println("HERE2");
//                    System.out.print("Invocation " + i);
                    byte[] reply = (inc == 0)?
                            counterProxy.invokeUnordered(out.toByteArray()):
                            counterProxy.invokeOrdered(out.toByteArray()); //magic happens here
                    System.out.println("HERE3");
                    if(reply != null) {
                        float newValue = new DataInputStream(new ByteArrayInputStream(reply)).readFloat();
                        System.out.println(", returned value: " + Double.toString(newValue));
                    } else {
                        System.out.println(", ERROR! Exiting.");
                        break;
                    }
//                    }
                } catch(IOException | NumberFormatException e){
                    counterProxy.close();
                }
                sensor += 1;
                if (sensor == 5) {
                    long elapsedTime;
                    elapsedTime = System.nanoTime() - stopWatchStartTime;
                    System.out.println("ELAPSED TIME IS " + Long.toString(elapsedTime/nanoSecondsPerSecond));
                    System.out.println("SENSOR IS 5");
                    sensor = 1;

                }
            }
            sc.close();


        }


//        try {
//
//            int inc = Integer.parseInt(args[1]);
//            int numberOfOps = (args.length > 2) ? Integer.parseInt(args[2]) : 1000;
//
//            for (int i = 0; i < numberOfOps; i++) {
//
//                ByteArrayOutputStream out = new ByteArrayOutputStream(4);
//                new DataOutputStream(out).writeInt(inc);
//
//                System.out.print("Invocation " + i);
//                byte[] reply = (inc == 0)?
//                        counterProxy.invokeUnordered(out.toByteArray()):
//                	counterProxy.invokeOrdered(out.toByteArray()); //magic happens here
//
//                if(reply != null) {
//                    int newValue = new DataInputStream(new ByteArrayInputStream(reply)).readInt();
//                    System.out.println(", returned value: " + newValue);
//                } else {
//                    System.out.println(", ERROR! Exiting.");
//                    break;
//                }
//            }
//        } catch(IOException | NumberFormatException e){
//            counterProxy.close();
//        }
    }
}

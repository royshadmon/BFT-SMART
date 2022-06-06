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
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Scanner; // used to read from CSV file
//import java.util.Timer;
import java.math.BigInteger;
import java.io.FileInputStream;

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
        int numSimulations = 3;
        int inc = Integer.parseInt(args[1]);


        ServiceProxy counterProxy = new ServiceProxy(Integer.parseInt(args[0]));

        double latencies[];
        latencies = new double[numSimulations];

        Properties prop = new Properties();
        String fileName = "config/system.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        }
//        catch (FileNotFoundException ex) {
//            System.out.println("IN FILE NOT FOUND READING CONFIG FILE");
//        }
        catch (IOException ex) {
            System.out.println("IN IOEXCEPTION READING CONFIG FILE");
        }
        System.out.println("Printing THE CONFIG FILE");
        int f = Integer.valueOf(prop.getProperty("system.servers.f"));
        System.out.println(prop.getProperty("system.servers.num"));
        double latencyRate = Double.valueOf(prop.getProperty("system.servers.rate"));
        System.out.println("RATE IS ");
        System.out.println(latencyRate);
//        String outputFile = "~\\latency-" + Integer.toString(f) + "-" + Double.toString(latencyRate) + ".txt";
        String outputFile = "latency-" + Integer.toString(f) + "-" + Double.toString(latencyRate) + ".txt";
        FileWriter myWriter = new FileWriter(outputFile);
        BufferedWriter bWriter = new BufferedWriter(myWriter);






//        bWriter = new BufferedWriter(myWriter);

        int sensor = 1;



        for (int i=0; i<numSimulations; i++) {
            File myFile = new File("src/main/java/bftsmart/demo/stream/wind-data-test.csv");
            Scanner sc = new Scanner(myFile);
            sc.useDelimiter(",");

            bWriter.write("NextSim");
            bWriter.newLine();

            while (sc.hasNext()) {
                try {

                    double value = Double.valueOf(sc.next());
                    System.out.println("THE VALUE IS " + Double.toString(value));

                    stopWatchStartTime = System.nanoTime();

                    ByteArrayOutputStream out = new ByteArrayOutputStream(10);
                    new DataOutputStream(out).writeDouble(value);
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
                    elapsedTime = (System.nanoTime() - stopWatchStartTime)/nanoSecondsPerSecond;
                    System.out.println("ELAPSED TIME IS " + Long.toString(elapsedTime));
                    sensor = 1;
                    // We need to write to file the time or to an array
                    bWriter.write(String.valueOf(elapsedTime));
                    bWriter.newLine();
                }
            }
            sc.close();


        }
        System.out.println("CLOSING WRITER");
        bWriter.close();
        System.out.println("CLOSING WRITER2");
        myWriter.close();
        System.out.println("CLOSING WRITER3");
        System.exit(0);

    }
}

package bftsmart.demo.counter.helperFunctions;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReadFromCSV {
    public static Queue<Double> read_csv(String csvFilename, int column_id) {
    // create a csv reader
        Queue<Double> src_data_queue = null;
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilename));
             CSVReader csvReader = new CSVReader(reader)) {

            // read one record at a time
            String[] record;
            // initialize queue
            src_data_queue = new ConcurrentLinkedQueue<>();
            while ((record = csvReader.readNext()) != null) {
                try {
                    src_data_queue.offer(Double.parseDouble(record[column_id]));
                }catch (NumberFormatException e) {
                    System.out.println("Skipping column header");
                }

            }

        } catch (IOException | CsvValidationException ex) {
            ex.printStackTrace();
        }
        return src_data_queue;
    }
}

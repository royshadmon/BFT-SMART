package bftsmart.demo.counter;

import java.io.Serializable;

public class ReturnObject implements Serializable {
    protected int sequence_number;
    protected double value;

    public ReturnObject(int seq_num, double value) {
        this.sequence_number = seq_num;
        this.value = value;
    }
}

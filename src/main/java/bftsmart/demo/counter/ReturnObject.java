package bftsmart.demo.counter;

import java.io.Serializable;

public class ReturnObject implements Serializable {
    protected int sequence_number;
    protected Double value;
    protected String stream_id;

    public ReturnObject(String stream_id, int seq_num, Double value) {
        this.sequence_number = seq_num;
        this.value = value;
        this.stream_id = stream_id;
    }
}

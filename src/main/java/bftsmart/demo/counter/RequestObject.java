package bftsmart.demo.counter;

import java.io.Serializable;

public class RequestObject implements Serializable {

    final double[] values;
    final String stream_id;
    final Integer seq_num;

    public RequestObject(String stream_id, Integer seq_num, double[] values) {
        this.values = values;
        this.stream_id = stream_id;
        this.seq_num = seq_num;
    }
}

package bftsmart.demo.counter;

import java.io.Serializable;

public class RequestObject implements Serializable {

    final double[] values;
    final String stream_id;

    public RequestObject(String stream_id, double[] values) {
        this.values = values;
        this.stream_id = stream_id;
    }
}

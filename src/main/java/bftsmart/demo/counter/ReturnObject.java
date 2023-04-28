package bftsmart.demo.counter;

import java.io.Serializable;

public class ReturnObject implements Serializable {
    protected int iteration;
    protected double value;

    public ReturnObject(int iteration, double value) {
        this.iteration = iteration;
        this.value = value;
    }
}

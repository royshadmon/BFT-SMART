package bftsmart.demo.counter.replicaFunctions;

public class testFunction {
    public static double half(double[] a) {
        if (a.length > 1) {
            System.out.println("TOO MANY INPUT PARAMETERS TO FUNCTION DOUBLE");
        }
        return a[0]/2;
    }

    public static double sum(double[] a) {
        if (a.length != 2) {
            System.out.println("INPUT PARAMETERS ARE WRONG FOR FUNCTION SUM");
        }
        return a[0] + a[1];
    }
}

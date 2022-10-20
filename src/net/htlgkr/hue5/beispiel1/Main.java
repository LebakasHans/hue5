package net.htlgkr.hue5.beispiel1;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        ReciprocalArraySum reciprocalArraySum = new ReciprocalArraySum();
        double[] sum = IntStream.range(1,5).mapToDouble(d -> d).toArray();
        double result = ReciprocalArraySum.parManyTaskArraySum(sum,1);
        System.out.println("Sum: " + result);
    }
}
package net.eaustria;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        ReciprocalArraySum reciprocalArraySum = new ReciprocalArraySum();
        double[] sum = IntStream.range(1,45).mapToDouble(d -> d).toArray();
        double result = ReciprocalArraySum.parManyTaskArraySum(sum,3);
        System.out.println("Sum: " + result);
    }
}
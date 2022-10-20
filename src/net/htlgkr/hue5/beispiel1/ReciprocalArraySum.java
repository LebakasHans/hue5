package net.htlgkr.hue5.beispiel1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */

public class ReciprocalArraySum {
    /**
     * Default constructor.
     */
    public ReciprocalArraySum() {
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;
        for (double d : input) {
            sum += 1/d;
        }
        return sum;
    }


    /**
     * This class stub can be filled in to implement the body of each task
     * created to perform reciprocal array sum in parallel.
     */
    private static class ReciprocalArraySumTask extends RecursiveTask<Double> {
        /**
         * Starting index for traversal done by this task.
         */
        private final int startIndexInclusive;
        /**
         * Ending index for traversal done by this task.
         */
        private final int endIndexExclusive;
        /**
         * Input array to reciprocal sum.
         */
        private final double[] input;
        private static int SEQUENTIAL_THRESHOLD = 4;

        /**
         * Constructor.
         * @param setStartIndexInclusive Set the starting index to begin
         *        parallel traversal at.
         * @param setEndIndexExclusive Set ending index for parallel traversal.
         * @param setInput Input values
         */
        ReciprocalArraySumTask(final int setStartIndexInclusive,
                               final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }

        @Override
        protected Double compute() {
            // TODO: Implement Thread forking on Threshold value. (If size of
            // array smaller than threshold: compute sequentially else, fork
            // 2 new threads
                ReciprocalArraySumTask left = null;
                ReciprocalArraySumTask right = null;
                var arr = Arrays.copyOfRange(input, startIndexInclusive, endIndexExclusive);
                if (arr.length < SEQUENTIAL_THRESHOLD) {
                    left = new ReciprocalArraySumTask(startIndexInclusive, endIndexExclusive - (endIndexExclusive - startIndexInclusive) / 2, input);
                    right = new ReciprocalArraySumTask(endIndexExclusive - (endIndexExclusive - startIndexInclusive) / 2, endIndexExclusive, input);

                }else {
                    return seqArraySum(arr);
                }
            invokeAll(left, right);
            return left.join() + right.join();
        }
    }


    /**
     * number of tasks to compute the reciprocal array sum.
     *
     * @param input Input array
     * @param numTasks The number of tasks to create
     * @return The sum of the reciprocals of the array input
     */
    protected static double parManyTaskArraySum(final double[] input,
                                                final int numTasks) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(numTasks);
        double sum = forkJoinPool.invoke(new ReciprocalArraySumTask(0, input.length-1, input));
        return sum;
    }
}
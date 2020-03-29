package service.util;

import java.util.ArrayList;

public class MeanStd {
    public static double computeMeanDouble(ArrayList<Double> numbers) {
        double total = 0.0;
        for (Double num : numbers) {
            total += num;
        }
        return total / (double) numbers.size();
    }

    public static double computeStdDouble(ArrayList<Double> numbers, Double mean) {
        double squaredSum = 0;
        for (Double num : numbers) {
            squaredSum += Math.pow(num - mean, 2);
        }
        return Math.pow(squaredSum / numbers.size(), 0.5d);
    }

    public static Double computeMeanLong(ArrayList<Long> numbers) {
        Double total = 0.0;
        for (long num : numbers) {
            total += num;
        }
        return total / (double) numbers.size();
    }

    public static double computeStdLong(ArrayList<Long> numbers, Double mean) {
        double squaredSum = 0;
        for (Long num : numbers) {
            squaredSum += Math.pow(num - mean, 2);
        }
        return Math.pow(squaredSum / numbers.size(), 0.5d);
    }
}

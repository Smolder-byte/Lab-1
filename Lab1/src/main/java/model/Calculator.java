package model;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.distribution.TDistribution;

public class Calculator {
    public static double geometricMean(double[] data) {
        return StatUtils.geometricMean(data);
    }
    
    public static double arithmeticMean(double[] data) {
        return StatUtils.mean(data);
    }
    
    public static double standardDeviation(double[] data) {
        return Math.sqrt(StatUtils.variance(data));
    }
    
    public static double range(double[] data) {
        return StatUtils.max(data) - StatUtils.min(data);
    }
    
    public static int count(double[] data) {
        return data.length;
    }
    
    public static double variationCoefficient(double[] data) {
        return (standardDeviation(data) / arithmeticMean(data)) * 100;
    }
    
    public static double[] confidenceInterval(double[] data) {
        double mean = arithmeticMean(data);
        double stdDev = standardDeviation(data);
        int n = data.length;
        
        TDistribution tDist = new TDistribution(n - 1);
        double criticalValue = tDist.inverseCumulativeProbability(0.975);
        double margin = criticalValue * stdDev / Math.sqrt(n);
        
        return new double[]{mean - margin, mean + margin};
    }
    
    public static double variance(double[] data) {
        return StatUtils.variance(data);
    }
    
    public static double min(double[] data) {
        return StatUtils.min(data);
    }
    
    public static double max(double[] data) {
        return StatUtils.max(data);
    }
    
    public static double covariance(double[] data1, double[] data2) {
        return new Covariance().covariance(data1, data2);
    }
}
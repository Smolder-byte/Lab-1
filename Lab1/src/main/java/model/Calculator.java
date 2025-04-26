package model;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.distribution.TDistribution;

public class Calculator {
    // 1. Среднее геометрическое
    public static double geometricMean(double[] data) {
        return StatUtils.geometricMean(data);
    }
    
    // 2. Среднее арифметическое
    public static double arithmeticMean(double[] data) {
        return StatUtils.mean(data);
    }
    
    // 3. Стандартное отклонение
    public static double standardDeviation(double[] data) {
        return Math.sqrt(StatUtils.variance(data));
    }
    
    // 4. Размах
    public static double range(double[] data) {
        DescriptiveStatistics stats = new DescriptiveStatistics(data);
        return stats.getMax() - stats.getMin();
    }
    
    // 5. Количество элементов
    public static int count(double[] data) {
        return data.length;
    }
    
    // 6. Коэффициент вариации (%)
    public static double variationCoefficient(double[] data) {
        return (standardDeviation(data) / arithmeticMean(data)) * 100;
    }
    
    // 7. Доверительный интервал (95%)
    public static double[] confidenceInterval(double[] data) {
        double mean = arithmeticMean(data);
        double stdDev = standardDeviation(data);
        int n = data.length;
        
        TDistribution tDist = new TDistribution(n - 1);
        double criticalValue = tDist.inverseCumulativeProbability(0.975);
        double margin = criticalValue * stdDev / Math.sqrt(n);
        
        return new double[]{mean - margin, mean + margin};
    }
    
    // 8. Дисперсия
    public static double variance(double[] data) {
        return StatUtils.variance(data);
    }
    
    // 9. Минимум
    public static double min(double[] data) {
        return StatUtils.min(data);
    }
    
    // 10. Максимум
    public static double max(double[] data) {
        return StatUtils.max(data);
    }
    
    // 11. Ковариация (для пар выборок)
    public static double covariance(double[] data1, double[] data2) {
        return new Covariance().covariance(data1, data2);
    }
}
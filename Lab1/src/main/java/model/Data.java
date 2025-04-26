
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    private List<double[]> samples = new ArrayList<>();
    private Map<String, List<Double>> results = new HashMap<>();
    
    public Data() {
        // Инициализируем списки для результатов
        String[] metrics = {"GeometricMean", "ArithmeticMean", "StandardDeviation", 
                           "Range", "Count", "VariationCoefficient", 
                           "ConfidenceLower", "ConfidenceUpper", 
                           "Variance", "Min", "Max"};
        for (String metric : metrics) {
            results.put(metric, new ArrayList<>());
        }
    }
    
    public void addSample(double[] sample) {
        samples.add(sample);
    }
    
    public void addResult(String metric, double value) {
        results.get(metric).add(value);
    }
    
    public List<double[]> getSamples() {
        return samples;
    }
    
    public Map<String, List<Double>> getResults() {
        return results;
    }
    
    public void clearResults() {
        for (List<Double> list : results.values()) {
            list.clear();
        }
    }
}
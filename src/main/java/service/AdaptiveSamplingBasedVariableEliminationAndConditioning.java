package service;

import model.EvidenceModel;
import model.GraphicalModel;
import service.bucketelimination.BucketEliminationAlgorithm;
import service.wcutset.WCutset;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdaptiveSamplingBasedVariableEliminationAndConditioning {

    public double logProbabilityEstimate;

    public ArrayList<Double> weights = new ArrayList<>();
    public HashMap<Integer, HashMap<Integer, Double>> variableDomainWeights = new HashMap<>();
    public HashMap<Integer, HashMap<Integer,Double>> variableDomainProbabilities = new HashMap<>();
    public Random random;

    public AdaptiveSamplingBasedVariableEliminationAndConditioning(File file1, File file2, Integer n, Integer w, Random random) throws Exception {
        this.random = random;

        ArrayList<Integer> wCutset = WCutset.wCutset(new GraphicalModel(file1), new EvidenceModel(file2), w);

        GraphicalModel gm = new GraphicalModel(file1);
        EvidenceModel em = new EvidenceModel(file2);

        variableDomain_Instantiate(wCutset, gm);

        BigDecimal z = new BigDecimal(0);

        EvidenceModel emClone;
        GraphicalModel gmClone;
        for (int i = 0; i < n; i++) {
            HashMap<Integer, Integer> sample = generateSample(wCutset);

            emClone = em.clone();
            emClone.evidence.putAll((HashMap<Integer, Integer>) sample.clone());
            gmClone = gm.clone();

            BucketEliminationAlgorithm bea = new BucketEliminationAlgorithm(gmClone, emClone);
            bea.start();

            double logProbOfWCutset = Math.log10(computeProbability(sample));
            double weight =  bea.logProbability - logProbOfWCutset;
            z = z.add(new BigDecimal(weight));

            weights.add(weight);
            variableDomain_Weights_Update(sample, weight);

            if (i != 0 && i % 100 == 0) {
                variableDomain_Probabilities_Update();
                System.out.print(i + ":" + weight + " - ");
            }
        }
        System.out.println("");
        BigDecimal zOverN = z.divide(new BigDecimal(n), MC.mathContext);
        logProbabilityEstimate = zOverN.doubleValue();
    }

    private double computeProbability(HashMap<Integer, Integer> sample) {
        double prob = 1.0;

        for (Map.Entry<Integer, Integer> entry : sample.entrySet()) {
            Integer variable = entry.getKey();
            Integer domain = entry.getValue();

            HashMap<Integer, Double> domainProbabilities = variableDomainProbabilities.get(variable);
            Double domainProbability = domainProbabilities.get(domain);

            prob *= domainProbability;
        }

        return prob;
    }

    private void variableDomain_Instantiate(ArrayList<Integer> wCutset, GraphicalModel gm) {
        for (Integer variable : wCutset) {
            // instantiate uniform probabilities
            HashMap<Integer, Double> domainProbabilities = new HashMap<>();
            double cardinality = gm.cardinalities[variable];
            for (int i = 0; i < cardinality; i++ ) {
                domainProbabilities.put(i, 1d / cardinality);
            }
            variableDomainProbabilities.put(variable, domainProbabilities);

            // instantiate weight counts
            HashMap<Integer, Double> domainWeights = new HashMap<>();
            for (int i = 0; i < cardinality; i++ ) {
                domainWeights.put(i, 0.0);
            }
            variableDomainWeights.put(variable, domainWeights);
        }
    }

    private void variableDomain_Weights_Update(HashMap<Integer, Integer> sample, Double weight) {
        for (Map.Entry<Integer, Integer> entry : sample.entrySet()) {
            Integer variable = entry.getKey();
            Integer domainValue = entry.getValue();

            HashMap<Integer, Double> domainWeights = variableDomainWeights.get(variable);
            domainWeights.put(domainValue, domainWeights.get(domainValue) + weight);
        }
    }

    private void variableDomain_Probabilities_Update() {
        double totalWeight = 0.0;
        for (double weight : weights) {
            totalWeight += weight;
        }

        for (Map.Entry<Integer, HashMap<Integer, Double>> variableDomainWeight : variableDomainWeights.entrySet()) {
            Integer variable = variableDomainWeight.getKey();
            HashMap<Integer, Double> domainWeights = variableDomainWeight.getValue();

            HashMap<Integer, Double> domainProbabilities = variableDomainProbabilities.get(variable);
            for (Map.Entry<Integer, Double> entry : domainWeights.entrySet()) {
                Integer domain = entry.getKey();
                Double weight = entry.getValue();

                domainProbabilities.put(domain, weight / totalWeight);
            }
        }
    }

    // pairs of (variable, value)
    private HashMap<Integer, Integer> generateSample(ArrayList<Integer> wCutset) throws Exception {
        HashMap<Integer, Integer> sample = new HashMap<>();

        for (Integer variable : wCutset) {
            sample.put(variable, generateVariableValue(variable));
        }

        return sample;
    }

    private Integer generateVariableValue(Integer variable) throws Exception {
        double r = random.nextDouble();
        HashMap<Integer, Double> domainProbabilities = variableDomainProbabilities.get(variable);

        double totalProb = 0.0;
        for (Map.Entry<Integer, Double> domainProbability : domainProbabilities.entrySet()) {
            Integer domain = domainProbability.getKey();
            Double prob = domainProbability.getValue();

            totalProb += prob;
            if (r < totalProb) {
                return domain;
            }
        }

        throw new Exception("bad");
    }
}

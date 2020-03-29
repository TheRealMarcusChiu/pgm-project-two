package service;

import ch.obermuhlner.math.big.BigDecimalMath;
import model.EvidenceModel;
import model.GraphicalModel;
import service.bucketelimination.BucketEliminationAlgorithm;
import service.wcutset.WCutset;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdaptiveSamplingBasedVariableEliminationAndConditioning {

    public double log10Estimate;

    public ArrayList<BigDecimal> weights = new ArrayList<>();
    public HashMap<Integer, HashMap<Integer, BigDecimal>> variableDomainWeights = new HashMap<>();
    public HashMap<Integer, HashMap<Integer,Double>> variableDomainProbabilities = new HashMap<>();
    public Random random;

    public AdaptiveSamplingBasedVariableEliminationAndConditioning(File file1, File file2, Integer n, Integer w, Random random) throws Exception {
        this.random = random;

        ArrayList<Integer> wCutset = WCutset.wCutset(new GraphicalModel(file1), new EvidenceModel(file2), w);

        GraphicalModel gm = new GraphicalModel(file1);
        EvidenceModel em = new EvidenceModel(file2);

        variableDomain_Instantiate(wCutset, gm);

        BigDecimal Z = new BigDecimal(0);

        EvidenceModel emClone;
        GraphicalModel gmClone;
        for (int i = 0; i < n; i++) {
            HashMap<Integer, Integer> sample = generateSample(wCutset);

            emClone = em.clone();
            emClone.evidence.putAll((HashMap<Integer, Integer>) sample.clone());
            gmClone = gm.clone();

            BucketEliminationAlgorithm bea = new BucketEliminationAlgorithm(gmClone, emClone);
            bea.start();

            BigDecimal ePartition = BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(bea.logProbability), MC.mathContext);
            BigDecimal probOfWCutSet = new BigDecimal(computeProbability(sample));
            BigDecimal weight = ePartition.divide(probOfWCutSet, MC.mathContext);
            Z = Z.add(weight);

            weights.add(weight);
            variableDomain_Weights_Update(sample, weight);

            if (i != 0 && i % 100 == 0) {
                variableDomain_Probabilities_Update();
                System.out.print(i + ":" + weight + " - ");
            }
        }

        System.out.println("");
        BigDecimal zOverN = Z.divide(new BigDecimal(n), MC.mathContext);
        BigDecimal zOverNLog10 = BigDecimalMath.log10(zOverN, MC.mathContext);
        log10Estimate = zOverNLog10.doubleValue();
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
            HashMap<Integer, BigDecimal> domainWeights = new HashMap<>();
            for (int i = 0; i < cardinality; i++ ) {
                domainWeights.put(i, new BigDecimal(0));
            }
            variableDomainWeights.put(variable, domainWeights);
        }
    }

    private void variableDomain_Weights_Update(HashMap<Integer, Integer> sample, BigDecimal weight) {
        for (Map.Entry<Integer, Integer> entry : sample.entrySet()) {
            Integer variable = entry.getKey();
            Integer domainValue = entry.getValue();

            HashMap<Integer, BigDecimal> domainWeights = variableDomainWeights.get(variable);
            domainWeights.put(domainValue, domainWeights.get(domainValue).add(weight));
        }
    }

    private void variableDomain_Probabilities_Update() {
        BigDecimal totalWeight = new BigDecimal(0.0);
        for (BigDecimal weight : weights) {
            totalWeight = totalWeight.add(weight);
        }

        for (Map.Entry<Integer, HashMap<Integer, BigDecimal>> variableDomainWeight : variableDomainWeights.entrySet()) {
            Integer variable = variableDomainWeight.getKey();
            HashMap<Integer, BigDecimal> domainWeights = variableDomainWeight.getValue();

            HashMap<Integer, Double> domainProbabilities = variableDomainProbabilities.get(variable);
            for (Map.Entry<Integer, BigDecimal> entry : domainWeights.entrySet()) {
                Integer domain = entry.getKey();
                BigDecimal weight = entry.getValue();

                domainProbabilities.put(domain, weight.divide(totalWeight, MC.mathContext).doubleValue());
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

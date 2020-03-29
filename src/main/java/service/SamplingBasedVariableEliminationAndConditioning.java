package service;

import ch.obermuhlner.math.big.BigDecimalMath;
import model.EvidenceModel;
import model.GraphicalModel;
import service.bucketelimination.BucketEliminationAlgorithm;
import service.wcutset.WCutset;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SamplingBasedVariableEliminationAndConditioning {

    public double logProbabilityEstimate;
    public Random random;

    public SamplingBasedVariableEliminationAndConditioning(File file1, File file2, Integer n, Integer w, Random random) throws IOException, CloneNotSupportedException {
        this.random = random;
        ArrayList<Integer> wCutset = WCutset.wCutset(new GraphicalModel(file1), new EvidenceModel(file2), w);

        GraphicalModel gm = new GraphicalModel(file1);
        EvidenceModel em = new EvidenceModel(file2);

//        BucketEliminationAlgorithm bea = new BucketEliminationAlgorithm(gm.clone(), em.clone());
//        bea.start();
//        double Z = bea.logProbability;
//        double Zp =  Math.pow(10, Z);

        BigDecimal z = new BigDecimal(0);
        BigDecimal z2 = new BigDecimal(0);

        double logProbOfWCutset = Math.log10(computeProbability(wCutset, gm));
        for (int i = 0; i < n; i++) {
            HashMap<Integer, Integer> sample = generateSample(wCutset, gm);

            EvidenceModel emClone = em.clone();
            emClone.evidence.putAll(sample);

            BucketEliminationAlgorithm bea = new BucketEliminationAlgorithm(gm.clone(), emClone);
            bea.start();

//            double weight = Math.pow(10, bea.logProbability - Z - logProbOfWCutset);
//            z = z.add(new BigDecimal(weight));

            BigDecimal ePartition = BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(bea.logProbability), MC.mathContext);
            BigDecimal probOfWCutSet = BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(logProbOfWCutset), MC.mathContext);
            BigDecimal weight1 = ePartition.divide(probOfWCutSet, MC.mathContext);
            BigDecimal weight2 = BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(bea.logProbability - logProbOfWCutset), MC.mathContext);
            z2 = z2.add(weight2, MC.mathContext);

            System.out.println("weight1: " + weight1.toString());
            System.out.println("weight2: " + weight2.toString());

            if (i % 100 == 0) {
//                System.out.print(i + ":" + weight + " - ");
            }
        }
//        logProbabilityEstimate = zOverNlog10.doubleValue();
//        BigDecimal zOverN = z
//                .divide(new BigDecimal(n), MC.mathContext)
//                .multiply(new BigDecimal(Zp), MC.mathContext);
//        BigDecimal zOverNlog10 = BigDecimalMath.log10(zOverN, MC.mathContext);
//        System.out.println("log10(zOverN1): " + zOverNlog10.toString());

        BigDecimal zOverN2 = z2.divide(new BigDecimal(n), MC.mathContext);
        BigDecimal zOverN2log10 = BigDecimalMath.log10(zOverN2, MC.mathContext);
        System.out.println("log10(zOverN2): " + zOverN2log10.toString());
    }

    private double computeProbability(ArrayList<Integer> wCutset, GraphicalModel gm) {
        double prob = 1d;

        for (Integer variable : wCutset) {
            prob *= (1d / (double)gm.cardinalities[variable]);
        }

        return prob;
    }

    // pairs of (variable, value)
    private HashMap<Integer, Integer> generateSample(ArrayList<Integer> wCutset, GraphicalModel gm) {
        HashMap<Integer, Integer> sample = new HashMap<>();

        for (Integer variable : wCutset) {
            double r = random.nextDouble();
            double s = 1d / ((double)gm.cardinalities[variable]);
            sample.put(variable, (int)Math.floor(r/s));
        }

        return sample;
    }
}

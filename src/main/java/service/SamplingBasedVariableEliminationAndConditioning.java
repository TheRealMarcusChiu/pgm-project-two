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

    public double log10Estimate;
    public Random random;

    public SamplingBasedVariableEliminationAndConditioning(File file1, File file2, Integer n, Integer w, Random random) throws IOException, CloneNotSupportedException {
        this.random = random;
        ArrayList<Integer> wCutset = WCutset.wCutset(new GraphicalModel(file1), new EvidenceModel(file2), w);

        GraphicalModel gm = new GraphicalModel(file1);
        EvidenceModel em = new EvidenceModel(file2);

        BigDecimal Z = new BigDecimal(0);

        BigDecimal probOfWCutset = new BigDecimal(computeProbability(wCutset, gm));
        for (int i = 0; i < n; i++) {
            HashMap<Integer, Integer> sample = generateSample(wCutset, gm);

            EvidenceModel emClone = em.clone();
            emClone.evidence.putAll(sample);

            BucketEliminationAlgorithm bea = new BucketEliminationAlgorithm(gm.clone(), emClone);
            bea.start();

            BigDecimal ePartition = BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(bea.logProbability), MC.mathContext);
            BigDecimal weight = ePartition.divide(probOfWCutset, MC.mathContext);
            Z = Z.add(weight, MC.mathContext);

            if (i % 100 == 0) {
                System.out.print(i + ":" + weight + " - ");
            }
        }

        System.out.println("");
        BigDecimal zOverN2 = Z.divide(new BigDecimal(n), MC.mathContext);
        BigDecimal zOverN2log10 = BigDecimalMath.log10(zOverN2, MC.mathContext);
        log10Estimate = zOverN2log10.doubleValue();
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

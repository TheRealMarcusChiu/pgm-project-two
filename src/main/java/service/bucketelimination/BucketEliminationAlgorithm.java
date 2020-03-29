package service.bucketelimination;

import model.BucketModel;
import model.EvidenceModel;
import model.FactorModel;
import model.GraphicalModel;
import service.bucketelimination.factor.FactorInstantiate;
import service.bucketelimination.factor.FactorProduct;
import service.bucketelimination.factor.FactorSum;
import service.util.MinOrdering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BucketEliminationAlgorithm {

    GraphicalModel gm;
    EvidenceModel em;
    ArrayList<BucketModel> buckets;
    List<Integer> eliminationOrder;

    ArrayList<FactorModel> constantFactors = new ArrayList<>();

    public double logProbability;

    public BucketEliminationAlgorithm(File file1, File file2) throws IOException {
        this.gm = new GraphicalModel(file1);
        this.em = new EvidenceModel(file2);
    }

    public BucketEliminationAlgorithm(GraphicalModel gm, EvidenceModel em) {
        this.gm = gm;
        this.em = em;
    }

    public void start() {
        instantiateEvidence();
        instantiateBuckets();
        startElimination();
        compute();
    }

    private void compute() {
        double product = 0d;
        for (FactorModel f : constantFactors) {
            product += f.factor[0];
        }
        logProbability = product;
    }

    private void startElimination() {
        while (buckets.size() > 0) {
            BucketModel b = buckets.remove(0);
            eliminationOrder.remove(0);
            FactorModel f = b.factors.get(0);
            for (int i = 1; i < b.factors.size(); i++) {
                f = FactorProduct.product(f, b.factors.get(i), gm.cardinalities);
            }
            f = FactorSum.sumOut(f, b.variable, gm.cardinalities);

            addFactor(f, eliminationOrder);
        }
    }

    private void addFactor(FactorModel f, List<Integer> eliminationOrder) {
        Integer minBucketIndex = getAssignedBucketIndex(f, eliminationOrder);
        if (minBucketIndex != -1) {
            buckets.get(minBucketIndex).factors.add(f);
        } else {
            constantFactors.add(f);
        }
    }

    private void instantiateEvidence() {
        for (int i = 0; i < gm.factorModels.size(); i++) {
            FactorModel f = FactorInstantiate.instantiate(gm.factorModels.get(i), em, gm.cardinalities);
            gm.factorModels.set(i, f);
        }
    }

    private void instantiateBuckets() {
        eliminationOrder = MinOrdering.computeMinDegreeOrdering(gm);

        for(Map.Entry<Integer, Integer> entry : em.evidence.entrySet()) {
            Integer variable = entry.getKey();
            eliminationOrder.remove(variable);
        }

        buckets = new ArrayList<>();
        for (int i = 0; i < eliminationOrder.size(); i++) {
            buckets.add(new BucketModel());
        }

        for (int i = 0; i < eliminationOrder.size(); i++) {
            buckets.get(i).variable = eliminationOrder.get(i);
        }

        // assign factors to buckets based on elimination order
        for (FactorModel f : gm.factorModels) {
            addFactor(f, eliminationOrder);
        }
    }

    private Integer getAssignedBucketIndex(FactorModel f, List<Integer> eliminationOrder) {
        if (f.variables.size() == 0) {
            return -1;
        } else {
            int minBucketIndex = eliminationOrder.indexOf(f.variables.get(0));
            for (Integer variable : f.variables) {
                int q = eliminationOrder.indexOf(variable);
                if (q < minBucketIndex) {
                    minBucketIndex = q;
                }
            }
            return minBucketIndex;
        }
    }
}

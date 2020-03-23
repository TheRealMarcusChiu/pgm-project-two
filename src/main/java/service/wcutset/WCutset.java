package service.wcutset;

import model.EvidenceModel;
import model.FactorModel;
import model.GraphicalModel;
import service.util.MinOrdering;
import service.wcutset.model.BucketModel;

import java.util.*;
import java.util.stream.Collectors;

public class WCutset {

    public static ArrayList<Integer> wCutset(GraphicalModel gm, EvidenceModel em, Integer w) {
        ArrayList<Integer> wCutset = new ArrayList<>();

        ArrayList<BucketModel> clusters = simulateBucketElimination(gm, em);

        while (containsClusterGreaterThanSize(clusters, w + 1)) {
            Integer x = findMostOccurringVariableInClusters(clusters);
            for (BucketModel cluster : clusters) {
                cluster.variables.remove(x);
            }
            wCutset.add(x);
        }

        return wCutset;
    }

    private static Integer findMostOccurringVariableInClusters(ArrayList<BucketModel> clusters) {
        // aggregate variables into arraylist
        ArrayList<Integer> poolOfVariables = new ArrayList<>();
        for (BucketModel cluster : clusters) {
            poolOfVariables.addAll(cluster.variables);
        }

        // compute number occurrences of each variable
        Map<Object, Long> occurrences = poolOfVariables.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        // sort in descending order
        LinkedHashMap<Object, Long> sortedMap = new LinkedHashMap<>();
        occurrences.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        // return first in order
        return (Integer)sortedMap.entrySet().iterator().next().getKey();
    }

    private static Boolean containsClusterGreaterThanSize(ArrayList<BucketModel> clusters, Integer size) {
        for (BucketModel cluster : clusters) {
            if (cluster.variables.size() > size) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<BucketModel> simulateBucketElimination(GraphicalModel gm, EvidenceModel em) {
        List<Integer> eliminationOrder = MinOrdering.computeMinDegreeOrdering(gm);

        for(Map.Entry<Integer, Integer> entry : em.evidence.entrySet()) {
            Integer variable = entry.getKey();
            eliminationOrder.remove(variable);
        }

        ArrayList<BucketModel> buckets = new ArrayList<>();
        for (int i = 0; i < eliminationOrder.size(); i++) {
            buckets.add(new BucketModel());
        }

        for (int i = 0; i < eliminationOrder.size(); i++) {
            buckets.get(i).variable = eliminationOrder.get(i);
        }

        // assign factors to buckets based on elimination order
        for (FactorModel f : gm.factorModels) {
            HashSet<Integer> factorVariables = new HashSet<>((ArrayList)f.variables.clone());
            addFactorVariables(buckets, factorVariables, eliminationOrder);
        }

        ArrayList<BucketModel> processedBuckets = new ArrayList<>();
        while (buckets.size() > 0) {
            BucketModel b = buckets.remove(0);
            eliminationOrder.remove(0);

            HashSet<Integer> newFactorVariables = (HashSet)b.variables.clone();
            newFactorVariables.remove(b.variable);

            addFactorVariables(buckets, newFactorVariables, eliminationOrder);
            processedBuckets.add(b);
        }

        return processedBuckets;
    }

    private static void addFactorVariables(ArrayList<BucketModel> buckets, HashSet<Integer> factorVariables, List<Integer> eliminationOrder) {
        Integer minBucketIndex = getAssignedBucketIndex(factorVariables, eliminationOrder);
        if (minBucketIndex != -1) {
            buckets.get(minBucketIndex).variables.addAll(factorVariables);
        } else {
        }
    }

    private static Integer getAssignedBucketIndex(HashSet<Integer> factorVariables, List<Integer> eliminationOrder) {
        if (factorVariables.size() == 0) {
            return -1;
        } else {
            int minBucketIndex = eliminationOrder.indexOf(factorVariables.iterator().next());
            for (Integer variable : factorVariables) {
                int q = eliminationOrder.indexOf(variable);
                if (q < minBucketIndex) {
                    minBucketIndex = q;
                }
            }
            return minBucketIndex;
        }
    }
}

package service.util;

import model.FactorModel;
import model.GraphicalModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinOrdering {

    // variable from smallest degree to largest degree
    public static List<Integer> computeMinDegreeOrdering(GraphicalModel gm) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < gm.cardinalities.length; i++) {
            counts.put(i, 0);
        }

        for (FactorModel fm : gm.factorModels) {
            for (Integer i : fm.variables) {
                counts.put(i, counts.get(i) + 1);
            }
        }

        List<Integer> collect = counts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return collect;
    }
}

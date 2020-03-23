package service.bucketelimination.factor;

import model.EvidenceModel;
import model.FactorModel;

import java.util.Map;

public class FactorInstantiate {

    public static FactorModel instantiate(FactorModel f, EvidenceModel e, Integer[] cardinality) {
        FactorModel nf = f;

        for(Map.Entry<Integer, Integer> entry : e.evidence.entrySet()) {
            Integer variable = entry.getKey();
            Integer value = entry.getValue();

            nf = instantiateHelper(nf, variable, value, cardinality);
        }

        return nf;
    }

    private static FactorModel instantiateHelper(FactorModel f, Integer variable, Integer value, Integer[] cardinality) {
        int index = f.variables.indexOf(variable);
        if (index != -1) {
            FactorModel nf = new FactorModel();

            int oldFactorSize = Util.totalCardinality(f.variables, cardinality);
            int newFactorSize = oldFactorSize / cardinality[variable];
            nf.factor = new Double[newFactorSize];
            f.variables.remove(index);
            nf.variables = f.variables;
            Util.computeStride(nf, cardinality);

            int sweep = f.strides.get(index);
            int s_c = sweep * cardinality[variable];
            int times = oldFactorSize / s_c;

            int ii = 0;
            for (int i = 0; i < times; i++) {
                for (int j = 0; j < sweep; j++) {
                    nf.factor[ii] = f.factor[(i * s_c) + (value * sweep) + j];
                    ii++;
                }
            }

            return nf;
        }
        return f;
    }
}

package service.bucketelimination.factor;

import model.FactorModel;

import java.util.ArrayList;
import java.util.Collections;

public class Util {

    public static void computeStride(FactorModel fm, Integer[] cardinalities) {
        fm.strides = new ArrayList<>();
        int stride = 1;
        for (int i = fm.variables.size()-1; i >= 0; i--) {
            fm.strides.add(stride);
            stride *= cardinalities[fm.variables.get(i)];
        }
        Collections.reverse(fm.strides);
//        int stride = 1;
//        for (int i = 0; i < fm.variables.size(); i++) {
//            fm.strides.add(stride);
//            stride *= cardinalities[fm.variables.get(i)];
//        }
    }

    public static Integer totalCardinality(Integer[] vars, Integer[] cardinalities) {
        Integer nFactorSize = 1;
        for (int variable : vars) {
            nFactorSize *= cardinalities[variable];
        }
        return nFactorSize;
    }

    public static Integer totalCardinality(ArrayList<Integer> vars, Integer[] cardinalities) {
        Integer nFactorSize = 1;
        for (int variable : vars) {
            nFactorSize *= cardinalities[variable];
        }
        return nFactorSize;
    }

    public static Integer[] getUnionOfVariables(FactorModel fm1, FactorModel fm2) {
        ArrayList<Integer> t = (ArrayList<Integer>) fm1.variables.clone();
        for (Integer i : fm2.variables) {
            if (t.indexOf(i) == -1) {
                t.add(i);
            }
        }
        return t.toArray(new Integer[0]);
    }
}

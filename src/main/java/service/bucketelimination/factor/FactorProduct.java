package service.bucketelimination.factor;

import model.FactorModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FactorProduct {

    public static FactorModel product(FactorModel f1, FactorModel f2, Integer[] c) {
        FactorModel fn = new FactorModel();

        int j = 0;
        int k = 0;

        Integer[] vs = Util.getUnionOfVariables(f1, f2);

        int[] vas = new int[vs.length];
        int nFactorSize = Util.totalCardinality(vs, c);

        fn.variables = (ArrayList<Integer>) Arrays.stream(vs).collect(Collectors.toList());
        Util.computeStride(fn, c);
        fn.factor = new BigDecimal[nFactorSize];

        for (int i = 0; i < nFactorSize; i++) {
            fn.factor[i] = f1.factor[j].multiply(f2.factor[k]);

            for (int lvi = vs.length-1; lvi >= 0 ; lvi--) {
                int lv = vs[lvi];
                vas[lvi] = vas[lvi] + 1;
                if (vas[lvi] == (c[lv])) {
                    vas[lvi] = 0;
                    j = j - ((c[lv] - 1) * f1.getStride(lv));
                    k = k - ((c[lv] - 1) * f2.getStride(lv));
                } else {
                    j = j + f1.getStride(lv);
                    k = k + f2.getStride(lv);
                    break;
                }
            }
        }

        return fn;
    }

//    public static FactorModel product(FactorModel f1, FactorModel f2, Integer[] c) {
//        FactorModel fn = new FactorModel();
//
//        int j = 0;
//        int k = 0;
//
//        Integer[] vs = Util.getUnionOfVariables(f1, f2);
//
//        int[] vas = new int[vs.length];
//        int nFactorSize = Util.totalCardinality(vs, c);
//
//        fn.variables = (ArrayList<Integer>) Arrays.stream(vs).collect(Collectors.toList());
//        Util.computeStride(fn, c);
//        fn.factor = new Double[nFactorSize];
//
//        for (int i = 0; i < nFactorSize; i++) {
//            fn.factor[i] = f1.factor[j] * f2.factor[k];
//
//            for (int lvi = 0; lvi < vs.length; lvi++) {
//                int lv = vs[lvi];
//                vas[lvi] = vas[lvi] + 1;
//                if (vas[lvi] == (c[lv])) {
//                    vas[lvi] = 0;
//                    j = j - ((c[lv] - 1) * f1.getStride(lv));
//                    k = k - ((c[lv] - 1) * f2.getStride(lv));
//                } else {
//                    j = j + f1.getStride(lv);
//                    k = k + f2.getStride(lv);
//                    break;
//                }
//            }
//        }
//
//        return fn;
//    }
}

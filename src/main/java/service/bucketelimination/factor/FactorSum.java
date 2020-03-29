package service.bucketelimination.factor;

import ch.obermuhlner.math.big.BigDecimalMath;
import model.FactorModel;
import service.MC;

import java.math.BigDecimal;

public class FactorSum {

    public static FactorModel sumOut(FactorModel f, Integer var, Integer[] cardinalities) {
        FactorModel nf;
        if (f.variables.size() > 1) {
            nf = sumOutGreaterThan1(f, var, cardinalities);
        } else {
            nf = sumOutEquals1(f);
        }
        Util.computeStride(nf, cardinalities);

        return nf;
    }

    private static FactorModel sumOutEquals1(FactorModel fm) {
        fm.variables.remove(0);
        BigDecimal sum = new BigDecimal("0");
        for (int i = 0; i < fm.factor.length; i++) {
//            sum = sum.add(fm.factor[i]);
            sum = sum.add(BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(fm.factor[i]), MC.mathContext));
        }
        fm.factor = new double[1];
//        fm.factor[0] = sum;
        fm.factor[0] = BigDecimalMath.log10(sum, MC.mathContext).doubleValue();
        return fm;
    }

    /**
     * temp-l = 1
     * for i = 0 to index-of-variable
     *      temp-l *= cardinality-variable(i)
     *
     * temp-r = 1
     * for i = index-of-variable to end
     *      temp-r *= cardinality-variable(i)
     *
     * jump-l = total-number / temp-l
     * jump-r = total-number / temp-r
     * new-factor-size = total-number / variable-cardinality
     *
     * int newIndex = 0
     * for i in 0 to jump-r
     *      for j in 0 to jump-l
     * 		    index = (i * temp-r) + j
     * 		    sum = 0
     * 		    for k in 0 to variable-cardinality - 1
     * 			    sum += old-factor[index + (k*jump-l)]
     * 		    new-factor[newIndex] = sum
     * 		    newIndex++;
     */
    private static FactorModel sumOutGreaterThan1(FactorModel fm, Integer var, Integer[] cardinalities) {
        FactorModel new_fm = new FactorModel();

        int varIndex = fm.variables.indexOf(var);

        int tempL = 1;
        for (int i = 0; i <= varIndex; i++) {
            tempL *= cardinalities[fm.variables.get(i)];
        }
        int tempR = 1;
        for (int i = varIndex; i < fm.variables.size(); i++) {
            tempR *= cardinalities[fm.variables.get(i)];
        }

        int jumpL = fm.factor.length / tempL;
        int jumpR = fm.factor.length / tempR;
        int newFactorSize = fm.factor.length / cardinalities[var];
        new_fm.factor = new double[newFactorSize];

        int newIndex = 0;
        for (int i = 0; i < jumpR; i++) {
            for (int j = 0; j < jumpL; j++) {
                int index = (i * tempR) + j;
                BigDecimal sum = new BigDecimal(0d);
                for (int k = 0; k < cardinalities[var]; k++) {
//                    sum += fm.factor[index + (k * jumpL)];
                    sum = sum.add(BigDecimalMath.pow(new BigDecimal(10), new BigDecimal(fm.factor[index + (k * jumpL)]), MC.mathContext));
                }
//                new_fm.factor[newIndex] = sum;
                new_fm.factor[newIndex] = BigDecimalMath.log10(sum, MC.mathContext).doubleValue();
                newIndex++;
            }
        }

        fm.variables.remove(varIndex);
        new_fm.variables = fm.variables;

        return new_fm;
    }
}

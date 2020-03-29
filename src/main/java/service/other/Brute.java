package service.other;

import ch.obermuhlner.math.big.BigDecimalMath;
import model.FactorModel;
import model.GraphicalModel;
import service.MC;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class Brute {

    GraphicalModel gm;
    int numVariables;

    public Brute(File file1) throws IOException {
        this.gm = new GraphicalModel(file1);
        this.numVariables = gm.cardinalities.length;
    }

    public BigDecimal computePartitionFunctionValue() throws Exception {
        BigDecimal sum = new BigDecimal(0);
        int[] assignment = new int[numVariables];
        boolean endNow;

        do {
            sum = sum.add(BigDecimalMath.pow(new BigDecimal(10), computeProduct(assignment), MC.mathContext));
            endNow = checkEnd(assignment);
            incrementAssignment(assignment);
        } while (!endNow);

        return BigDecimalMath.log10(sum, MC.mathContext);
    }

    private void incrementAssignment(int[] assignment) {
        for (int i = 0; i < numVariables; i++) {
            assignment[i] = assignment[i] + 1;
            if (assignment[i] > 1) {
                assignment[i] = 0;
            } else {
                break;
            }
        }
    }

    private boolean checkEnd(int[] assignment) throws Exception {
        for (int i = numVariables - 1; i >= 0; i--) {
            if (assignment[i] == 1) {
                if (i == 0) {
                    return true;
                }
            } else {
                return false;
            }
        }
        throw new Exception("checkEnded");
    }

//    private double computeProduct(int[] assignment, )
    private BigDecimal computeProduct(int[] assignment) {
        BigDecimal product = new BigDecimal(0);

        for (FactorModel f : gm.factorModels) {
            product = product.add(getValue(f, assignment));
        }

        return product;
    }

    private BigDecimal getValue(FactorModel f, int[] assignment) {
        int[] localAssignment = new int[f.variables.size()];
        for (int i = 0; i < localAssignment.length; i++) {
            localAssignment[i] = assignment[f.variables.get(i)];
        }

        int index = f.getIndexFromAssignment(localAssignment);
        return new BigDecimal(f.factor[index]);
    }
}

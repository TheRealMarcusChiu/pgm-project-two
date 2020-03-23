package service.other;

import model.FactorModel;
import model.GraphicalModel;

import java.io.File;
import java.io.IOException;

public class Brute {

    GraphicalModel gm;
    int numVariables;

    public Brute(File file1) throws IOException {
        this.gm = new GraphicalModel(file1);
        this.numVariables = gm.cardinalities.length;
    }

    public double computePartitionFunctionValue() throws Exception {
        double sum = 0;
        int[] assignment = new int[numVariables];
        boolean endNow;

        do {
            sum += computeProduct(assignment);
            endNow = checkEnd(assignment);
            incrementAssignment(assignment);
        } while (!endNow);

        return sum;
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
    private double computeProduct(int[] assignment) {
        double product = 1;

        for (FactorModel f : gm.factorModels) {
            product *= getValue(f, assignment);
        }

        return product;
    }

    private double getValue(FactorModel f, int[] assignment) {
        int[] localAssignment = new int[f.variables.size()];
        for (int i = 0; i < localAssignment.length; i++) {
            localAssignment[i] = assignment[f.variables.get(i)];
        }

        int index = f.getIndexFromAssignment(localAssignment);
        return f.factor[index];
    }
}

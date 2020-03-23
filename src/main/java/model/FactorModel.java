package model;

import java.util.ArrayList;

public class FactorModel implements Cloneable {
    public ArrayList<Integer> variables;
    public ArrayList<Integer> strides;
    public Double[] factor;

    public FactorModel() {
        variables = new ArrayList<>();
        strides = new ArrayList<>();
    }

    public FactorModel clone() throws CloneNotSupportedException {
        // Assign the shallow copy to new reference variable t
        FactorModel fm = (FactorModel)super.clone();
        fm.variables = (ArrayList)this.variables.clone();
        fm.strides = (ArrayList)this.strides.clone();
        fm.factor = this.factor.clone();
        return fm;
    }

    public Integer getStride(Integer variable) {
        int index = variables.indexOf(variable);
        if (index != -1) {
            return strides.get(index);
        } else {
            return 0;
        }
    }

    public Integer getIndexFromAssignment(int[] assignments) {
        int index = 0;
        for (int i = 0; i < assignments.length; i++) {
            index += assignments[i] * strides.get(i);
        }
        return index;
    }
}

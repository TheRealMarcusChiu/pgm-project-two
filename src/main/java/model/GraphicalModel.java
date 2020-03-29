package model;

import org.apache.commons.io.FileUtils;
import service.bucketelimination.factor.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphicalModel implements Cloneable {

    public Integer[] cardinalities;
    public ArrayList<FactorModel> factorModels = new ArrayList<>();

    public GraphicalModel(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        // filter empty lines and comments
        lines = lines.stream()
                .filter(line -> line.trim().length() > 0)
                .filter(line -> !line.substring(0,1).equals("c"))
                .collect(Collectors.toList());
        // remove first 2 lines
        lines.remove(0);
        lines.remove(0);

        cardinalities = Arrays.stream(lines.remove(0).split("\\s+")).map(Integer::parseInt).toArray(Integer[]::new);
        int numCliques = Integer.parseInt(lines.remove(0));

        for (int i = 0; i < numCliques; i++) {
            FactorModel fm = new FactorModel();
            String line = lines.remove(0);
            String[] ll = line.split("\\s+");
            ArrayList<Integer> temp = Arrays.stream(ll).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
            temp.remove(0);
            fm.variables = temp;
            Util.computeStride(fm, cardinalities);
            factorModels.add(fm);
        }

        for (int i = 0; i < numCliques; i++) {
            lines.remove(0);
            FactorModel fm = factorModels.get(i);
            String str = lines.remove(0);
            String[] strArray = str.split(" ");
            Double[] t = Arrays.stream(strArray).map(Double::valueOf).toArray(Double[]::new);
            fm.factor = new double[t.length];
            for (int ii = 0; ii < t.length; ii++) {
                fm.factor[ii] = Math.log10(t[ii]);
            }
        }
    }

    public GraphicalModel clone() throws CloneNotSupportedException {
        // Assign the shallow copy to new reference variable t
        GraphicalModel gm = (GraphicalModel)super.clone();
        gm.cardinalities = this.cardinalities.clone();
        gm.factorModels = new ArrayList<>();
        for (FactorModel fm : this.factorModels) {
            gm.factorModels.add(fm.clone());
        }
        return gm;
    }
}

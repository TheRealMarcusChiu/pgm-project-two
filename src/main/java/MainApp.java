import service.AdaptiveSamplingBasedVariableEliminationAndConditioning;
import service.SamplingBasedVariableEliminationAndConditioning;

import java.io.File;
import java.util.Random;

public class MainApp {

    public static void main(String args[]) throws Exception {
//        File file1 = new File("files/" + args[0]);
//        File file2 = new File("files/" + args[1]);

        File file1 = new File("files/Grids_16.uai");
        File file2 = new File("files/Grids_16.uai.evid");

        Integer[] ns = new Integer[]{100};
        Integer[] ws = new Integer[]{5};

        Integer n;
        Integer w;
        for (int i = 0; i < ns.length; i++) {
            n = ns[i];
            for (int j = 0; j < ws.length; j++) {
                w = ws[j];
                System.out.println("w: " + w + " - n: " + n);

                Random generator = new Random((long) (Math.random() * 1000000000000000L));

                SamplingBasedVariableEliminationAndConditioning s = new SamplingBasedVariableEliminationAndConditioning(file1, file2, n, w, generator);
//                System.out.println("s - output: " + s.logProbabilityEstimate);

//                AdaptiveSamplingBasedVariableEliminationAndConditioning as = new AdaptiveSamplingBasedVariableEliminationAndConditioning(file1, file2, n, w, generator);
//                System.out.println("a - output: " + as.logProbabilityEstimate);
            }
        }
    }
}

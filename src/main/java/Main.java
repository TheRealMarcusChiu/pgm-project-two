import model.EvidenceModel;
import model.GraphicalModel;
import service.bucketelimination.BucketEliminationAlgorithm;
import service.util.BigDecimalMath;
import service.wcutset.WCutset;

import java.io.File;

public class Main {
    public static void main(String args[]) throws Exception {
//        File file1 = new File("files/" + args[0]);
//        File file2 = new File("files/" + args[1]);

        File file1 = new File("files/3.uai");
        File file2 = new File("files/3.uai.evid");
////        File file1 = new File("files/Grids_14.uai");
////        File file2 = new File("files/Grids_14.uai.evid");
        BucketEliminationAlgorithm bea = new BucketEliminationAlgorithm(file1, file2);
        bea.start();
        System.out.println("LOG 10 of PARTITION FUNCTION: " + BigDecimalMath.log(10, bea.output));

//        File file1 = new File("files/6.uai");
//        File file2 = new File("files/6.uai.evid");
//        GraphicalModel gm = new GraphicalModel(file1);
//        EvidenceModel em = new EvidenceModel(file2);
//        WCutset.wCutset(gm, em, 5);
    }
}

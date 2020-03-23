package service;

import model.EvidenceModel;
import model.GraphicalModel;
import service.wcutset.WCutset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SamplingBasedVariableEliminationAndConditioning {

    GraphicalModel gm;
    EvidenceModel em;

    public SamplingBasedVariableEliminationAndConditioning(File file1, File file2) throws IOException {
        Integer w = 2;
        Integer n = 10;

        ArrayList<Integer> wCutset = WCutset.wCutset(
                new GraphicalModel(file1),
                new EvidenceModel(file2),
                w);

        GraphicalModel gm = new GraphicalModel(file1);
        EvidenceModel em = new EvidenceModel(file2);

        for (int i = 0; i < n; i++) {

        }
    }

    private HashMap<Integer, Integer> generateSample(ArrayList<Integer> wCutset, GraphicalModel gm) {
        HashMap<Integer, Integer> sample = new HashMap<>();



        return sample;
    }
}

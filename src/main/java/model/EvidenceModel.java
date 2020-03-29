package model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EvidenceModel implements Cloneable {

    public HashMap<Integer, Integer> evidence = new HashMap<>();

    public EvidenceModel(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        // filter empty lines and comments
        lines = lines.stream()
                .filter(line -> line.trim().length() > 0)
                .filter(line -> !line.substring(0, 1).equals("c"))
                .collect(Collectors.toList());
        // remove first line
        Integer[] numbers = Arrays.stream(lines.remove(0).split("\\s+")).map(Integer::parseInt).toArray(Integer[]::new);

        for (int i = 0; i < numbers[0]; i++) {
            evidence.put(numbers[(2*i)+1], numbers[(2*i)+2]);
        }
    }

    public EvidenceModel clone() throws CloneNotSupportedException {
        // Assign the shallow copy to new reference variable t
        EvidenceModel em = (EvidenceModel)super.clone();
        em.evidence = (HashMap)this.evidence.clone();
        return em;
    }
}

import service.AdaptiveSamplingBasedVariableEliminationAndConditioning;
import service.SamplingBasedVariableEliminationAndConditioning;
import service.util.MeanStd;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainApp {

    public static void main(String args[]) throws Exception {
        runFile("Grids_18");
    }

    private static void runFile(String filePrefix) throws Exception{
        BufferedWriter writer = new BufferedWriter(new FileWriter("output/" + filePrefix + "-output.txt"));

        File file1 = new File("files/" + filePrefix + ".uai");
        File file2 = new File("files/" + filePrefix + ".uai.evid");
        Double Z = getZ("files/" + filePrefix + ".uai.PR");

        Integer[] ns = new Integer[]{100, 1000, 10000, 20000};
        Integer[] ws = new Integer[]{1, 2, 3, 4, 5};

        Integer numRuns = 10;

        ArrayList<Output> outputs = new ArrayList<>();
        for (int i = 0; i < numRuns; i++) {
            outputs.add(run(file1, file2, ns, ws, Z));
        }

        HashMap<String, OutputsCalculate> outputsCalculateHashMap = calculateOutputs(outputs);
        HashMap<String, OutputMeanStd> outputMeanStdHashMap = compute(outputsCalculateHashMap);

        for (Map.Entry<String, OutputMeanStd> entry : outputMeanStdHashMap.entrySet()) {
            String prefix = entry.getKey();
            OutputMeanStd outputMeanStd = entry.getValue();

            String str = prefix + "\n" + outputMeanStd.toString() + "\n";
            System.out.println(str);
            writer.write(str);
        }

        writer.close();
    }

    private static HashMap<String, OutputMeanStd> compute(HashMap<String, OutputsCalculate> outputsCalculateHashMap) {
        HashMap<String, OutputMeanStd> outputMeanStdHashMap = new HashMap<>();

        // instantiate outputMeanStdHashMap and fill it
        for (Map.Entry<String, OutputsCalculate> entry : outputsCalculateHashMap.entrySet()) {
            String prefix = entry.getKey();
            OutputsCalculate outputsCalculate = entry.getValue();

            OutputMeanStd outputMeanStd = new OutputMeanStd();

            outputMeanStd.log10EstimateMean = MeanStd.computeMeanDouble(outputsCalculate.log10Estimate);
            outputMeanStd.log10EstimateStd = MeanStd.computeStdDouble(outputsCalculate.log10Estimate, outputMeanStd.log10EstimateMean);
            outputMeanStd.errorMean = MeanStd.computeMeanDouble(outputsCalculate.error);
            outputMeanStd.errorStd = MeanStd.computeStdDouble(outputsCalculate.error, outputMeanStd.errorMean);
            outputMeanStd.timeMean = MeanStd.computeMeanLong(outputsCalculate.time);
            outputMeanStd.timeStd = MeanStd.computeStdLong(outputsCalculate.time, outputMeanStd.timeMean);

            outputMeanStd.log10EstimateAdaptiveMean = MeanStd.computeMeanDouble(outputsCalculate.log10EstimateAdaptive);
            outputMeanStd.log10EstimateAdaptiveStd = MeanStd.computeStdDouble(outputsCalculate.log10EstimateAdaptive, outputMeanStd.log10EstimateAdaptiveMean);
            outputMeanStd.errorAdaptiveMean = MeanStd.computeMeanDouble(outputsCalculate.errorAdaptive);
            outputMeanStd.errorAdaptiveStd = MeanStd.computeStdDouble(outputsCalculate.error, outputMeanStd.errorAdaptiveMean);
            outputMeanStd.timeAdaptiveMean = MeanStd.computeMeanLong(outputsCalculate.timeAdaptive);
            outputMeanStd.timeAdaptiveStd = MeanStd.computeStdLong(outputsCalculate.timeAdaptive, outputMeanStd.timeAdaptiveMean);

            outputMeanStdHashMap.put(prefix, outputMeanStd);
        }

        return outputMeanStdHashMap;
    }

    private static HashMap<String, OutputsCalculate> calculateOutputs(ArrayList<Output> outputs) {
        HashMap<String, OutputsCalculate> outputsCalculateHashMap = new HashMap<>();

        // instantiate outputsCalculateHashMap
        for (Map.Entry<String, Double> entry : outputs.get(0).errorAdaptive.entrySet()) {
            String prefix = entry.getKey();
            outputsCalculateHashMap.put(prefix, new OutputsCalculate());
        }

        // fill in outputsCalculateHashMap
        for (Output output : outputs) {
            for (Map.Entry<String, Double> entry : output.log10Estimate.entrySet()) {
                String prefix = entry.getKey();
                Double log10Estimate = entry.getValue();
                outputsCalculateHashMap.get(prefix).log10Estimate.add(log10Estimate);
            }
            for (Map.Entry<String, Double> entry : output.error.entrySet()) {
                String prefix = entry.getKey();
                Double error = entry.getValue();
                outputsCalculateHashMap.get(prefix).error.add(error);
            }
            for (Map.Entry<String, Long> entry : output.time.entrySet()) {
                String prefix = entry.getKey();
                Long time = entry.getValue();
                outputsCalculateHashMap.get(prefix).time.add(time);
            }

            for (Map.Entry<String, Double> entry : output.log10EstimateAdaptive.entrySet()) {
                String prefix = entry.getKey();
                Double log10Estimate = entry.getValue();
                outputsCalculateHashMap.get(prefix).log10EstimateAdaptive.add(log10Estimate);
            }
            for (Map.Entry<String, Double> entry : output.errorAdaptive.entrySet()) {
                String prefix = entry.getKey();
                Double error = entry.getValue();
                outputsCalculateHashMap.get(prefix).errorAdaptive.add(error);
            }
            for (Map.Entry<String, Long> entry : output.timeAdaptive.entrySet()) {
                String prefix = entry.getKey();
                Long time = entry.getValue();
                outputsCalculateHashMap.get(prefix).timeAdaptive.add(time);
            }
        }

        return outputsCalculateHashMap;
    }

    private static Output run(File file1, File file2, Integer[] ns, Integer[] ws, Double Z) throws Exception {
        Output output = new Output();

        Integer n;
        Integer w;
        long startTime;
        long endTime;
        for (int i = 0; i < ns.length; i++) {
            n = ns[i];
            for (int j = 0; j < ws.length; j++) {
                w = ws[j];
                String prefix = "W=" + w + " - N=" + n;
                System.out.println("\n" + prefix);

                Random generator = new Random((long) (Math.random() * 1000000000000000L));

                startTime = System.nanoTime();
                SamplingBasedVariableEliminationAndConditioning s = new SamplingBasedVariableEliminationAndConditioning(file1, file2, n, w, generator);
                endTime = System.nanoTime();

                System.out.println("s - output: " + s.log10Estimate);
                output.log10Estimate.put(prefix, s.log10Estimate);
                output.time.put(prefix, endTime - startTime);
                output.error.put(prefix, (Z - s.log10Estimate) / Z);

                startTime = System.nanoTime();
                AdaptiveSamplingBasedVariableEliminationAndConditioning as = new AdaptiveSamplingBasedVariableEliminationAndConditioning(file1, file2, n, w, generator);
                endTime = System.nanoTime();

                System.out.println("a - output: " + as.log10Estimate);
                output.log10EstimateAdaptive.put(prefix, as.log10Estimate);
                output.timeAdaptive.put(prefix, endTime - startTime);
                output.errorAdaptive.put(prefix, (Z - as.log10Estimate) / Z);
            }
        }

        return output;
    }

    private static Double getZ(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName), 1024);
        in.readLine();
        return Double.parseDouble(in.readLine());
    }
}

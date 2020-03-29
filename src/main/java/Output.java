import java.util.HashMap;

public class Output {
    public HashMap<String, Double> log10Estimate = new HashMap<>();
    public HashMap<String, Double> error = new HashMap<>();
    public HashMap<String, Long> time = new HashMap<>();

    public HashMap<String, Double> log10EstimateAdaptive = new HashMap<>();
    public HashMap<String, Double> errorAdaptive = new HashMap<>();
    public HashMap<String, Long> timeAdaptive = new HashMap<>();

    @Override
    public String toString() {
        return "log10Estimate: " + log10Estimate.toString() +
                "\nerror: " + error.toString() +
                "\ntime: " + time.toString() +
                "\nlog10EstimateAdaptive" + log10EstimateAdaptive.toString() +
                "\nerrorAdaptive" + errorAdaptive.toString() +
                "\ntimeAdaptive" + timeAdaptive.toString();
    }
}

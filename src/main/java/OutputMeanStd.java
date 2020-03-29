public class OutputMeanStd {
    public Double log10EstimateMean;
    public Double log10EstimateStd;
    public Double errorMean;
    public Double errorStd;
    public Double timeMean;
    public Double timeStd;

    public Double log10EstimateAdaptiveMean;
    public Double errorAdaptiveMean;
    public Double timeAdaptiveMean;
    public Double log10EstimateAdaptiveStd;
    public Double errorAdaptiveStd;
    public Double timeAdaptiveStd;

    @Override
    public String toString() {
        return "log10EstimateMean: " + log10EstimateMean +
                "\nlog10EstimateStd: " + log10EstimateStd +
                "\nerrorMean: " + errorMean +
                "\nerrorStd: " + errorStd +
                "\ntimeMean: " + timeMean +
                "\ntimeStd: " + timeStd +

                "\nlog10EstimateAdaptiveMean: " + log10EstimateAdaptiveMean +
                "\nlog10EstimateAdaptiveStd: " + log10EstimateAdaptiveStd +
                "\nerrorAdaptiveMean: " + errorAdaptiveMean +
                "\nerrorAdaptiveStd: " + errorAdaptiveStd +
                "\ntimeAdaptiveMean: " + timeAdaptiveMean +
                "\ntimeAdaptiveStd: " + timeAdaptiveStd;
    }
}

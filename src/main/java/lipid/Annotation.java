package lipid;

import adduct.Adduct;


import java.util.*;

/**
 * Class to represent the annotation over a lipid
 */
public class Annotation {
    private final Lipid lipid;
    private final double mz;
    private final double intensity; // intensity of the most abundant peak in the groupedPeaks
    private final double rtMin;
    private final IonizationMode ionizationMode;
    private String adduct; // !!TODO The adduct will be detected based on the groupedSignals
    private final Set<Peak> groupedSignals;
    private double score;
    private int totalScoresApplied;


    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param ionizationMode
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IonizationMode ionizationMode) {
        this(lipid, mz, intensity, retentionTime, ionizationMode, Collections.emptySet());
    }

    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param ionizationMode
     * @param groupedSignals
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IonizationMode ionizationMode, Set<Peak> groupedSignals) {
        this.lipid = lipid;
        this.mz = mz;
        this.rtMin = retentionTime;
        this.intensity = intensity;
        this.ionizationMode = ionizationMode;
        // !!TODO This set should be sorted according to help the program to deisotope the signals plus detect the adduct
        this.groupedSignals = new TreeSet<>(groupedSignals);
        this.score = 0;
        this.totalScoresApplied = 0;
    }

    public Lipid getLipid() {
        return lipid;
    }

    public double getMz() {
        return mz;
    }

    public double getRtMin() {
        return rtMin;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public double getIntensity() {
        return intensity;
    }

    public IonizationMode getIonizationMode() {
        return ionizationMode;
    }

    public Set<Peak> getGroupedSignals() {
        return Collections.unmodifiableSet(groupedSignals);
    }


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // !CHECK Take into account that the score should be normalized between -1 and 1
    public void addScore(double delta) {
        this.score += delta;
        this.totalScoresApplied++;
    }

    /**
     * @return The normalized score between 0 and 1 that consists on the final number divided into the times that the rule
     * has been applied.
     */
    public double getNormalizedScore() {
        return (double) this.score / this.totalScoresApplied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        return Double.compare(that.mz, mz) == 0 &&
                Double.compare(that.rtMin, rtMin) == 0 &&
                Objects.equals(lipid, that.lipid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lipid, mz, rtMin);
    }

    @Override
    public String toString() {
        return String.format("Annotation(%s, mz=%.4f, RT=%.2f, adduct=%s, intensity=%.1f, score=%d)",
                lipid.getName(), mz, rtMin, adduct, intensity, score);
    }

    public String detectAdduct(IonizationMode ionizationMode) {
        final double TOLERANCE_PPM = 10.0;

        Peak closestPeak = null;
        double closestMzDiff = Double.MAX_VALUE;

        for (Peak p : groupedSignals) {
            double diffMz = Math.abs(p.getMz() - this.mz);
            if (diffMz < closestMzDiff) {
                closestMzDiff = diffMz;
                closestPeak = p;
            }
        }

        Map<String, Double> map = Adduct.getAdductMapByIonizationMode(ionizationMode);

        if (closestPeak != null) {
            double peakMz = closestPeak.getMz();

            for (Map.Entry<String, Double> entry : map.entrySet()) {
                String adductName = entry.getKey();
                double adductMass = entry.getValue();

                int charge = Adduct.getCharge(adductName);
                int multimer = Adduct.getMultimer(adductName);

                try {
                    double monoMass = MassTransformation.getMonoisotopicMassFromMZ(peakMz, adductName, ionizationMode);
                    double expectedMz = MassTransformation.getMzFromMonoisotopicMass(monoMass, adductName, ionizationMode);

                    double ppmError = Math.abs((expectedMz - this.mz) / this.mz) * 1_000_000;
                    if (ppmError <= TOLERANCE_PPM) {
                        this.adduct = adductName;
                        return adductName;
                    }
                } catch (IllegalArgumentException e) {
                    // skip invalid charge/multimer combinations
                    continue;
                }
            }
        }

        return "No Peak match with AdductList";
    }

}

package lipid;

public class Peak  implements Comparable<Peak> {

    private final double mz;
    private final double intensity;

    public Peak(double mz, double intensity) {
        this.mz = mz;
        this.intensity = intensity;
    }

    public double getMz() {
        return mz;
    }

    public double getIntensity() {
        return intensity;
    }

    @Override
    public String toString() {
        return String.format("Peak(mz=%.4f, intensity=%.2f)", mz, intensity);
    }

    @Override
    public int hashCode() {
        return Double.hashCode(mz) * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Peak other)) return false;
        return Double.compare(mz, other.mz) == 0;
    }


    // Implementing Comparable
    @Override
    public int compareTo(Peak otherPeak) {
        // First, compare by mz (mass-to-charge ratio)
        int mzComparison = Double.compare(this.mz, otherPeak.mz);

        if (mzComparison != 0) {
            return mzComparison; // If mz differs, return the comparison result
        }

        // If mz is the same, compare by intensity
        return Double.compare(this.intensity, otherPeak.intensity);
    }
}

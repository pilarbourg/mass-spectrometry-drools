package adduct;

public class Adduct {

    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param experimentalMass mz
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     *
     * @return the mass difference within the tolerance respecting to the
     * massToSearch
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct) {
        Double massToSearch;
        // !! TODO METHOD

        /*
        if Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect

        if Adduct is double or triple charged the formula is M =( mz - adductMass ) * charge

        if adduct is a dimer the formula is M =  (mz - adductMass) / numberOfMultimer


        return monoisotopicMass;

         */
        return null;
    }

}

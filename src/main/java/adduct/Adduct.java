package adduct;

import java.util.Map;

public class Adduct {


    /**
     * Calculate the mass to search depending on the adduct hypothesis
     * @param mz experimental mass
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return the mass difference within the tolerance respecting to the
     *      * massToSearch
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct) {
        Double massToSearch;

        Double adductMass = getAdductMass(adduct);
        int charge = getCharge(adduct);
        int multimer = getMultimer(adduct);

        if (charge == 1 && multimer == 1) {
            // Single charged, single molecule: if Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect
            massToSearch = mz + adductMass;
        } else if (charge > 1 && multimer == 1) {
            // Multi-charged single molecule: if Adduct is double or triple charged the formula is M =( mz - adductMass ) * charge
            massToSearch = (mz + adductMass) * charge;
        } else if (multimer > 1) {
            // Multimers (dimers, trimers, etc.): if adduct is a dimer the formula is M =  (mz - adductMass) / numberOfMultimer
            massToSearch = (mz + adductMass) / multimer;
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
        return massToSearch;
    }

    private static Double getAdductMass(String adduct) {
        Map<String, Double> posMap = AdductList.MAPMZPOSITIVEADDUCTS;
        Map<String, Double> negMap = AdductList.MAPMZNEGATIVEADDUCTS;

        if (posMap.containsKey(adduct)) {
            return Double.valueOf(posMap.get(adduct));
        } else if (negMap.containsKey(adduct)) {
            return Double.valueOf(negMap.get(adduct));
        } else {
            throw new RuntimeException("Adduct not found: " + adduct);
        }
    }

    public static int getCharge(String adduct) {
        adduct = adduct.trim();
        if (adduct.contains("3+")) {
            return 3;
        } else if (adduct.contains("2+")) {
            return 2;
        } else if (adduct.contains("3−") || adduct.contains("3-")) {
            return 3;
        } else if (adduct.contains("2−") || adduct.contains("2-")) {
            return 2;
        } else {
            return 1; // default single charge
        }
    }

    private static int getMultimer(String adduct) {
        if (adduct.startsWith("[2M")) {
            return 2;
        } else if (adduct.startsWith("[3M")) {
            return 3;
        } else {
            return 1; // single molecule
        }
    }

}

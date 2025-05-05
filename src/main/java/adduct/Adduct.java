package adduct;

import lipid.IonizationMode;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println("MZ: " + mz + " Adduct: " + adduct);
        Double adductMass = getAdductMass(adduct);
        System.out.println("Mass: " + adductMass + " Adduct: " + adduct);
        int charge = getCharge(adduct);
        int multimer = getMultimer(adduct);

        if (charge == 1 && multimer == 1) {
            // Single charged, single molecule: if Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect
            massToSearch = mz + adductMass;
            System.out.println("Mass to search : " + massToSearch + " Adduct: " + adduct);
        } else if (charge > 1 && multimer == 1) {
            // Multi-charged single molecule: if Adduct is double or triple charged the formula is M =( mz - adductMass ) * charge
            massToSearch = (mz * charge) - adductMass;
            System.out.println("Mass to search : " + massToSearch + " Adduct: " + adduct);
        } else if (multimer > 1) {
            // Multimers (dimers, trimers, etc.): if adduct is a dimer the formula is M =  (mz - adductMass) / numberOfMultimer
            massToSearch = (mz + adductMass) / multimer;
            System.out.println("Mass to search : " + massToSearch + " Adduct: " + adduct);
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
        return massToSearch;
    }

    private static Double getAdductMass(String adduct) {
        Map<String, Double> posMap = AdductList.MAPMZPOSITIVEADDUCTS;
        Map<String, Double> negMap = AdductList.MAPMZNEGATIVEADDUCTS;

        if (posMap.containsKey(adduct)) {
            System.out.println("ADDUCT: " + posMap.get(adduct));
            return posMap.get(adduct);
        } else if (negMap.containsKey(adduct)) {
            return negMap.get(adduct);
        } else {
            throw new RuntimeException("Adduct not found: " + adduct);
        }
    }

    public static int getCharge(String adduct) {
        adduct = adduct.trim();

        // Match patterns like 2+, 3−, 2-, etc., at the END of the adduct string
        Pattern pattern = Pattern.compile("(\\d+)?([+−-])$");
        Matcher matcher = pattern.matcher(adduct);

        if (matcher.find()) {
            String chargeStr = matcher.group(1);
            return (chargeStr != null) ? Integer.parseInt(chargeStr) : 1;
        }

        // Default to 1+ if no match
        return 1;
    }


    public static int getMultimer(String adduct) {
        if (adduct.startsWith("[2M")) {
            return 2;
        } else if (adduct.startsWith("[3M")) {
            return 3;
        } else {
            return 1; // single molecule
        }
    }


    public static Double getMzFromMonoisotopicMass(Double neutralMass, String adduct) {
        Double adductMass = getAdductMass(adduct);
        int charge = getCharge(adduct);
        int multimer = getMultimer(adduct);
        System.out.println("adduct: " + adduct + "AdductMass: " + adductMass + "Charge: " + charge + "Multimer: " + multimer);

        if (charge == 1 && multimer == 1) {
            return neutralMass + adductMass;
        } else if (charge > 1 && multimer == 1) {
            return (neutralMass + adductMass) / charge;
        } else if (multimer > 1) {
            return (neutralMass * multimer) - adductMass;
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
    }

    public static Map<String, Double> getAdductMapByIonizationMode(IonizationMode ionizationMode) {
        if (ionizationMode == IonizationMode.POSITIVE) {
            return AdductList.MAPMZPOSITIVEADDUCTS;
        } else if (ionizationMode == IonizationMode.NEGATIVE) {
            return AdductList.MAPMZNEGATIVEADDUCTS;
        } else {
            throw new IllegalArgumentException("Unknown ionization mode: " + ionizationMode);
        }
    }

}

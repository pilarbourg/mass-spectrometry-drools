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
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct, IonizationMode ionizationMode) {
        Double adductValue = getAdductMass(adduct, ionizationMode);
        int charge = getCharge(adduct);
        int multimer = getMultimer(adduct);

        if (charge == 1 && multimer == 1) { //** Single charged, single molecule: if Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect
            return getMonoMassFromSingleChargedMZ(mz, adductValue, charge);
        } else if (charge > 1 && multimer == 1) {   //** Multi-charged single molecule: if Adduct is double or triple charged the formula is M =( mz - adductMass ) * charge
            return getMonoMassFromMultiChargedMZ(mz, adductValue, charge);
        } else if (multimer > 1) {  //** Multimers (dimers, trimers, etc.): if adduct is a dimer the formula is M =  (mz - adductMass) / numberOfMultimer
            return getMonoMassFromMultimerMZ(mz, adductValue, charge, multimer);
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
    }

    /**
     * This method calculates the monoisotopic mass from a multi charged experimental mass (m/z)
     * @param experimentalMass the experimental mass as a double
     * @param adductValue the mass of the adduct as a double
     * @return the monoisotopic weight as a double
     */
    public static Double getMonoMassFromSingleChargedMZ(Double experimentalMass, Double adductValue, int charge) {
        return experimentalMass - adductValue;
    }

    private static Double getMonoMassFromMultimerMZ(double experimentalMass, double adductValue, int charge, int numberAtoms) {
        double result = experimentalMass;
        result -= adductValue;
        result /= numberAtoms;
        result = result + charge;
        return result;
    }

    private static Double getMonoMassFromMultiChargedMZ(double experimentalMass, double adductValue, int charge) {
        double result = experimentalMass;
        result += adductValue;
        result *= charge;
        result = result + charge;
        return result;
    }

    private static Double getAdductMass(String adduct, IonizationMode ionizationMode) {
        Map<String, Double> map = getAdductMapByIonizationMode(ionizationMode);

        if (map.containsKey(adduct)) {
            return map.get(adduct);
        } else {
            throw new RuntimeException("Adduct not found: " + adduct);
        }
    }

    public static int getCharge(String adduct) {
        adduct = adduct.trim();
        // Match at the end: digits followed by a charge sign (+, −, or -)
        Pattern pattern = Pattern.compile("(\\d*)([+−-])$");
        Matcher matcher = pattern.matcher(adduct);

        if (matcher.find()) {
            String chargeStr = matcher.group(1);
            int charge;

            if (chargeStr.length() > 0) {
                charge = Integer.parseInt(chargeStr);
            } else {
                charge = 1;
            }

            return charge;
        }

        return 1;
    }


    public static int getMultimer(String adduct) {
        adduct = adduct.trim();
        Pattern pattern = Pattern.compile("^\\[(\\d*)M");
        Matcher matcher = pattern.matcher(adduct);

        if (matcher.find()) {
            String multimerString = matcher.group(1);
            if (multimerString != null && !multimerString.isEmpty()) {
                return Integer.parseInt(multimerString);
            }
        }
        return 1; // Default to monomer if no number found
    }


    public static Double getMzFromMonoisotopicMass(Double neutralMass, String adduct, IonizationMode ionizationMode) {
        Double adductMass = getAdductMass(adduct, ionizationMode);
        int charge = getCharge(adduct);
        int multimer = getMultimer(adduct);

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

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass    Mass measured by MS
     * @param theoreticalMass Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double experimentalMass, Double theoreticalMass) {
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((experimentalMass - theoreticalMass) * 1000000
                / theoreticalMass));
        return ppmIncrement;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass    Mass measured by MS
     * @param ppm ppm of tolerance
     */
    public static double calculateDeltaPPM(Double experimentalMass, int ppm) {
        double deltaPPM;
        deltaPPM =  Math.round(Math.abs((experimentalMass * ppm) / 1000000));
        return deltaPPM;

    }


}

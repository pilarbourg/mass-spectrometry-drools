package lipid;

import adduct.Adduct;

import java.util.Map;

public class MassTransformation {


    /**
     * Calculate the mass to search depending on the adduct hypothesis
     * @param mz experimental mass
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return the mass difference within the tolerance respecting to the
     *      * massToSearch
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct, IonizationMode ionizationMode) {
        Double adductValue = getAdductMass(adduct, ionizationMode);
        int charge = Adduct.getCharge(adduct);
        int multimer = Adduct.getMultimer(adduct);

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
        return experimentalMass + adductValue;
    }

    private static Double getMonoMassFromMultimerMZ(double experimentalMass, double adductValue, int charge, int numberAtoms) {
        double result = experimentalMass;
        result += adductValue;
        result /= numberAtoms;
        result = result + charge;
        return result;
    }

    private static Double getMonoMassFromMultiChargedMZ(double experimentalMass, double adductValue, int charge) {
        double result = experimentalMass;
        result -= adductValue;
        result *= charge;
        result = result + charge;
        return result;
    }

    private static Double getAdductMass(String adduct, IonizationMode ionizationMode) {
        Map<String, Double> map = Adduct.getAdductMapByIonizationMode(ionizationMode);

        if (map.containsKey(adduct)) {
            return map.get(adduct);
        } else {
            throw new RuntimeException("Adduct not found: " + adduct);
        }
    }

    public static Double getMzFromMonoisotopicMass(Double neutralMass, String adduct, IonizationMode ionizationMode) {
        Double adductMass = getAdductMass(adduct, ionizationMode);
        int charge = Adduct.getCharge(adduct);
        int multimer = Adduct.getMultimer(adduct);

        if (charge == 1 && multimer == 1) {
            return neutralMass - adductMass;
        } else if (charge > 1 && multimer == 1) {
            return (neutralMass - adductMass) / charge;
        } else if (multimer > 1) {
            return (neutralMass * multimer) + adductMass;
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
    }
}

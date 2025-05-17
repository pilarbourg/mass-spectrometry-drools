package adduct;

import lipid.IonizationMode;

import java.util.Map;

public class MassTransformation {

    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param mz     experimental mass
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return the mass difference within the tolerance respecting to the
     * * massToSearch
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct, IonizationMode ionizationMode) {
        Double adductValue = getAdductMass(adduct, ionizationMode);
        int charge = Adduct.getCharge(adduct);
        int multimer = Adduct.getMultimer(adduct);

        if (charge == 1 && multimer == 1) { //** Single charged, single molecule
            return getMonoMassFromSingleChargedMZ(mz, adductValue);
        } else if (charge > 1 && multimer == 1) {   //** Multi-charged single molecule
            return getMonoMassFromMultiChargedMZ(mz, adductValue, charge);
        } else if (multimer > 1) {  //** Multimers (dimers, trimers, etc.)
            return getMonoMassFromMultimerMZ(mz, adductValue, charge, multimer);
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
    }

    // If Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect
    public static Double getMonoMassFromSingleChargedMZ(Double experimentalMass, Double adductValue) {
        return experimentalMass + adductValue;
    }

    // If adduct is a dimer or multimer the formula is M =  (mz +- adductMass) / numberOfMultimer
    private static Double getMonoMassFromMultimerMZ(double experimentalMass, double adductValue, int charge, int numberAtoms) {
        return ((experimentalMass + adductValue) * charge) / numberAtoms;
    }

    // If Adduct is double or triple charged the formula is M = ( mz +- adductMass ) * charge
    private static Double getMonoMassFromMultiChargedMZ(double experimentalMass, double adductValue, int charge) {
        return (experimentalMass + adductValue) * charge;
    }

    private static Double getAdductMass(String adduct, IonizationMode ionizationMode) {
        Map<String, Double> map = Adduct.getAdductMapByIonizationMode(ionizationMode);

        if (map.containsKey(adduct)) {
            return map.get(adduct);
        } else {
            throw new RuntimeException("Adduct not found: " + adduct);
        }
    }

    /**
     * Calculate the mz of a monoisotopic mass with the corresponding adduct
     *
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return
     */
    public static Double getMzFromMonoisotopicMass(Double neutralMass, String adduct, IonizationMode ionizationMode) {
        Double adductMass = getAdductMass(adduct, ionizationMode);
        int charge = Adduct.getCharge(adduct);
        int multimer = Adduct.getMultimer(adduct);

        if (charge == 1 && multimer == 1) {
            return getMzFromSingleChargedMonoMass(neutralMass, adductMass);
        } else if (charge > 1 && multimer == 1) {
            return getMzFromMultiChargedMonoMass(neutralMass, adductMass, charge);
        } else if (multimer > 1) {
            return getMzFromMultimerMonoMass(neutralMass, adductMass, charge, multimer);
        } else {
            throw new RuntimeException("Unsupported adduct format: " + adduct);
        }
    }

    // If Adduct is single charge the formula is m/z = M +- adductMass. Charge is 1 so it does not affect
    public static Double getMzFromSingleChargedMonoMass(Double neutralMass, Double adductValue) {
        return neutralMass - adductValue;
    }

    // If Adduct is double or triple charged the formula is mz = M/charge +- adductMass
    private static Double getMzFromMultiChargedMonoMass(double neutralMass, double adductValue, int charge) {
        return (neutralMass / charge) - adductValue;
    }

    // If adduct is a dimer or multimer the formula is mz = M * numberOfMultimer +- adductMass
    private static Double getMzFromMultimerMonoMass(double neutralMass, double adductValue, int charge, int numberAtoms) {
        return (neutralMass * numberAtoms) - adductValue;
    }
}

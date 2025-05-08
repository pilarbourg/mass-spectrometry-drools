package adduct;

import lipid.IonizationMode;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Adduct {
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

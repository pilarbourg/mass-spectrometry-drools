package lipid;

import adduct.Adduct;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdductDetectionTest {
    // !!TODO For the adduct detection both regular algorithms or drools can be used as far the tests are passed.


    @Before
    public void setup() {
        // !! TODO Empty by now,you can create common objects for all tests.
    }

    @Test
    public void testGetCharge() {
        assertEquals(1, Adduct.getCharge("[M+H]+"));
        assertEquals(2, Adduct.getCharge("[M+2H]2+"));
        assertEquals(3, Adduct.getCharge("[M+3H]3+"));
    }

    @Test
    public void testGetMonoisotopicMass() {
        Double mz = 200.5;
        String adduct = "[M+H]+";
        Double expectedMass = 200.5-1.007276;

        Double result = MassTransformation.getMonoisotopicMassFromMZ(mz, adduct, IonizationMode.POSITIVE);
        assertEquals(expectedMass, result, 0.001);
    }

    @Test
    public void shouldDetectAdductBasedOnMzDifference() {
        // Given two peaks with ~21.98 Da difference (e.g., [M+H]+ and [M+Na]+)
        Peak mH = new Peak(700.500, 100000.0); // [M+H]+
        Peak mNa = new Peak(722.482, 80000.0);  // [M+Na]+
        Lipid lipid = new Lipid(1, "PC 34:1", "C42H82NO8P", "PC", 34, 1);

        double annotationMZ = 700.49999d;
        double annotationIntensity = 80000.0;
        double annotationRT = 6.5d;
        Annotation annotation = new Annotation(lipid, annotationMZ, annotationIntensity, annotationRT, IonizationMode.POSITIVE, Set.of(mH, mNa));

        // Then
        assertNotNull("[M+H]+ should be detected", annotation.detectAdduct(IonizationMode.POSITIVE));
        assertEquals( "Adduct inferred from lowest mz in group","[M+H]+", annotation.detectAdduct(IonizationMode.POSITIVE));
    }


    @Test
    public void shouldDetectLossOfWaterAdduct() {
        Peak mh = new Peak(700.500, 90000.0);        // [M+H]+
        Peak mhH2O = new Peak(682.4894, 70000.0);     // [M+H–H₂O]+, ~18.0106 Da less

        Lipid lipid = new Lipid(1, "PE 36:2", "C41H78NO8P", "PE", 36, 2);
        Annotation annotation = new Annotation(lipid, mh.getMz(), mh.getIntensity(), 7.5d, IonizationMode.POSITIVE, Set.of(mh, mhH2O));

        assertNotNull("[M+H]+ should be detected",   annotation.detectAdduct(IonizationMode.POSITIVE));
        assertEquals( "Adduct inferred from lowest mz in group","[M+H]+",  annotation.detectAdduct(IonizationMode.POSITIVE));
    }

    @Test
    public void shouldDetectDoublyChargedAdduct() {
        // Assume real M = (700.500 - 1.0073) = 699.4927
        // So [M+2H]2+ = (M + 2.0146) / 2 = 350.7536
        Peak singlyCharged = new Peak(700.500, 100000.0);  // [M+H]+
        Peak doublyCharged = new Peak(350.754, 85000.0);   // [M+2H]2+

        Lipid lipid = new Lipid(3, "TG 54:3", "C57H104O6", "TG", 54, 3);
        Annotation annotation = new Annotation(lipid, singlyCharged.getMz(), singlyCharged.getIntensity(), 10d, IonizationMode.POSITIVE, Set.of(singlyCharged, doublyCharged));

        assertNotNull("[M+H]+ should be detected",  annotation.detectAdduct(IonizationMode.POSITIVE));
        assertEquals( "Adduct inferred from lowest mz in group","[M+H]+",  annotation.detectAdduct(IonizationMode.POSITIVE));
    }

}

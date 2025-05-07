package lipid;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationTest {

    private Annotation annotation;

    @Before
    public void setup() {
        annotation = new Annotation(
                new Lipid(1, "TG(16:0/16:0/16:0)", "C51H98O6", "TG", 51, 2),
                500.0, 1000.0, 10.0
        );
    }

    @Test
    public void shouldGiveHighNormalizedScore() {
        annotation.addScore(5);
        assertEquals(1.0, annotation.getScore(), 0.001);

        annotation.addScore(5);
        assertEquals(1.0, annotation.getScore(), 0.001);

        annotation.addScore(5);
        assertEquals(1.0, annotation.getScore(), 0.001);
    }

    @Test
    public void shouldGiveLowerNormalizedScore() {
        annotation.addScore(5);
        assertEquals(1.0, annotation.getScore(), 0.001);

        annotation.addScore(1);
        assertEquals(0.75, annotation.getScore(), 0.001);

        annotation.addScore(0);
        assertEquals(0.666, annotation.getScore(), 0.001);
    }
}
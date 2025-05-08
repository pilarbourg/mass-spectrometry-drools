package main;

import lipid.Annotation;
import lipid.IonizationMode;
import lipid.Lipid;
import lipid.LipidScoreUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        LipidScoreUnit lipidScoreUnit = new LipidScoreUnit();

        // Sample lipids
        Lipid lipid1 = new Lipid(1, "Lipid A", "C20H40O2", "Type1", 20, 2);
        Lipid lipid2 = new Lipid(2, "Lipid B", "C22H42O2", "Type1", 22, 2);
        Lipid lipid3 = new Lipid(3, "TG 56:3", "C59H108O6", "TG", 56, 3); // MZ of [M+H]+ = 913.82187

        // Sample annotations
        Annotation annotation1 = new Annotation(lipid1, 500.0, 1200.0, 1.0, IonizationMode.POSITIVE);
        Annotation annotation2 = new Annotation(lipid2, 520.0, 1100.0, 2.0, IonizationMode.POSITIVE);
        Annotation annotation3 = new Annotation(lipid3, 913.822, 10E5, 11d, IonizationMode.POSITIVE);


        RuleUnitInstance<LipidScoreUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(lipidScoreUnit);


        try {
            // TODO INTRODUCE THE CODE IF DESIRED TO INSERT FACTS AND TRIGGER RULES
            // Insert facts into unit's DataStores
            lipidScoreUnit.getLipids().add(lipid1);
            lipidScoreUnit.getLipids().add(lipid2);
            lipidScoreUnit.getLipids().add(lipid3);
            lipidScoreUnit.getAnnotations().add(annotation1);
            lipidScoreUnit.getAnnotations().add(annotation2);
            lipidScoreUnit.getAnnotations().add(annotation3);

            instance.fire();
            // TODO INTRODUCE THE QUERIES IF DESIRED

        } finally {
            instance.close();
        }
    }
}

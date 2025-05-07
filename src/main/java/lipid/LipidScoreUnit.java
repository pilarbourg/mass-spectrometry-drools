package lipid;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

import java.util.HashSet;

public class LipidScoreUnit implements RuleUnitData {

    // !TODO insert here the code to store the data structures containing the facts where the rules will be applied
    //** DONE?
    private final DataStore<Annotation> annotations;
    private final DataStore<Lipid> lipids;
    private final DataStore<Peak> peaks;


    public LipidScoreUnit() {
        this(DataSource.createStore(), DataSource.createStore(), DataSource.createStore());
    }

    public LipidScoreUnit(DataStore<Annotation> annotations, DataStore<Lipid> lipids, DataStore<Peak> peaks) {
        this.annotations = annotations;
        this.lipids = lipids;
        this.peaks = peaks;

    }

    public DataStore<Annotation> getAnnotations() {
        return annotations;
    }

    public DataStore<Lipid> getLipids() {
        return lipids;
    }

    public DataStore<Peak> getPeaks() {
        return peaks;
    }

}

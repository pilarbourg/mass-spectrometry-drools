package lipid;

import java.util.Map;

public class LipidTypePriorityOrder {
    // The higher the value, the more retained (i.e. later elution)
    private static final Map<String, Integer> PRIORITY = Map.of(
            "PG", 0,
            "PE", 1,
            "PI", 2,
            "PA", 3,
            "PS", 4,
            "PC", 10  // PS <<
    );

    public static int compare(String type1, String type2) {
        return Integer.compare(getPriority(type1), getPriority(type2));
    }

    public static int getPriority(String type) {
        return PRIORITY.getOrDefault(type, Integer.MAX_VALUE); // Unknown = lowest priority
    }

    public static boolean isMoreRetained(String type1, String type2) {
        return compare(type1, type2) > 0;
    }
}

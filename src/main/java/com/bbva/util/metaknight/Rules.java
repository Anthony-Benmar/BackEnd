package com.bbva.util.metaknight;

import java.util.*;

public class Rules {

    private static final String IS_CRITICAL = "isCritical";
    private static final String WITH_REFUSALS = "withRefusals";
    private static final String MIN_THRESHOLD = "minThreshold";
    private static final String TARGET_THRESHOLD = "targetThreshold";
    private static final String ACCEPTANCE_MIN = "acceptanceMin";
    private static final String CLASS = "class";
    private static final String CONFIG = "config";

    /**
     * Aplicar regla de duplicados
     * Equivalente a apply_duplicate_rule en Python
     */
    public Map<String, Object> applyDuplicateRule(List<String> keys, String functionalId) {
        Map<String, Object> config = new HashMap<>();
        config.put("columns", keys);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, true);
        config.put(MIN_THRESHOLD, 100);
        config.put(TARGET_THRESHOLD, 100);
        config.put(ACCEPTANCE_MIN, 100);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.consistence.DuplicateRule");
        rule.put(CONFIG, config);

        return rule;
    }

    /**
     * Aplicar regla de validez (not null)
     * Equivalente a apply_validity_rule en Python
     */
    public Map<String, Object> applyValidityRule(String column, String functionalId) {
        Map<String, Object> config = new HashMap<>();
        config.put("column", column);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, true);
        config.put(ACCEPTANCE_MIN, 100);
        config.put(MIN_THRESHOLD, 100);
        config.put(TARGET_THRESHOLD, 100);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.validity.ValidityRule");
        rule.put(CONFIG, config);

        return rule;
    }

    /**
     * Aplicar regla de formato
     * Equivalente a apply_format_rule en Python
     */
    public Map<String, Object> applyFormatRule(String column, String regularExpression, String functionalId) {
        Map<String, Object> config = new HashMap<>();
        config.put("columns", Arrays.asList(column));
        config.put("format", regularExpression);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, true);
        config.put(ACCEPTANCE_MIN, 100);
        config.put(MIN_THRESHOLD, 100);
        config.put(TARGET_THRESHOLD, 100);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.format.FormatRule");
        rule.put(CONFIG, config);

        return rule;
    }

    /**
     * Regla de staging (completitud)
     * Equivalente a staging_rule en Python
     */
    public Map<String, Object> stagingRule(String process) {
        if (!"staging".equals(process)) {
            return Collections.emptyMap();
        }

        Map<String, Object> config = new HashMap<>();
        config.put(ACCEPTANCE_MIN, 100.0);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, false);
        config.put(MIN_THRESHOLD, 100.0);
        config.put(TARGET_THRESHOLD, 100.0);
        config.put("id", "2819d9f1b1");

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.completeness.CompletenessRule");
        rule.put(CONFIG, config);

        return rule;
    }
}
package com.bbva.util.metaknight;

import java.util.*;

public class Rules {

    /**
     * Aplicar regla de duplicados
     * Equivalente a apply_duplicate_rule en Python
     */
    public Map<String, Object> applyDuplicateRule(List<String> keys, String functionalId) {
        Map<String, Object> config = new HashMap<>();
        config.put("columns", keys);
        config.put("isCritical", true);
        config.put("withRefusals", true);
        config.put("minThreshold", 100);
        config.put("targetThreshold", 100);
        config.put("acceptanceMin", 100);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put("class", "com.datio.hammurabi.rules.consistence.DuplicateRule");
        rule.put("config", config);

        return rule;
    }

    /**
     * Aplicar regla de validez (not null)
     * Equivalente a apply_validity_rule en Python
     */
    public Map<String, Object> applyValidityRule(String column, String functionalId) {
        Map<String, Object> config = new HashMap<>();
        config.put("column", column);
        config.put("isCritical", true);
        config.put("withRefusals", true);
        config.put("acceptanceMin", 100);
        config.put("minThreshold", 100);
        config.put("targetThreshold", 100);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put("class", "com.datio.hammurabi.rules.validity.NotNullValidationRule");
        rule.put("config", config);

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
        config.put("isCritical", true);
        config.put("withRefusals", true);
        config.put("acceptanceMin", 100);
        config.put("minThreshold", 100);
        config.put("targetThreshold", 100);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put("class", "com.datio.hammurabi.rules.validity.FormatValidationRule");
        rule.put("config", config);

        return rule;
    }

    /**
     * Regla de staging (completitud)
     * Equivalente a staging_rule en Python
     */
    public Map<String, Object> stagingRule(String process) {
        if (!"staging".equals(process)) {
            return null;
        }

        Map<String, Object> config = new HashMap<>();
        config.put("acceptanceMin", 100.0);
        config.put("isCritical", true);
        config.put("withRefusals", false);
        config.put("minThreshold", 100.0);
        config.put("targetThreshold", 100.0);
        config.put("id", "2819d9f1b1");

        Map<String, Object> rule = new HashMap<>();
        rule.put("class", "com.datio.hammurabi.rules.completeness.CompletenessRule");
        rule.put("config", config);

        return rule;
    }
}
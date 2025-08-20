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
        rule.put(CLASS, "com.datio.hammurabi.rules.validity.NotNullValidationRule");
        rule.put(CONFIG, config);

        return rule;
    }

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
        rule.put(CLASS, "com.datio.hammurabi.rules.validity.FormatValidationRule");
        rule.put(CONFIG, config);

        return rule;
    }
    public Map<String, Object> rawCompletenessRule(String stagingPath, String artifactoryPath, String functionalId) {
        Map<String, Object> dataValuesOptions = new HashMap<>();
        dataValuesOptions.put("castMode", "notPermissive");
        dataValuesOptions.put("mode", "FAILFAST");
        dataValuesOptions.put("charset", "UTF-8");

        Map<String, Object> dataValuesSchema = new HashMap<>();
        dataValuesSchema.put("path", artifactoryPath);

        Map<String, Object> dataValues = new HashMap<>();
        dataValues.put("options", dataValuesOptions);
        dataValues.put("paths", Arrays.asList("\"" + stagingPath + "\""));
        dataValues.put("schema", dataValuesSchema);
        dataValues.put("type", "fixed");

        Map<String, Object> config = new HashMap<>();
        config.put("dataValues", dataValues);
        config.put(ACCEPTANCE_MIN, 100);
        config.put(MIN_THRESHOLD, 100);
        config.put(TARGET_THRESHOLD, 100);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, false);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.completeness.BasicPerimeterCompletenessRule");
        rule.put(CONFIG, config);
        return rule;
    }

    public Map<String, Object> masterCompletenessRule(String rawPath, String artifactoryPath, String subset, String functionalId) {
        Map<String, Object> dataValuesOptions = new HashMap<>();
        dataValuesOptions.put("includeMetadataAndDeleted", true);

        Map<String, Object> dataValuesSchema = new HashMap<>();
        dataValuesSchema.put("path", artifactoryPath);

        Map<String, Object> dataValues = new HashMap<>();
        dataValues.put("applyConversions", false);
        dataValues.put("castMode", "notPermissive");
        dataValues.put("options", dataValuesOptions);
        dataValues.put("paths", Arrays.asList(("\"" + rawPath + "\"")));
        dataValues.put("schema", dataValuesSchema);
        dataValues.put("type", "avro");

        Map<String, Object> config = new HashMap<>();
        config.put("id", functionalId);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, false);
        config.put(MIN_THRESHOLD, 100.0);
        config.put(TARGET_THRESHOLD, 100.0);
        config.put(ACCEPTANCE_MIN, 100.0);
        config.put("dataValuesSubset", subset);
        config.put("dataValues", dataValues);

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.completeness.ConditionalPerimeterCompletenessRule");
        rule.put(CONFIG, config);
        return rule;
    }
    public Map<String, Object> hammurabiL1TRule(String masterPath, String uuaa, String dfMasterName, String subset, String functionalId) {
        Map<String, Object> dataValuesOptions = new HashMap<>();
        dataValuesOptions.put("includeMetadataAndDeleted", true);
        dataValuesOptions.put("overrideSchema", true);

        Map<String, Object> dataValuesSchema = new HashMap<>();
        dataValuesSchema.put("path", "${ARTIFACTORY_UNIQUE_CACHE}\"/artifactory/\"${SCHEMAS_REPOSITORY}\"/schemas/pe/" + uuaa + "/master/" + dfMasterName + "/latest/" + dfMasterName + ".input.schema\"");

        Map<String, Object> dataValues = new HashMap<>();
        dataValues.put("options", dataValuesOptions);
        dataValues.put("paths", Arrays.asList(("\"" + masterPath + "\"")));
        dataValues.put("schema", dataValuesSchema);
        dataValues.put("type", "parquet");

        Map<String, Object> config = new HashMap<>();
        config.put("dataValues", dataValues);
        config.put("dataValuesSubset", subset);
        config.put(ACCEPTANCE_MIN, 100.0);
        config.put(MIN_THRESHOLD, 100.0);
        config.put(TARGET_THRESHOLD, 100.0);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, false);
        config.put("id", functionalId);

        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, "com.datio.hammurabi.rules.completeness.ConditionalPerimeterCompletenessRule");
        rule.put(CONFIG, config);

        return rule;
    }

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
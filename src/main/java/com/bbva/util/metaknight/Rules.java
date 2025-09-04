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
    private static final String OPTIONS = "options";
    private static final String PATHS = "paths";
    private static final String SCHEMA = "schema";
    private static final String DATA_VALUES = "dataValues";

    private Map<String, Object> createBaseConfig(String functionalId, boolean withRefusals) {
        Map<String, Object> config = new HashMap<>();
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, withRefusals);
        config.put(MIN_THRESHOLD, 100);
        config.put(TARGET_THRESHOLD, 100);
        config.put(ACCEPTANCE_MIN, 100);
        config.put("id", "\"" + functionalId + "\"");
        return config;
    }
    private Map<String, Object> createCompletenessBaseConfig(String functionalId, boolean withRefusals) {
        Map<String, Object> config = new HashMap<>();
        config.put(ACCEPTANCE_MIN, 100.0);
        config.put(MIN_THRESHOLD, 100.0);
        config.put(TARGET_THRESHOLD, 100.0);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, withRefusals);
        config.put("id", "\"" + functionalId + "\"");
        return config;
    }

    private Map<String, Object> createCompletenessBaseConfigInteger(String functionalId, boolean withRefusals) {
        Map<String, Object> config = new HashMap<>();
        config.put(ACCEPTANCE_MIN, 100);
        config.put(MIN_THRESHOLD, 100);
        config.put(TARGET_THRESHOLD, 100);
        config.put(IS_CRITICAL, true);
        config.put(WITH_REFUSALS, withRefusals);
        config.put("id", "\"" + functionalId + "\"");
        return config;
    }

    private Map<String, Object> createRule(String className, Map<String, Object> config) {
        Map<String, Object> rule = new HashMap<>();
        rule.put(CLASS, className);
        rule.put(CONFIG, config);
        return rule;
    }

    private Map<String, Object> createDataValues(String type, List<String> paths, String schemaPath, Map<String, Object> options) {
        Map<String, Object> dataValuesSchema = new HashMap<>();
        dataValuesSchema.put("path", schemaPath);

        Map<String, Object> dataValues = new HashMap<>();
        dataValues.put(PATHS, paths);
        dataValues.put(SCHEMA, dataValuesSchema);
        dataValues.put("type", "\"" + type + "\"");

        if (options != null) {
            dataValues.put(OPTIONS, options);
        }

        return dataValues;
    }

    public Map<String, Object> applyDuplicateRule(List<String> keys, String functionalId) {
        Map<String, Object> config = createBaseConfig(functionalId, true);
        config.put("columns", keys);

        return createRule("com.datio.hammurabi.rules.consistence.DuplicateRule", config);
    }

    public Map<String, Object> applyValidityRule(String column, String functionalId) {
        Map<String, Object> config = createBaseConfig(functionalId, true);
        config.put("column", column);

        return createRule("com.datio.hammurabi.rules.validity.NotNullValidationRule", config);
    }

    public Map<String, Object> applyFormatRule(String column, String regularExpression, String functionalId) {
        Map<String, Object> config = createBaseConfig(functionalId, true);
        config.put("column", column);
        config.put("format", regularExpression);

        return createRule("com.datio.hammurabi.rules.validity.FormatValidationRule", config);
    }

    public Map<String, Object> rawCompletenessRule(String stagingPath, String artifactoryPath, String functionalId) {
        Map<String, Object> dataValuesOptions = new HashMap<>();
        dataValuesOptions.put("castMode", "notPermissive");
        dataValuesOptions.put("mode", "FAILFAST");
        dataValuesOptions.put("charset", "UTF-8");

        Map<String, Object> dataValues = createDataValues("fixed",
                Arrays.asList("\"" + stagingPath + "\""),
                artifactoryPath,
                dataValuesOptions);

        Map<String, Object> config = createCompletenessBaseConfigInteger(functionalId, false);
        config.put(DATA_VALUES, dataValues);

        return createRule("com.datio.hammurabi.rules.completeness.BasicPerimeterCompletenessRule", config);
    }

    public Map<String, Object> masterCompletenessRule(String rawPath, String artifactoryPath, String subset, String functionalId) {
        Map<String, Object> dataValuesOptions = new HashMap<>();
        dataValuesOptions.put("includeMetadataAndDeleted", true);

        Map<String, Object> dataValues = createDataValues("avro",
                Arrays.asList("\"" + rawPath + "\""),
                artifactoryPath,
                dataValuesOptions);
        dataValues.put("applyConversions", false);
        dataValues.put("castMode", "notPermissive");

        Map<String, Object> config = createCompletenessBaseConfig(functionalId, false);
        config.put("dataValuesSubset", "\"" + subset + "\"");
        config.put(DATA_VALUES, dataValues);

        return createRule("com.datio.hammurabi.rules.completeness.ConditionalPerimeterCompletenessRule", config);
    }

    public Map<String, Object> hammurabiL1TRule(String masterPath, String uuaa, String dfMasterName, String subset, String functionalId) {
        Map<String, Object> dataValuesOptions = new HashMap<>();
        dataValuesOptions.put("includeMetadataAndDeleted", "\"" + true + "\"");
        dataValuesOptions.put("overrideSchema", "\"" + true + "\"");

        String schemaPath = "${ARTIFACTORY_UNIQUE_CACHE}\"/artifactory/\"${SCHEMAS_REPOSITORY}\"/schemas/pe/"
                + uuaa + "/master/" + dfMasterName + "/latest/" + dfMasterName + ".input.schema\"";

        Map<String, Object> dataValues = createDataValues("parquet",
                Arrays.asList("\"" + masterPath + "\""),
                schemaPath,
                dataValuesOptions);

        Map<String, Object> config = createCompletenessBaseConfig(functionalId, false);
        config.put("dataValuesSubset", "\"" + subset + "\"");
        config.put(DATA_VALUES, dataValues);

        return createRule("com.datio.hammurabi.rules.completeness.ConditionalPerimeterCompletenessRule", config);
    }

    public Map<String, Object> stagingRule(String process) {
        if (!"staging".equals(process)) {
            return Collections.emptyMap();
        }

        Map<String, Object> config = createCompletenessBaseConfig("2819d9f1b1", false);

        return createRule("com.datio.hammurabi.rules.completeness.CompletenessRule", config);
    }
}
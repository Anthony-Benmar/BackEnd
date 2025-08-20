package com.bbva.util.metaknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    private Rules rules;

    @BeforeEach
    void setUp() {
        rules = new Rules();
    }

    @Test
    void testApplyDuplicateRule_Success() {
        List<String> keys = Arrays.asList("field1", "field2", "field3");
        String functionalId = "test123";

        Map<String, Object> result = rules.applyDuplicateRule(keys, functionalId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.consistence.DuplicateRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(7, config.size());

        assertEquals(keys, config.get("columns"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(true, config.get("withRefusals"));
        assertEquals(100, config.get("minThreshold"));
        assertEquals(100, config.get("targetThreshold"));
        assertEquals(100, config.get("acceptanceMin"));
        assertEquals(functionalId, config.get("id"));
    }

    @Test
    void testApplyDuplicateRule_EmptyKeys() {
        List<String> emptyKeys = new ArrayList<>();
        String functionalId = "test123";

        Map<String, Object> result = rules.applyDuplicateRule(emptyKeys, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(emptyKeys, config.get("columns"));
        assertTrue(((List<?>) config.get("columns")).isEmpty());
    }

    @Test
    void testApplyDuplicateRule_NullFunctionalId() {
        List<String> keys = Arrays.asList("field1", "field2");
        String functionalId = null;

        Map<String, Object> result = rules.applyDuplicateRule(keys, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("id"));
    }

    @Test
    void testApplyValidityRule_Success() {
        String column = "test_column";
        String functionalId = "validity123";

        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.validity.NotNullValidationRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(7, config.size());

        assertEquals(column, config.get("column"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(true, config.get("withRefusals"));
        assertEquals(100, config.get("acceptanceMin"));
        assertEquals(100, config.get("minThreshold"));
        assertEquals(100, config.get("targetThreshold"));
        assertEquals(functionalId, config.get("id"));
    }

    @Test
    void testApplyValidityRule_NullColumn() {
        String column = null;
        String functionalId = "validity123";

        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("column"));
    }

    @Test
    void testApplyValidityRule_EmptyColumn() {
        String column = "";
        String functionalId = "validity123";

        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals("", config.get("column"));
    }

    @Test
    void testApplyFormatRule_Success() {
        String column = "test_column";
        String regularExpression = "^[A-Z]{3}[0-9]{3}$";
        String functionalId = "format123";

        Map<String, Object> result = rules.applyFormatRule(column, regularExpression, functionalId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.validity.FormatValidationRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(8, config.size());

        assertEquals(Arrays.asList(column), config.get("columns"));
        assertEquals(regularExpression, config.get("format"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(true, config.get("withRefusals"));
        assertEquals(100, config.get("acceptanceMin"));
        assertEquals(100, config.get("minThreshold"));
        assertEquals(100, config.get("targetThreshold"));
        assertEquals(functionalId, config.get("id"));
    }

    @Test
    void testApplyFormatRule_NullRegex() {
        String column = "test_column";
        String regularExpression = null;
        String functionalId = "format123";

        Map<String, Object> result = rules.applyFormatRule(column, regularExpression, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("format"));
    }

    @Test
    void testApplyFormatRule_EmptyRegex() {
        String column = "test_column";
        String regularExpression = "";
        String functionalId = "format123";

        Map<String, Object> result = rules.applyFormatRule(column, regularExpression, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals("", config.get("format"));
    }

    @Test
    void testStagingRule_ValidProcess() {
        String process = "staging";

        Map<String, Object> result = rules.stagingRule(process);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.completeness.CompletenessRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(6, config.size());

        assertEquals(100.0, config.get("acceptanceMin"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(false, config.get("withRefusals"));
        assertEquals(100.0, config.get("minThreshold"));
        assertEquals(100.0, config.get("targetThreshold"));
        assertEquals("2819d9f1b1", config.get("id"));
    }

    @Test
    void testStagingRule_InvalidInputs() {
        assertStagingRuleReturnsEmpty("invalid");
        assertStagingRuleReturnsEmpty(null);
        assertStagingRuleReturnsEmpty("");
        assertStagingRuleReturnsEmpty("STAGING");
    }

    private void assertStagingRuleReturnsEmpty(String process) {
        Map<String, Object> result = rules.stagingRule(process);

        assertNotNull(result, "Result should not be null for process: " + process);
        assertTrue(result.isEmpty(), "Result should be empty for process: " + process);
    }

    @Test
    void testApplyDuplicateRule_SingleKey() {
        List<String> keys = Arrays.asList("single_field");
        String functionalId = "single123";

        Map<String, Object> result = rules.applyDuplicateRule(keys, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(1, ((List<?>) config.get("columns")).size());
        assertEquals("single_field", ((List<?>) config.get("columns")).get(0));
    }

    @Test
    void testRuleStructure_AllRequiredFields() {
        Map<String, Object> duplicateRule = rules.applyDuplicateRule(Arrays.asList("field1"), "id1");
        assertRuleStructure(duplicateRule, "com.datio.hammurabi.rules.consistence.DuplicateRule");

        Map<String, Object> validityRule = rules.applyValidityRule("field1", "id2");
        assertRuleStructure(validityRule, "com.datio.hammurabi.rules.validity.NotNullValidationRule");

        Map<String, Object> formatRule = rules.applyFormatRule("field1", "regex", "id3");
        assertRuleStructure(formatRule, "com.datio.hammurabi.rules.validity.FormatValidationRule");

        Map<String, Object> stagingRule = rules.stagingRule("staging");
        assertRuleStructure(stagingRule, "com.datio.hammurabi.rules.completeness.CompletenessRule");
    }

    @Test
    void testConfigValues_AreConsistent() {
        List<Map<String, Object>> allRules = Arrays.asList(
                rules.applyDuplicateRule(Arrays.asList("field1"), "id1"),
                rules.applyValidityRule("field1", "id2"),
                rules.applyFormatRule("field1", "regex", "id3")
        );

        for (Map<String, Object> rule : allRules) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) rule.get("config");

            assertEquals(100, config.get("minThreshold"));
            assertEquals(100, config.get("targetThreshold"));
            assertEquals(100, config.get("acceptanceMin"));
            assertEquals(true, config.get("isCritical"));
            assertEquals(true, config.get("withRefusals"));
        }
    }

    @Test
    void testLargeColumnList_DuplicateRule() {
        List<String> largeColumnList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeColumnList.add("column_" + i);
        }
        String functionalId = "large123";

        Map<String, Object> result = rules.applyDuplicateRule(largeColumnList, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(100, ((List<?>) config.get("columns")).size());
        assertEquals("column_0", ((List<?>) config.get("columns")).get(0));
        assertEquals("column_99", ((List<?>) config.get("columns")).get(99));
    }

    @Test
    void testSpecialCharacters_InFunctionalId() {
        String specialId = "id_with-special.chars@123!";
        String column = "test_field";

        Map<String, Object> result = rules.applyValidityRule(column, specialId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(specialId, config.get("id"));
    }

    @Test
    void testSpecialCharacters_InColumnName() {
        String column = "column-with_special.chars@field";
        String functionalId = "test123";

        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(column, config.get("column"));
    }

    private void assertRuleStructure(Map<String, Object> rule, String expectedClass) {
        assertNotNull(rule);
        assertEquals(2, rule.size());
        assertEquals(expectedClass, rule.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) rule.get("config");
        assertNotNull(config);

        assertTrue(config.containsKey("isCritical"));
        assertTrue(config.containsKey("minThreshold") || config.containsKey("acceptanceMin"));
        assertTrue(config.containsKey("id"));
    }
    @Test
    void testRawCompletenessRule_Success() {
        String stagingPath = "/in/staging/datax/test/staging_table";
        String artifactoryPath = "\"artifactory-path\"";
        String functionalId = "raw123";

        Map<String, Object> result = rules.rawCompletenessRule(stagingPath, artifactoryPath, functionalId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.completeness.BasicPerimeterCompletenessRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(7, config.size());

        assertEquals(100, config.get("acceptanceMin"));
        assertEquals(100, config.get("minThreshold"));
        assertEquals(100, config.get("targetThreshold"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(false, config.get("withRefusals"));
        assertEquals(functionalId, config.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> dataValues = (Map<String, Object>) config.get("dataValues");
        assertNotNull(dataValues);
        assertEquals(4, dataValues.size());

        assertEquals("fixed", dataValues.get("type"));
        assertEquals(Arrays.asList("\"" + stagingPath + "\""), dataValues.get("paths"));

        @SuppressWarnings("unchecked")
        Map<String, Object> schema = (Map<String, Object>) dataValues.get("schema");
        assertEquals(artifactoryPath, schema.get("path"));

        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) dataValues.get("options");
        assertEquals("notPermissive", options.get("castMode"));
        assertEquals("FAILFAST", options.get("mode"));
        assertEquals("UTF-8", options.get("charset"));
    }

    @Test
    void testRawCompletenessRule_NullParameters() {
        String stagingPath = null;
        String artifactoryPath = null;
        String functionalId = null;

        Map<String, Object> result = rules.rawCompletenessRule(stagingPath, artifactoryPath, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> dataValues = (Map<String, Object>) config.get("dataValues");
        assertEquals(Arrays.asList("\"" + stagingPath + "\""), dataValues.get("paths"));

        @SuppressWarnings("unchecked")
        Map<String, Object> schema = (Map<String, Object>) dataValues.get("schema");
        assertNull(schema.get("path"));
    }

    @Test
    void testMasterCompletenessRule_Success() {
        String rawPath = "/data/raw/test/data/raw_table";
        String artifactoryPath = "\"raw-artifactory-path\"";
        String subset = "cutoff_date='${?DATE}'";
        String functionalId = "master123";

        Map<String, Object> result = rules.masterCompletenessRule(rawPath, artifactoryPath, subset, functionalId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.completeness.ConditionalPerimeterCompletenessRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(8, config.size());

        assertEquals(functionalId, config.get("id"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(false, config.get("withRefusals"));
        assertEquals(100.0, config.get("minThreshold"));
        assertEquals(100.0, config.get("targetThreshold"));
        assertEquals(100.0, config.get("acceptanceMin"));
        assertEquals(subset, config.get("dataValuesSubset"));

        // Validar dataValues
        @SuppressWarnings("unchecked")
        Map<String, Object> dataValues = (Map<String, Object>) config.get("dataValues");
        assertNotNull(dataValues);
        assertEquals(6, dataValues.size());

        assertEquals(false, dataValues.get("applyConversions"));
        assertEquals("notPermissive", dataValues.get("castMode"));
        assertEquals("avro", dataValues.get("type"));
        assertEquals(Arrays.asList("\"" + rawPath + "\""), dataValues.get("paths"));

        @SuppressWarnings("unchecked")
        Map<String, Object> schema = (Map<String, Object>) dataValues.get("schema");
        assertEquals(artifactoryPath, schema.get("path"));

        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) dataValues.get("options");
        assertEquals(true, options.get("includeMetadataAndDeleted"));
    }

    @Test
    void testMasterCompletenessRule_EmptySubset() {
        String rawPath = "/data/raw/test/data/raw_table";
        String artifactoryPath = "\"artifactory-path\"";
        String subset = "";
        String functionalId = "master123";

        Map<String, Object> result = rules.masterCompletenessRule(rawPath, artifactoryPath, subset, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals("", config.get("dataValuesSubset"));
    }

    @Test
    void testHammurabiL1TRule_Success() {
        String masterPath = "/data/master/test/data/master_table";
        String uuaa = "test_uuaa";
        String dfMasterName = "test_master_table";
        String subset = "cutoff_date='${?DATE}' and partition1='${?PARAMETER1}'";
        String functionalId = "l1t123";

        Map<String, Object> result = rules.hammurabiL1TRule(masterPath, uuaa, dfMasterName, subset, functionalId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("com.datio.hammurabi.rules.completeness.ConditionalPerimeterCompletenessRule", result.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(8, config.size());

        assertEquals(functionalId, config.get("id"));
        assertEquals(true, config.get("isCritical"));
        assertEquals(false, config.get("withRefusals"));
        assertEquals(100.0, config.get("minThreshold"));
        assertEquals(100.0, config.get("targetThreshold"));
        assertEquals(100.0, config.get("acceptanceMin"));
        assertEquals(subset, config.get("dataValuesSubset"));

        // Validar dataValues
        @SuppressWarnings("unchecked")
        Map<String, Object> dataValues = (Map<String, Object>) config.get("dataValues");
        assertNotNull(dataValues);
        assertEquals(4, dataValues.size());

        assertEquals("parquet", dataValues.get("type"));
        assertEquals(Arrays.asList("\"" + masterPath + "\""), dataValues.get("paths"));

        @SuppressWarnings("unchecked")
        Map<String, Object> schema = (Map<String, Object>) dataValues.get("schema");
        String expectedSchemaPath = "${ARTIFACTORY_UNIQUE_CACHE}\"/artifactory/\"${SCHEMAS_REPOSITORY}\"/schemas/pe/" +
                uuaa + "/master/" + dfMasterName + "/latest/" + dfMasterName + ".input.schema\"";
        assertEquals(expectedSchemaPath, schema.get("path"));

        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) dataValues.get("options");
        assertEquals(true, options.get("includeMetadataAndDeleted"));
        assertEquals(true, options.get("overrideSchema"));
    }

    @Test
    void testHammurabiL1TRule_NullParameters() {
        String masterPath = null;
        String uuaa = null;
        String dfMasterName = null;
        String subset = null;
        String functionalId = null;

        Map<String, Object> result = rules.hammurabiL1TRule(masterPath, uuaa, dfMasterName, subset, functionalId);

        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("id"));
        assertNull(config.get("dataValuesSubset"));

        @SuppressWarnings("unchecked")
        Map<String, Object> dataValues = (Map<String, Object>) config.get("dataValues");
        assertEquals(Arrays.asList("\"" + masterPath + "\""), dataValues.get("paths"));
    }

    @Test
    void testAllCompletenessRules_HaveConsistentThresholds() {
        Map<String, Object> rawRule = rules.rawCompletenessRule("/path", "artifactory", "id1");
        Map<String, Object> masterRule = rules.masterCompletenessRule("/path", "artifactory", "subset", "id2");
        Map<String, Object> l1tRule = rules.hammurabiL1TRule("/path", "uuaa", "table", "subset", "id3");

        List<Map<String, Object>> allRules = Arrays.asList(rawRule, masterRule, l1tRule);

        for (Map<String, Object> rule : allRules) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) rule.get("config");

            Object acceptanceMin = config.get("acceptanceMin");
            Object minThreshold = config.get("minThreshold");
            Object targetThreshold = config.get("targetThreshold");

            double acceptanceMinValue = acceptanceMin instanceof Integer ?
                    ((Integer) acceptanceMin).doubleValue() : (Double) acceptanceMin;
            double minThresholdValue = minThreshold instanceof Integer ?
                    ((Integer) minThreshold).doubleValue() : (Double) minThreshold;
            double targetThresholdValue = targetThreshold instanceof Integer ?
                    ((Integer) targetThreshold).doubleValue() : (Double) targetThreshold;

            assertEquals(100.0, acceptanceMinValue, "acceptanceMin should be 100");
            assertEquals(100.0, minThresholdValue, "minThreshold should be 100");
            assertEquals(100.0, targetThresholdValue, "targetThreshold should be 100");
            assertEquals(true, config.get("isCritical"));
            assertEquals(false, config.get("withRefusals"));
        }
    }

}
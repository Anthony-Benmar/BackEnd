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

        assertEquals("com.datio.hammurabi.rules.validity.ValidityRule", result.get("class"));

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

        assertEquals("com.datio.hammurabi.rules.format.FormatRule", result.get("class"));

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
        assertRuleStructure(validityRule, "com.datio.hammurabi.rules.validity.ValidityRule");

        Map<String, Object> formatRule = rules.applyFormatRule("field1", "regex", "id3");
        assertRuleStructure(formatRule, "com.datio.hammurabi.rules.format.FormatRule");

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
}
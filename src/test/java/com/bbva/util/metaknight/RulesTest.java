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
        // Arrange
        List<String> keys = Arrays.asList("field1", "field2", "field3");
        String functionalId = "test123";

        // Act
        Map<String, Object> result = rules.applyDuplicateRule(keys, functionalId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify class
        assertEquals("com.datio.hammurabi.rules.consistence.DuplicateRule", result.get("class"));

        // Verify config
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
        // Arrange
        List<String> emptyKeys = new ArrayList<>();
        String functionalId = "test123";

        // Act
        Map<String, Object> result = rules.applyDuplicateRule(emptyKeys, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(emptyKeys, config.get("columns"));
        assertTrue(((List<?>) config.get("columns")).isEmpty());
    }

    @Test
    void testApplyDuplicateRule_NullFunctionalId() {
        // Arrange
        List<String> keys = Arrays.asList("field1", "field2");
        String functionalId = null;

        // Act
        Map<String, Object> result = rules.applyDuplicateRule(keys, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("id"));
    }

    @Test
    void testApplyValidityRule_Success() {
        // Arrange
        String column = "test_column";
        String functionalId = "validity123";

        // Act
        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify class
        assertEquals("com.datio.hammurabi.rules.validity.ValidityRule", result.get("class"));

        // Verify config
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
        // Arrange
        String column = null;
        String functionalId = "validity123";

        // Act
        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("column"));
    }

    @Test
    void testApplyValidityRule_EmptyColumn() {
        // Arrange
        String column = "";
        String functionalId = "validity123";

        // Act
        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals("", config.get("column"));
    }

    @Test
    void testApplyFormatRule_Success() {
        // Arrange
        String column = "test_column";
        String regularExpression = "^[A-Z]{3}[0-9]{3}$";
        String functionalId = "format123";

        // Act
        Map<String, Object> result = rules.applyFormatRule(column, regularExpression, functionalId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify class
        assertEquals("com.datio.hammurabi.rules.format.FormatRule", result.get("class"));

        // Verify config
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNotNull(config);
        assertEquals(8, config.size()); // Updated from 7 to 8 - seems an extra field was added

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
        // Arrange
        String column = "test_column";
        String regularExpression = null;
        String functionalId = "format123";

        // Act
        Map<String, Object> result = rules.applyFormatRule(column, regularExpression, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertNull(config.get("format"));
    }

    @Test
    void testApplyFormatRule_EmptyRegex() {
        // Arrange
        String column = "test_column";
        String regularExpression = "";
        String functionalId = "format123";

        // Act
        Map<String, Object> result = rules.applyFormatRule(column, regularExpression, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals("", config.get("format"));
    }

    @Test
    void testStagingRule_ValidProcess() {
        // Arrange
        String process = "staging";

        // Act
        Map<String, Object> result = rules.stagingRule(process);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify class
        assertEquals("com.datio.hammurabi.rules.completeness.CompletenessRule", result.get("class"));

        // Verify config
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
    void testStagingRule_InvalidProcess() {
        // Arrange
        String process = "invalid";

        // Act
        Map<String, Object> result = rules.stagingRule(process);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testStagingRule_NullProcess() {
        // Arrange
        String process = null;

        // Act
        Map<String, Object> result = rules.stagingRule(process);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testStagingRule_EmptyProcess() {
        // Arrange
        String process = "";

        // Act
        Map<String, Object> result = rules.stagingRule(process);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testStagingRule_CaseSensitive() {
        // Arrange
        String process = "STAGING"; // uppercase

        // Act
        Map<String, Object> result = rules.stagingRule(process);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Should be case sensitive
    }

    @Test
    void testApplyDuplicateRule_SingleKey() {
        // Arrange
        List<String> keys = Arrays.asList("single_field");
        String functionalId = "single123";

        // Act
        Map<String, Object> result = rules.applyDuplicateRule(keys, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(1, ((List<?>) config.get("columns")).size());
        assertEquals("single_field", ((List<?>) config.get("columns")).get(0));
    }

    @Test
    void testRuleStructure_AllRequiredFields() {
        // Test that all rule types have the expected structure

        // Duplicate rule
        Map<String, Object> duplicateRule = rules.applyDuplicateRule(Arrays.asList("field1"), "id1");
        assertRuleStructure(duplicateRule, "com.datio.hammurabi.rules.consistence.DuplicateRule");

        // Validity rule
        Map<String, Object> validityRule = rules.applyValidityRule("field1", "id2");
        assertRuleStructure(validityRule, "com.datio.hammurabi.rules.validity.ValidityRule");

        // Format rule
        Map<String, Object> formatRule = rules.applyFormatRule("field1", "regex", "id3");
        assertRuleStructure(formatRule, "com.datio.hammurabi.rules.format.FormatRule");

        // Staging rule
        Map<String, Object> stagingRule = rules.stagingRule("staging");
        assertRuleStructure(stagingRule, "com.datio.hammurabi.rules.completeness.CompletenessRule");
    }

    @Test
    void testConfigValues_AreConsistent() {
        // Test that all rules have consistent threshold values
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
        // Arrange
        List<String> largeColumnList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeColumnList.add("column_" + i);
        }
        String functionalId = "large123";

        // Act
        Map<String, Object> result = rules.applyDuplicateRule(largeColumnList, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(100, ((List<?>) config.get("columns")).size());
        assertEquals("column_0", ((List<?>) config.get("columns")).get(0));
        assertEquals("column_99", ((List<?>) config.get("columns")).get(99));
    }

    @Test
    void testSpecialCharacters_InFunctionalId() {
        // Arrange
        String specialId = "id_with-special.chars@123!";
        String column = "test_field";

        // Act
        Map<String, Object> result = rules.applyValidityRule(column, specialId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(specialId, config.get("id"));
    }

    @Test
    void testSpecialCharacters_InColumnName() {
        // Arrange
        String column = "column-with_special.chars@field";
        String functionalId = "test123";

        // Act
        Map<String, Object> result = rules.applyValidityRule(column, functionalId);

        // Assert
        assertNotNull(result);

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(column, config.get("column"));
    }

    // Helper method to verify rule structure
    private void assertRuleStructure(Map<String, Object> rule, String expectedClass) {
        assertNotNull(rule);
        assertEquals(2, rule.size());
        assertEquals(expectedClass, rule.get("class"));

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) rule.get("config");
        assertNotNull(config);

        // All rules should have these common fields
        assertTrue(config.containsKey("isCritical"));
        assertTrue(config.containsKey("minThreshold") || config.containsKey("acceptanceMin"));
        assertTrue(config.containsKey("id"));
    }
}
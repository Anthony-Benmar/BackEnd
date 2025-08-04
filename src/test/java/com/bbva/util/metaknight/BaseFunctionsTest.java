package com.bbva.util.metaknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BaseFunctionsTest {

    private BaseFunctions baseFunctions;

    @BeforeEach
    void setUp() {
        baseFunctions = new BaseFunctions();
    }

    @Test
    void testConvertToCustomFormat_Success() {
        Map<String, Object> data = createTestRuleData();

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("class = \"com.datio.hammurabi.rules.validity.NotNullValidationRule\""));
        assertTrue(result.contains("config = {"));
        assertTrue(result.contains("column = \"test_column\""));
        assertTrue(result.contains("isCritical = true"));
        assertTrue(result.contains("minThreshold = 100"));
        assertTrue(result.contains("targetThreshold = 100"));
        assertTrue(result.contains("acceptanceMin = 100"));
        assertTrue(result.contains("id = \"test123\""));
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));
    }

    @Test
    void testConvertToCustomFormat_WithListValues() {
        Map<String, Object> config = new HashMap<>();
        config.put("columns", Arrays.asList("field1", "field2", "field3"));
        config.put("isCritical", true);
        config.put("threshold", 90);

        Map<String, Object> data = new HashMap<>();
        data.put("class", "com.datio.hammurabi.rules.consistence.DuplicateRule");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("columns = [\"field1\", \"field2\", \"field3\"]"));
        assertTrue(result.contains("isCritical = true"));
        assertTrue(result.contains("threshold = 90"));
    }

    @Test
    void testConvertToCustomFormat_WithBooleanValues() {
        Map<String, Object> config = new HashMap<>();
        config.put("isCritical", true);
        config.put("withRefusals", false);
        config.put("enabled", true);

        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("isCritical = true"));
        assertTrue(result.contains("withRefusals = false"));
        assertTrue(result.contains("enabled = true"));
    }

    @Test
    void testConvertToCustomFormat_WithStringValues() {
        Map<String, Object> config = new HashMap<>();
        config.put("format", "^[A-Z]{3}$");
        config.put("description", "Test description");
        config.put("id", "test_id_123");

        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("format = \"^[A-Z]{3}$\""));
        assertTrue(result.contains("description = \"Test description\""));
        assertTrue(result.contains("id = \"test_id_123\""));
    }

    @Test
    void testConvertToCustomFormat_WithNumericValues() {
        Map<String, Object> config = new HashMap<>();
        config.put("minThreshold", 85);
        config.put("targetThreshold", 95);
        config.put("acceptanceMin", 100);
        config.put("timeout", 30.5);

        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("minThreshold = 85"));
        assertTrue(result.contains("targetThreshold = 95"));
        assertTrue(result.contains("acceptanceMin = 100"));
        assertTrue(result.contains("timeout = 30.5"));
    }

    @Test
    void testConvertToCustomFormat_EmptyList() {
        Map<String, Object> config = new HashMap<>();
        config.put("columns", new ArrayList<>());
        config.put("isCritical", true);

        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("columns = []"));
        assertTrue(result.contains("isCritical = true"));
    }
    @Test
    void testGetRegularExpression_AllFormats() {
        assertRegexResult("ALPHANUMERIC(10)", "^(.{10})$");
        assertRegexResult("ALPHANUMERIC(5)", "^(.{5})$");
        assertRegexResult("ALPHANUMERIC(255)", "^(.{255})$");
        assertRegexResult("ALPHANUMERIC()", "^(.{})$");

        assertRegexResult("DATE", "^([1-9]{1}[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

        String errorMessage = "No se pudo definir RE para el formato lógico.";
        assertRegexResult("UNKNOWN_FORMAT", errorMessage);
        assertRegexResult(null, errorMessage);
        assertRegexResult("ALPHANUMERIC", errorMessage);
    }

    private void assertRegexResult(String logicalFormat, String expectedResult) {
        String result = baseFunctions.getRegularExpression(logicalFormat);
        assertEquals(expectedResult, result,
                "getRegularExpression failed for input: '" + logicalFormat + "'");
    }

    @Test
    void testConvertInputToSelectedFormat_Success() {
        Map<String, Object> options = new HashMap<>();
        options.put("overrideSchema", "true");
        options.put("includeMetadataAndDeleted", "false");

        Map<String, Object> schema = new HashMap<>();
        schema.put("path", "/path/to/schema");

        Map<String, Object> input = new HashMap<>();
        input.put("options", options);
        input.put("paths", Arrays.asList("/data/input/path"));
        input.put("schema", schema);
        input.put("type", "parquet");

        Map<String, Object> inputJson = new HashMap<>();
        inputJson.put("input", input);

        String result = baseFunctions.convertInputToSelectedFormat(inputJson);

        assertNotNull(result);
        assertTrue(result.contains("input {"));
        assertTrue(result.contains("overrideSchema = true"));
        assertTrue(result.contains("includeMetadataAndDeleted = false"));
        assertTrue(result.contains("paths = ["));
        assertTrue(result.contains("\"/data/input/path\""));
        assertTrue(result.contains("path = /path/to/schema"));
        assertTrue(result.contains("type = \"parquet\""));
    }

    @Test
    void testConvertJsonToSelectedFormat_Success() {
        Map<String, Object> inputJson = new HashMap<>();
        inputJson.put("frequencyRuleExecution", "daily");
        inputJson.put("targetPathName", "/target/path");
        inputJson.put("physicalTargetName", "target_table");
        inputJson.put("uuaa", "test_uuaa");
        inputJson.put("subset", "cutoff_date='${DATE}'");

        String result = baseFunctions.convertJsonToSelectedFormat(inputJson);

        assertNotNull(result);
        assertTrue(result.contains("dataFrameInfo {"));
        assertTrue(result.contains("cutoffDate = ${?CUTOFF_DATE}"));
        assertTrue(result.contains("frequencyRuleExecution = \"daily\""));
        assertTrue(result.contains("targetPathName = \"/target/path\""));
        assertTrue(result.contains("physicalTargetName = \"target_table\""));
        assertTrue(result.contains("uuaa = \"test_uuaa\""));
        assertTrue(result.contains("subset = \"cutoff_date='${DATE}'\""));
    }

    @Test
    void testConvertStagingInputToSelectedFormat_Success() {
        Map<String, Object> options = new HashMap<>();
        options.put("delimiter", ";");

        Map<String, Object> schema = new HashMap<>();
        schema.put("path", "/schema/path");

        Map<String, Object> input = new HashMap<>();
        input.put("options", options);
        input.put("paths", Arrays.asList("/staging/path"));
        input.put("schema", schema);
        input.put("type", "csv");

        Map<String, Object> inputJson = new HashMap<>();
        inputJson.put("input", input);

        String result = baseFunctions.convertStagingInputToSelectedFormat(inputJson);

        assertNotNull(result);
        assertTrue(result.contains("input {"));
        assertTrue(result.contains("delimiter= \";\""));
        assertTrue(result.contains("header=\"false\""));
        assertTrue(result.contains("castMode=\"notPermissive\""));
        assertTrue(result.contains("charset=\"UTF-8\""));
        assertTrue(result.contains("paths = ["));
        assertTrue(result.contains("\"/staging/path\""));
        assertTrue(result.contains("path = /schema/path"));
        assertTrue(result.contains("type = \"csv\""));
    }

    @Test
    void testConvertStagingJsonToSelectedFormat_Success() {
        Map<String, Object> inputJson = new HashMap<>();
        inputJson.put("targetPathName", "/staging/target");
        inputJson.put("physicalTargetName", "staging_table");
        inputJson.put("uuaa", "staging_uuaa");

        String result = baseFunctions.convertStagingJsonToSelectedFormat(inputJson);

        assertNotNull(result);
        assertTrue(result.contains("dataFrameInfo {"));
        assertTrue(result.contains("cutoffDate = ${?CUTOFF_DATE}"));
        assertTrue(result.contains("targetPathName = \"/staging/target\""));
        assertTrue(result.contains("physicalTargetName =  \"staging_table\""));
        assertTrue(result.contains("uuaa = \"staging_uuaa\""));
    }

    @Test
    void testConvertFinalJsonToSelectedFormat_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("configUrl", "http://config.url/path");

        Map<String, Object> inputJson = new HashMap<>();
        inputJson.put("_id", "test-job-id-01");
        inputJson.put("description", "Test job description");
        inputJson.put("params", params);

        String result = baseFunctions.convertFinalJsonToSelectedFormat(inputJson);

        assertNotNull(result);
        assertTrue(result.contains("\"_id\": \"test-job-id-01\""));
        assertTrue(result.contains("\"description\": \"Test job description\""));
        assertTrue(result.contains("\"kind\": \"processing\""));
        assertTrue(result.contains("\"configUrl\": http://config.url/path"));
        assertTrue(result.contains("\"sparkHistoryEnabled\": \"false\""));
        assertTrue(result.contains("\"runtime\": \"hammurabi-lts\""));
        assertTrue(result.contains("\"size\": \"M\""));
        assertTrue(result.contains("\"streaming\": false"));
    }
    @Test
    void testGetRuleDescription_AllFormats() {
        assertRuleDescription("ALPHANUMERIC(15)", "Comprobación del formato alfabetico de longitud 1 al 15");
        assertRuleDescription("ALPHANUMERIC(3)", "Comprobación del formato alfabetico de longitud 1 al 3");

        assertRuleDescription("DATE", "^([1-9]{1}[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

        String errorMessage = "No se pudo describir la regla para el formato lógico";
        assertRuleDescription("UNKNOWN_FORMAT", errorMessage);
        assertRuleDescription(null, errorMessage);
        assertRuleDescription("ALPHANUMERIC", errorMessage);
    }

    private void assertRuleDescription(String logicalFormat, String expectedResult) {
        String result = baseFunctions.getRuleDescription(logicalFormat);
        assertEquals(expectedResult, result,
                "getRuleDescription failed for input: '" + logicalFormat + "'");
    }
    @Test
    void testConvertToCustomFormat_NullConfig() {
        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", null);

        assertThrows(NullPointerException.class, () -> {
            baseFunctions.convertToCustomFormat(data);
        });
    }

    @Test
    void testConvertToCustomFormat_EmptyConfig() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("class = \"test.class\""));
        assertTrue(result.contains("config = {"));
        assertTrue(result.endsWith("}\n}"));
    }

    @Test
    void testConvertToCustomFormat_SingleElementList() {
        Map<String, Object> config = new HashMap<>();
        config.put("columns", Arrays.asList("single_column"));

        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("columns = [\"single_column\"]"));
    }

    @Test
    void testConvertToCustomFormat_SpecialCharactersInStrings() {
        Map<String, Object> config = new HashMap<>();
        config.put("format", "^[A-Z]{3}\\$[0-9]+$");
        config.put("description", "Test with \"quotes\" and $pecial chars");

        Map<String, Object> data = new HashMap<>();
        data.put("class", "test.class");
        data.put("config", config);

        String result = baseFunctions.convertToCustomFormat(data);

        assertNotNull(result);
        assertTrue(result.contains("format = \"^[A-Z]{3}\\$[0-9]+$\""));
        assertTrue(result.contains("description = \"Test with \"quotes\" and $pecial chars\""));
    }

    @Test
    void testConvertInputToSelectedFormat_BooleanOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("overrideSchema", true);
        options.put("includeMetadataAndDeleted", false);

        Map<String, Object> schema = new HashMap<>();
        schema.put("path", "/path/to/schema");

        Map<String, Object> input = new HashMap<>();
        input.put("options", options);
        input.put("paths", Arrays.asList("/data/input/path"));
        input.put("schema", schema);
        input.put("type", "parquet");

        Map<String, Object> inputJson = new HashMap<>();
        inputJson.put("input", input);

        String result = baseFunctions.convertInputToSelectedFormat(inputJson);

        assertNotNull(result);
        assertTrue(result.contains("overrideSchema = true"));
        assertTrue(result.contains("includeMetadataAndDeleted = false"));
    }

    private Map<String, Object> createTestRuleData() {
        Map<String, Object> config = new HashMap<>();
        config.put("column", "test_column");
        config.put("isCritical", true);
        config.put("withRefusals", true);
        config.put("minThreshold", 100);
        config.put("targetThreshold", 100);
        config.put("acceptanceMin", 100);
        config.put("id", "test123");

        Map<String, Object> data = new HashMap<>();
        data.put("class", "com.datio.hammurabi.rules.validity.NotNullValidationRule");
        data.put("config", config);

        return data;
    }
}

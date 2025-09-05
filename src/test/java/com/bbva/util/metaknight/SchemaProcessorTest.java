package com.bbva.util.metaknight;

import com.bbva.dto.metaknight.request.IngestaRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SchemaProcessorTest {

    private SchemaProcessor schemaProcessor;

    @BeforeEach
    void setUp() {
        schemaProcessor = new SchemaProcessor();
    }

    @Test
    void testInitialize_Success() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterData();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertNotNull(schemaProcessor.getKeys());
        assertEquals(2, schemaProcessor.getKeys().size());
        assertTrue(schemaProcessor.getKeys().contains("field1"));
        assertTrue(schemaProcessor.getKeys().contains("field3"));
        assertFalse(schemaProcessor.getKeys().contains("partition_field"));

        assertEquals("prefix_uuaa_table123", schemaProcessor.getDfRawName());
        assertEquals("prefix_uuaa_table123", schemaProcessor.getDfMasterName());
        assertEquals("original_staging", schemaProcessor.getOriginalStagingName());
        assertEquals("original_staging", schemaProcessor.getDfStagingName());
        assertEquals("table123", schemaProcessor.getTag());
        assertEquals("uuaa", schemaProcessor.getDfUuaa());
    }

    @Test
    void testInitialize_WithMultiplePartitions() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterData();
        IngestaRequestDto request = createValidRequest();
        request.setParticiones("partition_field,another_partition,cutoff_date");

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals(2, schemaProcessor.getKeys().size());
        assertTrue(schemaProcessor.getKeys().contains("field1"));
        assertTrue(schemaProcessor.getKeys().contains("field3"));

        assertFalse(schemaProcessor.getKeys().contains("partition_field"));
        assertFalse(schemaProcessor.getKeys().contains("another_partition"));
    }

    @Test
    void testBuildPaths() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterData();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        String expectedArtifactoryPath = "${ARTIFACTORY_UNIQUE_CACHE}\"/artifactory/\"${SCHEMAS_REPOSITORY}" +
                "\"/schemas/pe/test_uuaa/raw/prefix_uuaa_table123/latest/prefix_uuaa_table123.output.schema\"";
        assertEquals(expectedArtifactoryPath, schemaProcessor.getArtifactoryPath());

        String expectedStagingPath = "/in/staging/datax/test_uuaa/original_staging";
        String expectedRawPath = "/data/raw/uuaa/data/prefix_uuaa_table123";
        String expectedMasterPath = "/data/master/test_uuaa/data/prefix_uuaa_table123";

        assertEquals(expectedStagingPath, schemaProcessor.getDfStagingPath());
        assertEquals(expectedRawPath, schemaProcessor.getDfRawPath());
        assertEquals(expectedMasterPath, schemaProcessor.getDfMasterPath());
    }

    @Test
    void testBuildJsonIds() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterData();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("test_uuaa-pe-hmm-qlt-table123m-01", schemaProcessor.getIdJsonMaster());
        assertEquals("test_uuaa-pe-hmm-qlt-table123r-01", schemaProcessor.getIdJsonRaw());
        assertEquals("test_uuaa-pe-hmm-qlt-table123s-01", schemaProcessor.getIdJsonStaging());
    }

    @Test
    void testProcessKirbyFields() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterData();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("\"source_field1|source_field2|source_field3\"", schemaProcessor.getTrimAllColumns());

        assertEquals(1, schemaProcessor.getRawDateColumns().size());
        assertTrue(schemaProcessor.getRawDateColumns().contains("source_field2"));

        assertEquals(1, schemaProcessor.getRawTimestampColumns().size());
        assertTrue(schemaProcessor.getRawTimestampColumns().contains("source_field3"));

        assertEquals(3, schemaProcessor.getMasterFieldWithOriginList().size());
        assertEquals(3, schemaProcessor.getMasterFieldList().size());
        assertTrue(schemaProcessor.getMasterFieldList().contains("master_field1"));
        assertTrue(schemaProcessor.getMasterFieldList().contains("master_field2"));
        assertTrue(schemaProcessor.getMasterFieldList().contains("master_field3"));
    }

    @Test
    void testGetSubset_SinglePartition() throws Exception {
        List<String> partitions = Arrays.asList("cutoff_date");
        Method method = SchemaProcessor.class.getDeclaredMethod("getSubset", List.class);
        method.setAccessible(true);

        String result = (String) method.invoke(schemaProcessor, partitions);

        assertEquals("cutoff_date='\"${?DATE}\"'", result);
    }

    @Test
    void testGetSubset_MultiplePartitions() throws Exception {
        List<String> partitions = Arrays.asList("partition1", "cutoff_date", "partition3");
        Method method = SchemaProcessor.class.getDeclaredMethod("getSubset", List.class);
        method.setAccessible(true);

        String result = (String) method.invoke(schemaProcessor, partitions);

        String expected = "partition1='\"${?PARAMETER1}\"' and cutoff_date='\"${?DATE}\"' and partition3='\"${?PARAMETER3}\"'";
        assertEquals(expected, result);
    }

    @Test
    void testGetPartitionList_SinglePartition() throws Exception {
        List<String> partitions = Arrays.asList("cutoff_date");
        Method method = SchemaProcessor.class.getDeclaredMethod("getPartitionList", List.class);
        method.setAccessible(true);

        String result = (String) method.invoke(schemaProcessor, partitions);

        assertEquals("\"cutoff_date\"", result);
    }

    @Test
    void testGetPartitionList_MultiplePartitions() throws Exception {
        List<String> partitions = Arrays.asList("partition1", "partition2", "partition3");
        Method method = SchemaProcessor.class.getDeclaredMethod("getPartitionList", List.class);
        method.setAccessible(true);

        String result = (String) method.invoke(schemaProcessor, partitions);

        assertEquals("\"partition1\", \"partition2\", \"partition3\"", result);
    }

    @Test
    void testKeysDict_Creation() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterData();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        Map<String, String> keysDict = schemaProcessor.getKeysDict();
        assertNotNull(keysDict);
        assertEquals(0, keysDict.size());
    }

    @Test
    void testStagingNameProcessing_WithDollarSign() {
        List<Map<String, Object>> rawData = createMockRawDataWithDollar();
        List<Map<String, Object>> masterData = createMockMasterDataSingleObject();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("staging$table", schemaProcessor.getOriginalStagingName());
        assertEquals("staging$table", schemaProcessor.getDfStagingName());
    }

    @Test
    void testTagGeneration_LongTableName() {
        List<Map<String, Object>> rawData = createMockRawDataLongName();
        List<Map<String, Object>> masterData = createMockMasterDataLongName();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("verylongtablenamewithmanyparts", schemaProcessor.getTag());
        assertEquals("prefix_uuaa_very_long_table_name_with_many_parts", schemaProcessor.getDfRawName());
    }

    @Test
    void testMasterFieldFiltering_CalculatedFields() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = createMockMasterDataWithCalculated();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("\"source_field1|source_field2\"", schemaProcessor.getTrimAllColumns());

        assertEquals(3, schemaProcessor.getMasterFieldWithOriginList().size());

        assertEquals(1, schemaProcessor.getRawDateColumns().size());
        assertEquals(0, schemaProcessor.getRawTimestampColumns().size());
    }

    @Test
    void testEmptyMasterData_NoFields() {
        List<Map<String, Object>> rawData = createMockRawData();
        List<Map<String, Object>> masterData = new ArrayList<>();
        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("\"\"", schemaProcessor.getTrimAllColumns());
        assertTrue(schemaProcessor.getRawDateColumns().isEmpty());
        assertTrue(schemaProcessor.getRawTimestampColumns().isEmpty());
        assertTrue(schemaProcessor.getMasterFieldWithOriginList().isEmpty());
        assertTrue(schemaProcessor.getMasterFieldList().isEmpty());
    }

    @Test
    void testTagGeneration_ShortTableName() {
        List<Map<String, Object>> rawData = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "field1");
        row1.put("Key", "True");
        row1.put("Logical Format", "ALPHANUMERIC(10)");
        row1.put("Physical name object", "prefix_uuaa");
        row1.put("Physical name of source object", "original_staging");
        rawData.add(row1);

        List<Map<String, Object>> masterData = new ArrayList<>();
        Map<String, Object> masterRow = new HashMap<>();
        masterRow.put("Physical Name field", "master_field1");
        masterRow.put("Source field", "source_field1");
        masterRow.put("Data Type", "string");
        masterRow.put("Physical name object", "prefix_uuaa");
        masterData.add(masterRow);

        IngestaRequestDto request = createValidRequest();

        schemaProcessor.initialize(rawData, masterData, request);

        assertEquals("", schemaProcessor.getTag());
        assertEquals("uuaa", schemaProcessor.getDfUuaa());
    }

    private IngestaRequestDto createValidRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setUuaaMaster("test_uuaa");
        request.setParticiones("partition_field");
        return request;
    }

    private List<Map<String, Object>> createMockRawData() {
        List<Map<String, Object>> rawData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "field1");
        row1.put("Key", "True");
        row1.put("Logical Format", "ALPHANUMERIC(10)");
        row1.put("Physical name object", "prefix_uuaa_table123");
        row1.put("Physical name of source object", "original_staging");
        rawData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("Physical Name field", "field2");
        row2.put("Key", "False");
        row2.put("Logical Format", "STRING");
        row2.put("Physical name object", "prefix_uuaa_table123");
        row2.put("Physical name of source object", "original_staging");
        rawData.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("Physical Name field", "field3");
        row3.put("Key", "True");
        row3.put("Logical Format", "DATE");
        row3.put("Physical name object", "prefix_uuaa_table123");
        row3.put("Physical name of source object", "original_staging");
        rawData.add(row3);

        Map<String, Object> row4 = new HashMap<>();
        row4.put("Physical Name field", "partition_field");
        row4.put("Key", "True");
        row4.put("Logical Format", "STRING");
        row4.put("Physical name object", "prefix_uuaa_table123");
        row4.put("Physical name of source object", "original_staging");
        rawData.add(row4);

        return rawData;
    }

    private List<Map<String, Object>> createMockRawDataWithDollar() {
        List<Map<String, Object>> rawData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "field1");
        row1.put("Key", "True");
        row1.put("Logical Format", "ALPHANUMERIC(10)");
        row1.put("Physical name object", "test_raw_table");
        row1.put("Physical name of source object", "staging$table");
        rawData.add(row1);

        return rawData;
    }

    private List<Map<String, Object>> createMockRawDataLongName() {
        List<Map<String, Object>> rawData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "field1");
        row1.put("Key", "True");
        row1.put("Logical Format", "ALPHANUMERIC(10)");
        row1.put("Physical name object", "prefix_uuaa_very_long_table_name_with_many_parts");
        row1.put("Physical name of source object", "original_staging");
        rawData.add(row1);

        return rawData;
    }

    private List<Map<String, Object>> createMockMasterData() {
        List<Map<String, Object>> masterData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "master_field1");
        row1.put("Source field", "source_field1");
        row1.put("Data Type", "string");
        row1.put("Physical name object", "prefix_uuaa_table123");
        masterData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("Physical Name field", "master_field2");
        row2.put("Source field", "source_field2");
        row2.put("Data Type", "date");
        row2.put("Physical name object", "prefix_uuaa_table123");
        masterData.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("Physical Name field", "master_field3");
        row3.put("Source field", "source_field3");
        row3.put("Data Type", "timestamp");
        row3.put("Physical name object", "prefix_uuaa_table123");
        masterData.add(row3);

        return masterData;
    }

    private List<Map<String, Object>> createMockMasterDataSingleObject() {
        List<Map<String, Object>> masterData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "master_field1");
        row1.put("Source field", "source_field1");
        row1.put("Data Type", "string");
        row1.put("Physical name object", "test_raw_table");
        masterData.add(row1);

        return masterData;
    }

    private List<Map<String, Object>> createMockMasterDataLongName() {
        List<Map<String, Object>> masterData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "master_field1");
        row1.put("Source field", "source_field1");
        row1.put("Data Type", "string");
        row1.put("Physical name object", "prefix_uuaa_very_long_table_name_with_many_parts");
        masterData.add(row1);

        return masterData;
    }

    private List<Map<String, Object>> createMockMasterDataWithCalculated() {
        List<Map<String, Object>> masterData = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("Physical Name field", "master_field1");
        row1.put("Source field", "source_field1");
        row1.put("Data Type", "string");
        row1.put("Physical name object", "prefix_uuaa_table123");
        masterData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("Physical Name field", "master_field2");
        row2.put("Source field", "source_field2");
        row2.put("Data Type", "date");
        row2.put("Physical name object", "prefix_uuaa_table123");
        masterData.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("Physical Name field", "calculated_field");
        row3.put("Source field", "Calculated");
        row3.put("Data Type", "timestamp");
        row3.put("Physical name object", "prefix_uuaa_table123");
        masterData.add(row3);

        return masterData;
    }
}
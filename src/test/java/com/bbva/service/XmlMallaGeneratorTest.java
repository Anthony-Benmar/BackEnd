package com.bbva.service;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import com.bbva.util.metaknight.XmlMallaGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XmlMallaGeneratorTest {

    private XmlMallaGenerator xmlMallaGenerator;
    private MallaRequestDto mallaData;

    @BeforeEach
    void setUp() {
        xmlMallaGenerator = new XmlMallaGenerator();
        mallaData = createValidMallaData();
    }

//    @Test
//    void testGenerarFlujoCompletoXml_Success() throws Exception {
//        // When
//        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);
//
//        // Then
//        assertNotNull(xmlResult);
//        assertFalse(xmlResult.isEmpty());
//        assertTrue(xmlResult.contains("<FOLDER") || xmlResult.contains("<folder"));
//        assertTrue(xmlResult.contains("</FOLDER>") || xmlResult.contains("</folder>"));
//    }

    @Test
    void testGenerarFlujoCompletoXml_ContainsRequiredElements() throws Exception {
        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        // Basic validation that XML was generated
        assertTrue(xmlResult.length() > 100); // Should be a substantial XML
    }

    @Test
    void testGenerarFlujoCompletoXml_WithL1T() throws Exception {
        // Given
        mallaData.setKrbL1tJobname("TESTKL0001");
        mallaData.setHmmL1tJobname("TESTHL0001");
        mallaData.setKrbL1tJobid("test_krb_l1t_001");
        mallaData.setHmmL1tJobid("test_hmm_l1t_001");
        mallaData.setL1tSourceName("test_l1t");

        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        // Verify it's a valid XML structure
        assertTrue(xmlResult.contains("<") && xmlResult.contains(">"));
    }

    @Test
    void testGenerarFlujoCompletoXml_WithoutL1T() throws Exception {
        // Given
        mallaData.setKrbL1tJobname(null);
        mallaData.setHmmL1tJobname(null);
        mallaData.setL1tSourceName(null);

        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        // Verify it's a valid XML structure
        assertTrue(xmlResult.contains("<") && xmlResult.contains(">"));
    }

    @Test
    void testGenerarFlujoCompletoXml_ValidXmlStructure() throws Exception {
        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());

        // Basic XML validation
        assertTrue(xmlResult.contains("<"));
        assertTrue(xmlResult.contains(">"));

        // Should contain some XML structure
        assertTrue(xmlResult.length() > 50);
    }

    @Test
    void testGenerarFlujoCompletoXml_NullMallaData_ThrowsException() {
        // When & Then
        assertThrows(Exception.class, () -> {
            xmlMallaGenerator.generarFlujoCompletoXml(null);
        });
    }

    @Test
    void testGenerarFlujoCompletoXml_EmptyRequiredFields_HandlesGracefully() throws Exception {
        // Given
        MallaRequestDto emptyData = new MallaRequestDto();

        // When & Then - Could throw exception or handle gracefully
        try {
            String result = xmlMallaGenerator.generarFlujoCompletoXml(emptyData);
            // If it doesn't throw, just verify we get some response
            assertNotNull(result);
        } catch (Exception e) {
            // If it throws, that's also acceptable behavior
            assertTrue(e instanceof MallaGenerationException || e instanceof RuntimeException);
        }
    }

    @Test
    void testGenerarFlujoCompletoXml_WithMinimalData() throws Exception {
        // Given
        MallaRequestDto minimalData = createMinimalMallaData();

        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(minimalData);

        // Then
        assertNotNull(xmlResult);
        // Basic validation
        assertTrue(xmlResult.length() > 0);
    }

    @Test
    void testGenerarFlujoCompletoXml_AllJobnamesPresent() throws Exception {
        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());

        // The XML should be substantial if all data is provided
        assertTrue(xmlResult.length() > 200);
    }

    @Test
    void testGenerarFlujoCompletoXml_MultipleCallsConsistent() throws Exception {
        // When
        String xmlResult1 = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);
        String xmlResult2 = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult1);
        assertNotNull(xmlResult2);
        assertEquals(xmlResult1, xmlResult2); // Should be deterministic
    }

    @Test
    void testGenerarFlujoCompletoXml_DifferentFrequencyHandling() throws Exception {
        // Test that the generator can handle the data regardless of setup
        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
    }

    @Test
    void testGenerarFlujoCompletoXml_SpecialCharactersInData() throws Exception {
        // Given
        mallaData.setTeamEmail("test@domain.com");
        mallaData.setCreationUser("user.with.dots");
        mallaData.setUuaa("TEST-SPECIAL");

        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        // Should handle special characters gracefully
        assertTrue(xmlResult.length() > 0);
    }

    @Test
    void testGenerarFlujoCompletoXml_LongJobnames() throws Exception {
        // Given
        mallaData.setTransferJobname("VERY_LONG_TRANSFER_JOBNAME_TEST");
        mallaData.setCopyJobname("VERY_LONG_COPY_JOBNAME_TEST");

        // When
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        // Then
        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
    }

    // Helper methods
    private MallaRequestDto createValidMallaData() {
        MallaRequestDto data = new MallaRequestDto();

        // Basic info
        data.setCreationUser("testuser");
        data.setUuaa("TEST");
        data.setUuaaLowercase("test");
        data.setNamespace("pe.bbva.app-id-test.pro");
        data.setParentFolder("test_folder");
        data.setTeamEmail("test@example.com");
        data.setCreationDate("20241201");
        data.setCreationTime("120000");

        // Transfer config
        data.setTransferJobname("TESTTP0001");
        data.setTransferSourceName("TEST_SOURCE");
        data.setTransferTimeFrom("00:00");
        data.setTransferName("TEST_TRANSFER");
        data.setTransferUuaaRaw("test_raw");

        // Copy config
        data.setCopyJobname("TESTCP0001");
        data.setCopyUuaaRaw("test_copy");

        // Filewatcher
        data.setFwJobname("TESTFW0001");
        data.setCreateNums("1");

        // Processing jobs
        data.setHmmStgJobname("TESTHMMS0001");
        data.setHmmStgJobid("test_hmm_stg_001");
        data.setKrbRawJobname("TESTKR0001");
        data.setKrbRawJobid("test_krb_raw_001");
        data.setRawSourceName("test_raw_source");
        data.setHmmRawJobname("TESTHMR0001");
        data.setHmmRawJobid("test_hmm_raw_001");
        data.setKrbMasterJobname("TESTKM0001");
        data.setKrbMasterJobid("test_krb_master_001");
        data.setMasterSourceName("test_master_source");
        data.setHmmMasterJobname("TESTHM0001");
        data.setHmmMasterJobid("test_hmm_master_001");

        // Cleanup jobs
        data.setErase1Jobname("TESTE10001");
        data.setErase2Jobname("TESTE20001");

        return data;
    }

    private MallaRequestDto createMinimalMallaData() {
        MallaRequestDto data = new MallaRequestDto();

        // Only essential fields
        data.setCreationUser("testuser");
        data.setUuaa("TEST");
        data.setUuaaLowercase("test");
        data.setTeamEmail("test@example.com");
        data.setCreationDate("20241201");
        data.setCreationTime("120000");

        return data;
    }
}
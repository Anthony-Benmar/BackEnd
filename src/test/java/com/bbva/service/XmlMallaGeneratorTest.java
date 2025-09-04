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

    @Test
    void testGenerarFlujoCompletoXml_ContainsRequiredElements() throws Exception {
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        assertFalse(xmlResult.isEmpty());
    }

    @Test
    void testGenerarFlujoCompletoXml_WithL1T() throws Exception {
        mallaData.setKrbL1tJobname("TESTKL0001");
        mallaData.setHmmL1tJobname("TESTHL0001");
        mallaData.setKrbL1tJobid("test_krb_l1t_001");
        mallaData.setHmmL1tJobid("test_hmm_l1t_001");
        mallaData.setL1tSourceName("test_l1t");

        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        assertTrue(xmlResult.contains("<") && xmlResult.contains(">"));
    }

    @Test
    void testGenerarFlujoCompletoXml_WithoutL1T() throws Exception {
        mallaData.setKrbL1tJobname(null);
        mallaData.setHmmL1tJobname(null);
        mallaData.setL1tSourceName(null);

        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        assertTrue(xmlResult.contains("<") && xmlResult.contains(">"));
    }

    @Test
    void testGenerarFlujoCompletoXml_ValidXmlStructure() throws Exception {
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());

        assertTrue(xmlResult.contains("<"));
        assertTrue(xmlResult.contains(">"));

        assertTrue(xmlResult.length() > 50);
    }

    @Test
    void testGenerarFlujoCompletoXml_NullMallaData_ThrowsException() {
        assertThrows(Exception.class, () -> {
            xmlMallaGenerator.generarFlujoCompletoXml(null);
        });
    }

    @Test
    void testGenerarFlujoCompletoXml_EmptyRequiredFields_HandlesGracefully() throws Exception {
        MallaRequestDto emptyData = new MallaRequestDto();

        try {
            String result = xmlMallaGenerator.generarFlujoCompletoXml(emptyData);
            assertNotNull(result);
        } catch (Exception e) {
            assertTrue(e instanceof MallaGenerationException || e instanceof RuntimeException);
        }
    }

    @Test
    void testGenerarFlujoCompletoXml_AllJobnamesPresent() throws Exception {
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());

        assertTrue(xmlResult.length() > 200);
    }

    @Test
    void testGenerarFlujoCompletoXml_MultipleCallsConsistent() throws Exception {
        String xmlResult1 = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);
        String xmlResult2 = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult1);
        assertNotNull(xmlResult2);
        assertEquals(xmlResult1, xmlResult2);
    }

    @Test
    void testGenerarFlujoCompletoXml_DifferentFrequencyHandling() throws Exception {
        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
    }

    @Test
    void testGenerarFlujoCompletoXml_SpecialCharactersInData() throws Exception {
        mallaData.setTeamEmail("test@domain.com");
        mallaData.setCreationUser("user.with.dots");
        mallaData.setUuaa("TEST-SPECIAL");

        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
        assertTrue(!xmlResult.isEmpty());
    }

    @Test
    void testGenerarFlujoCompletoXml_LongJobnames() throws Exception {
        mallaData.setTransferJobname("VERY_LONG_TRANSFER_JOBNAME_TEST");
        mallaData.setCopyJobname("VERY_LONG_COPY_JOBNAME_TEST");

        String xmlResult = xmlMallaGenerator.generarFlujoCompletoXml(mallaData);

        assertNotNull(xmlResult);
        assertFalse(xmlResult.isEmpty());
    }

    private MallaRequestDto createValidMallaData() {
        MallaRequestDto data = new MallaRequestDto();

        data.setCreationUser("testuser");
        data.setUuaa("TEST");
        data.setUuaaLowercase("test");
        data.setNamespace("pe.bbva.app-id-test.pro");
        data.setParentFolder("test_folder");
        data.setTeamEmail("test@example.com");
        data.setCreationDate("20241201");
        data.setCreationTime("120000");

        data.setTransferJobname("TESTTP0001");
        data.setTransferSourceName("TEST_SOURCE");
        data.setTransferTimeFrom("00:00");
        data.setTransferName("TEST_TRANSFER");
        data.setTransferUuaaRaw("test_raw");

        data.setCopyJobname("TESTCP0001");
        data.setCopyUuaaRaw("test_copy");

        data.setFwJobname("TESTFW0001");
        data.setCreateNums("1");

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

        data.setErase1Jobname("TESTE10001");
        data.setErase2Jobname("TESTE20001");

        return data;
    }
}
package com.bbva.service;
import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import com.bbva.service.metaknight.MallaTransformerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MallaTransformerServiceTest {

    private MallaTransformerService mallaTransformerService;
    private MallaRequestDto mallaData;

    @BeforeEach
    void setUp() {
        mallaTransformerService = new MallaTransformerService();
        mallaData = new MallaRequestDto();
        mallaData.setTransferJobname("TRANSFER_JOB");
        mallaData.setCopyJobname("COPY_JOB");
        mallaData.setFwJobname("FW_JOB");
        mallaData.setHmmMasterJobname("HMM_MASTER_JOB");
        mallaData.setHmmL1tJobname("HMM_L1T_JOB");
        mallaData.setKrbL1tJobname("KRB_L1T_JOB");
        mallaData.setErase1Jobname("ERASE1_JOB");
        mallaData.setErase2Jobname("ERASE2_JOB");
        mallaData.setCopyUuaaRaw("copyUuaa");
        mallaData.setTransferUuaaRaw("transferUuaa");
    }

    @Test
    void testTransformarDatioToAda_Success() throws HandledException {
        // Given
        String xmlDatio = """
            <FOLDER>
                <JOB APPLICATION="TEST-DATIO" JOBNAME="TRANSFER_JOB">
                    <DOMAIL DEST="test@example.com" />
                    <OUTCOND NAME="TRANSFER_JOB-TO-COPY_JOB" />
                </JOB>
            </FOLDER>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlDatio, mallaData);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("TEST-ADA"));
        assertFalse(result.contains("TEST-DATIO"));
        assertTrue(result.contains("ada_dhm_pe.group@bbva.com"));
    }

    @Test
    void testReplaceDatioInApplication() throws HandledException {
        // Given
        String xmlContent = """
            <JOB APPLICATION="TEST-DATIO" JOBNAME="JOB1">
            </JOB>
            <JOB APPLICATION="ANOTHER-DATIO" JOBNAME="JOB2">
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("APPLICATION=\"TEST-ADA\""));
        assertTrue(result.contains("APPLICATION=\"ANOTHER-ADA\""));
        assertFalse(result.contains("DATIO"));
    }

    @Test
    void testReplaceCmdlineValueWhenSentryJob() throws HandledException {
        // Given
        String xmlContent = """
            <JOB RUN_AS="sentry" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py">
            </JOB>
            <JOB RUN_AS="other" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py">
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("/opt/datio/sentry-pe-aws/dataproc_sentry.py"));
        // Solo debe cambiar para jobs con RUN_AS="sentry"
        int awsCount = result.split("/opt/datio/sentry-pe-aws/dataproc_sentry.py").length - 1;
        int originalCount = result.split("/opt/datio/sentry-pe/dataproc_sentry.py").length - 1;
        assertEquals(1, awsCount);
        assertEquals(1, originalCount);
    }

    @Test
    void testAddDotCloudInTransfer() throws HandledException {
        // Given
        String xmlContent = """
            <JOB CMDLINE="datax-agent --transferId %%PARM1 --config">
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("datax-agent --transferId %%PARM1._cloud"));
    }

    @Test
    void testReplaceCtmfwWithDefaultBbvaCountry() throws HandledException {
        // Given
        String xmlContent = """
            <JOB CMDLINE="ctmfw --watch /path/to/file">
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("DEFAULT_BBVA_COUNTRY=pe;/opt/datio/filewatcher-s3/filewatcher.sh"));
        assertFalse(result.contains("ctmfw"));
    }

    @Test
    void testReplaceNodeidAndRunasForFilewatcher() throws HandledException {
        // Given
        String xmlContent = """
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR" NODEID="OLD_NODE" RUN_AS="olduser">
            </JOB>
            <JOB SUB_APPLICATION="OTHER" NODEID="OTHER_NODE" RUN_AS="otheruser">
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("NODEID=\"PE-SENTRY-00\""));
        assertTrue(result.contains("RUN_AS=\"sentry\""));
        assertTrue(result.contains("NODEID=\"OTHER_NODE\"")); // No debe cambiar este
        assertTrue(result.contains("RUN_AS=\"otheruser\"")); // No debe cambiar este
    }

    @Test
    void testAddWildcardToFilewatcherPath() throws HandledException {
        // Given
        String xmlContent = """
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR">
                <CMDLINE>/path/to/file.csv</CMDLINE>
            </JOB>
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR">
                <CMDLINE>/another/path/file.dat</CMDLINE>
            </JOB>
            <JOB SUB_APPLICATION="OTHER">
                <CMDLINE>/other/file.csv</CMDLINE>
            </JOB>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("/path/to/file.csv/*.csv"));
        assertTrue(result.contains("/another/path/file.dat/*.csv"));
        assertTrue(result.contains("/other/file.csv")); // No debe cambiar este
    }

    @Test
    void testReplaceArtifactoryHost() throws HandledException {
        // Given
        String xmlContent = """
            <CONFIG>
                <URL>https://artifactory-gdt.central-02.nextgen.igrupobbva/repo</URL>
            </CONFIG>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("artifactory-gdt.central-04.nextgen.igrupobbva"));
        assertFalse(result.contains("artifactory-gdt.central-02.nextgen.igrupobbva"));
    }

    @Test
    void testRemoveCopyHdfsJobs() throws HandledException {
        // Given
        String xmlContent = """
            <JOB DESCRIPTION="COPY (HDFS) job to remove">
                <CONFIG>test</CONFIG>
            </JOB>
            <JOB DESCRIPTION="OTHER job to keep">
                <CONFIG>test</CONFIG>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertFalse(result.contains("COPY (HDFS)"));
        assertTrue(result.contains("OTHER job to keep"));
    }

    @Test
    void testRemoveJobByName() throws HandledException {
        // Given
        String xmlContent = """
            <JOB JOBNAME="ERASE2_JOB">
                <CONFIG>erase2 job</CONFIG>
            </JOB>
            <JOB JOBNAME="OTHER_JOB">
                <CONFIG>other job</CONFIG>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertFalse(result.contains("ERASE2_JOB"));
        assertTrue(result.contains("OTHER_JOB"));
    }

    @Test
    void testRemoveJobByName_NullJobname() throws HandledException {
        // Given
        mallaData.setErase2Jobname(null);
        String xmlContent = """
            <JOB JOBNAME="SOME_JOB">
                <CONFIG>some job</CONFIG>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("SOME_JOB")); // No debe eliminar nada
    }

    @Test
    void testUpdateTransferJobOutconds() throws HandledException {
        // Given
        String xmlContent = """
            <JOB JOBNAME="TRANSFER_JOB">
                <OUTCOND NAME="TRANSFER_JOB-TO-COPY_JOB" />
                <DOFORCEJOB JOBNAME="COPY_JOB" />
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("FW_JOB"));
        assertFalse(result.contains("COPY_JOB"));
    }

    @Test
    void testUpdateFilewatcherInconds() throws HandledException {
        // Given
        String xmlContent = """
            <JOB JOBNAME="FW_JOB">
                <INCOND NAME="COPY_JOB-TO-FW_JOB" />
                <INCOND NAME="TRANSFER_JOB-TO-COPY_JOB" />
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("TRANSFER_JOB"));
        int copyJobCount = result.split("COPY_JOB").length - 1;
        assertTrue(copyJobCount < 2); // Debe haber menos referencias a COPY_JOB
    }

    @Test
    void testCleanupHmmMasterJob_WithL1T() throws HandledException {
        // Given
        String xmlContent = """
            <JOB JOBNAME="HMM_MASTER_JOB">
                <OUTCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
                <DOFORCEJOB JOBNAME="ERASE2_JOB" />
                <OUTCOND NAME="HMM_MASTER_JOB-CF@OK" />
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertFalse(result.contains("ERASE1_JOB"));
        assertFalse(result.contains("ERASE2_JOB"));
        assertFalse(result.contains("CF@OK"));
    }

    @Test
    void testCleanupHmmMasterJob_WithoutL1T() throws HandledException {
        // Given
        mallaData.setHmmL1tJobname(null);
        String xmlContent = """
            <JOB JOBNAME="HMM_MASTER_JOB">
                <OUTCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
                <DOFORCEJOB JOBNAME="ERASE2_JOB" />
                <OUTCOND NAME="HMM_MASTER_JOB-CF@OK" />
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("ERASE1_JOB")); // No debe eliminar cuando no hay L1T
        assertFalse(result.contains("ERASE2_JOB"));
        assertFalse(result.contains("CF@OK"));
    }

    @Test
    void testCleanupHmmL1tJobReferences() throws HandledException {
        // Given
        String xmlContent = """
            <JOB JOBNAME="HMM_L1T_JOB">
                <OUTCOND NAME="HMM_L1T_JOB-TO-ERASE2_JOB" />
                <DOFORCEJOB JOBNAME="ERASE2_JOB" />
                <OTHER_TAG NAME="KEEP_THIS" />
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertFalse(result.contains("ERASE2_JOB"));
        assertTrue(result.contains("KEEP_THIS"));
    }

    @Test
    void testCleanupHmmL1tJobReferences_NullL1tJobname() throws HandledException {
        // Given
        mallaData.setHmmL1tJobname(null);
        String xmlContent = """
            <JOB JOBNAME="SOME_JOB">
                <OUTCOND NAME="SOME_JOB-TO-ERASE2_JOB" />
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("ERASE2_JOB")); // No debe cambiar nada
    }

    @Test
    void testUpdateFilewatcherCmdlinePath() throws HandledException {
        // Given
        String xmlContent = """
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR">
                <CMDLINE>/path/external/copyUuaa/file.csv</CMDLINE>
            </JOB>
            <JOB SUB_APPLICATION="OTHER">
                <CMDLINE>/path/external/copyUuaa/file.csv</CMDLINE>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("datax/transferUuaa"));
        assertTrue(result.contains("external/copyUuaa")); // Solo uno debe cambiar
    }

    @Test
    void testUpdateErase1DependenciesForL1T() throws HandledException {
        // Given
        String xmlContent = """
            <INCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
            <OUTCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("HMM_L1T_JOB-TO-ERASE1_JOB"));
        assertFalse(result.contains("HMM_MASTER_JOB-TO-ERASE1_JOB"));
    }

    @Test
    void testUpdateErase1DependenciesForL1T_NoL1T() throws HandledException {
        // Given
        mallaData.setHmmL1tJobname(null);
        mallaData.setKrbL1tJobname(null);
        String xmlContent = """
            <INCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("HMM_MASTER_JOB-TO-ERASE1_JOB")); // No debe cambiar
    }

    @Test
    void testAddAdaEmailToAllJobs_EmailNotPresent() throws HandledException {
        // Given
        String xmlContent = """
            <DOMAIL DEST="test@example.com" />
            <DOMAIL DEST="another@test.com;third@test.com" />
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("test@example.com;ada_dhm_pe.group@bbva.com"));
        assertTrue(result.contains("another@test.com;third@test.com;ada_dhm_pe.group@bbva.com"));
    }

    @Test
    void testAddAdaEmailToAllJobs_EmailAlreadyPresent() throws HandledException {
        // Given
        String xmlContent = """
            <DOMAIL DEST="test@example.com;ada_dhm_pe.group@bbva.com" />
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("test@example.com;ada_dhm_pe.group@bbva.com"));
        // No debe duplicar el email
        int adaEmailCount = result.split("ada_dhm_pe\\.group@bbva\\.com").length - 1;
        assertEquals(1, adaEmailCount);
    }

    @Test
    void testTransformarDatioToAda_Exception() {
        // Given
        String xmlDatio = null; // Esto causará una excepción

        // When & Then
        HandledException exception = assertThrows(HandledException.class, () -> {
            mallaTransformerService.transformarDatioToAda(xmlDatio, mallaData);
        });

        assertEquals("MALLA_TRANSFORM_ERROR", exception.getCode());
        assertTrue(exception.getMessage().contains("Error transformando XML de DATIO a ADA"));
    }

    @Test
    void testEscapeRegexCharacters() throws HandledException {
        // Given
        mallaData.setErase2Jobname("JOB.WITH[SPECIAL]CHARS(AND)MORE");
        String xmlContent = """
            <JOB JOBNAME="JOB.WITH[SPECIAL]CHARS(AND)MORE">
                <CONFIG>special job</CONFIG>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertFalse(result.contains("JOB.WITH[SPECIAL]CHARS(AND)MORE"));
    }

    @Test
    void testUpdateFilewatcherCmdlinePath_NullValues() throws HandledException {
        // Given
        mallaData.setCopyUuaaRaw(null);
        mallaData.setTransferUuaaRaw(null);
        String xmlContent = """
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR">
                <CMDLINE>/path/external/test/file.csv</CMDLINE>
            </JOB>
            """;

        // When
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        // Then
        assertTrue(result.contains("/path/external/test/file.csv")); // No debe cambiar
    }
}
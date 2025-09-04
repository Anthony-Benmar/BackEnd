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
        String xmlDatio = """
            <FOLDER>
                <JOB APPLICATION="TEST-DATIO" JOBNAME="TRANSFER_JOB">
                    <DOMAIL DEST="test@example.com" />
                    <OUTCOND NAME="TRANSFER_JOB-TO-COPY_JOB" />
                </JOB>
            </FOLDER>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlDatio, mallaData);

        assertNotNull(result);
        assertTrue(result.contains("TEST-ADA"));
        assertFalse(result.contains("TEST-DATIO"));
        assertTrue(result.contains("ada_dhm_pe.group@bbva.com"));
    }

    @Test
    void testReplaceDatioInApplication() throws HandledException {
        String xmlContent = """
            <JOB APPLICATION="TEST-DATIO" JOBNAME="JOB1">
            </JOB>
            <JOB APPLICATION="ANOTHER-DATIO" JOBNAME="JOB2">
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("APPLICATION=\"TEST-ADA\""));
        assertTrue(result.contains("APPLICATION=\"ANOTHER-ADA\""));
        assertFalse(result.contains("DATIO"));
    }

    @Test
    void testReplaceCmdlineValueWhenSentryJob() throws HandledException {
        String xmlContent = """
            <JOB RUN_AS="sentry" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py">
            </JOB>
            <JOB RUN_AS="other" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py">
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("/opt/datio/sentry-pe-aws/dataproc_sentry.py"));
        int awsCount = result.split("/opt/datio/sentry-pe-aws/dataproc_sentry.py").length - 1;
        int originalCount = result.split("/opt/datio/sentry-pe/dataproc_sentry.py").length - 1;
        assertEquals(1, awsCount);
        assertEquals(1, originalCount);
    }

    @Test
    void testAddDotCloudInTransfer() throws HandledException {
        String xmlContent = """
            <JOB CMDLINE="datax-agent --transferId %%PARM1 --config">
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("datax-agent --transferId %%PARM1._cloud"));
    }

    @Test
    void testReplaceCtmfwWithDefaultBbvaCountry() throws HandledException {
        String xmlContent = """
        <JOB CMDLINE="ctmfw --watch /path/to/file">
        </JOB>
        """;

        assertTransformation(xmlContent,
                new String[]{"DEFAULT_BBVA_COUNTRY=pe;/opt/datio/filewatcher-s3/filewatcher.sh"},
                new String[]{"ctmfw"});
    }

    @Test
    void testReplaceNodeidAndRunasForFilewatcher() throws HandledException {
        String xmlContent = """
        <JOB SUB_APPLICATION="CTD-FWATCHER-CCR" NODEID="OLD_NODE" RUN_AS="olduser">
        </JOB>
        <JOB SUB_APPLICATION="OTHER" NODEID="OTHER_NODE" RUN_AS="otheruser">
        </JOB>
        """;

        assertTransformation(xmlContent,
                new String[]{"NODEID=\"PE-SENTRY-00\"", "RUN_AS=\"sentry\"", "NODEID=\"OTHER_NODE\"", "RUN_AS=\"otheruser\""},
                new String[]{});
    }
    @Test
    void testReplaceArtifactoryHost() throws HandledException {
        String xmlContent = """
        <CONFIG>
            <URL>https://artifactory-gdt.central-02.nextgen.igrupobbva/repo</URL>
        </CONFIG>
        """;

        assertTransformation(xmlContent,
                new String[]{"artifactory-gdt.central-04.nextgen.igrupobbva"},
                new String[]{"artifactory-gdt.central-02.nextgen.igrupobbva"});
    }

    @Test
    void testRemoveCopyHdfsJobs() throws HandledException {
        String xmlContent = """
        <JOB DESCRIPTION="COPY (HDFS) job to remove">
            <CONFIG>test</CONFIG>
        </JOB>
        <JOB DESCRIPTION="OTHER job to keep">
            <CONFIG>test</CONFIG>
        </JOB>
        """;

        assertTransformation(xmlContent,
                new String[]{"OTHER job to keep"},
                new String[]{"COPY (HDFS)"});
    }

    @Test
    void testRemoveJobByName() throws HandledException {
        String xmlContent = """
        <JOB JOBNAME="ERASE2_JOB">
            <CONFIG>erase2 job</CONFIG>
        </JOB>
        <JOB JOBNAME="OTHER_JOB">
            <CONFIG>other job</CONFIG>
        </JOB>
        """;

        assertTransformation(xmlContent,
                new String[]{"OTHER_JOB"},
                new String[]{"ERASE2_JOB"});
    }

    @Test
    void testRemoveJobByName_NullJobname() throws HandledException {
        mallaData.setErase2Jobname(null);
        String xmlContent = """
            <JOB JOBNAME="SOME_JOB">
                <CONFIG>some job</CONFIG>
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("SOME_JOB"));
    }

    @Test
    void testUpdateTransferJobOutconds() throws HandledException {
        String xmlContent = """
        <JOB JOBNAME="TRANSFER_JOB">
            <OUTCOND NAME="TRANSFER_JOB-TO-COPY_JOB" />
            <DOFORCEJOB JOBNAME="COPY_JOB" />
        </JOB>
        """;

        assertTransformation(xmlContent,
                new String[]{"FW_JOB"},
                new String[]{"COPY_JOB"});
    }

    @Test
    void testUpdateFilewatcherInconds() throws HandledException {
        String xmlContent = """
            <JOB JOBNAME="FW_JOB">
                <INCOND NAME="COPY_JOB-TO-FW_JOB" />
                <INCOND NAME="TRANSFER_JOB-TO-COPY_JOB" />
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("TRANSFER_JOB"));
        int copyJobCount = result.split("COPY_JOB").length - 1;
        assertTrue(copyJobCount < 2);
    }

    @Test
    void testCleanupHmmMasterJob_WithL1T() throws HandledException {
        String xmlContent = """
            <JOB JOBNAME="HMM_MASTER_JOB">
                <OUTCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
                <DOFORCEJOB JOBNAME="ERASE2_JOB" />
                <OUTCOND NAME="HMM_MASTER_JOB-CF@OK" />
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertFalse(result.contains("ERASE1_JOB"));
        assertFalse(result.contains("ERASE2_JOB"));
        assertFalse(result.contains("CF@OK"));
    }

    @Test
    void testCleanupHmmMasterJob_WithoutL1T() throws HandledException {
        mallaData.setHmmL1tJobname(null);
        String xmlContent = """
            <JOB JOBNAME="HMM_MASTER_JOB">
                <OUTCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
                <DOFORCEJOB JOBNAME="ERASE2_JOB" />
                <OUTCOND NAME="HMM_MASTER_JOB-CF@OK" />
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("ERASE1_JOB"));
        assertFalse(result.contains("ERASE2_JOB"));
        assertFalse(result.contains("CF@OK"));
    }

    @Test
    void testCleanupHmmL1tJobReferences() throws HandledException {
        String xmlContent = """
        <JOB JOBNAME="HMM_L1T_JOB">
            <OUTCOND NAME="HMM_L1T_JOB-TO-ERASE2_JOB" />
            <DOFORCEJOB JOBNAME="ERASE2_JOB" />
            <OTHER_TAG NAME="KEEP_THIS" />
        </JOB>
        """;

        assertTransformation(xmlContent,
                new String[]{"KEEP_THIS"},
                new String[]{"ERASE2_JOB"});
    }

    @Test
    void testCleanupHmmL1tJobReferences_NullL1tJobname() throws HandledException {
        mallaData.setHmmL1tJobname(null);
        String xmlContent = """
            <JOB JOBNAME="SOME_JOB">
                <OUTCOND NAME="SOME_JOB-TO-ERASE2_JOB" />
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("ERASE2_JOB"));
    }

    @Test
    void testUpdateFilewatcherCmdlinePath() throws HandledException {
        String xmlContent = """
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR">
                <CMDLINE>/path/external/copyUuaa/file.csv</CMDLINE>
            </JOB>
            <JOB SUB_APPLICATION="OTHER">
                <CMDLINE>/path/external/copyUuaa/file.csv</CMDLINE>
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("datax/transferUuaa"));
        assertTrue(result.contains("external/copyUuaa"));
    }

    @Test
    void testUpdateErase1DependenciesForL1T() throws HandledException {
        String xmlContent = """
            <INCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
            <OUTCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("HMM_L1T_JOB-TO-ERASE1_JOB"));
        assertFalse(result.contains("HMM_MASTER_JOB-TO-ERASE1_JOB"));
    }

    @Test
    void testUpdateErase1DependenciesForL1T_NoL1T() throws HandledException {
        mallaData.setHmmL1tJobname(null);
        mallaData.setKrbL1tJobname(null);
        String xmlContent = """
            <INCOND NAME="HMM_MASTER_JOB-TO-ERASE1_JOB" />
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("HMM_MASTER_JOB-TO-ERASE1_JOB"));
    }

    @Test
    void testAddAdaEmailToAllJobs_EmailNotPresent() throws HandledException {
        String xmlContent = """
            <DOMAIL DEST="test@example.com" />
            <DOMAIL DEST="another@test.com;third@test.com" />
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("test@example.com;ada_dhm_pe.group@bbva.com"));
        assertTrue(result.contains("another@test.com;third@test.com;ada_dhm_pe.group@bbva.com"));
    }

    @Test
    void testAddAdaEmailToAllJobs_EmailAlreadyPresent() throws HandledException {
        String xmlContent = """
            <DOMAIL DEST="test@example.com;ada_dhm_pe.group@bbva.com" />
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("test@example.com;ada_dhm_pe.group@bbva.com"));
        int adaEmailCount = result.split("ada_dhm_pe\\.group@bbva\\.com").length - 1;
        assertEquals(1, adaEmailCount);
    }

    @Test
    void testTransformarDatioToAda_Exception() {
        String xmlDatio = null;

        HandledException exception = assertThrows(HandledException.class, () -> {
            mallaTransformerService.transformarDatioToAda(xmlDatio, mallaData);
        });

        assertEquals("MALLA_TRANSFORM_ERROR", exception.getCode());
        assertTrue(exception.getMessage().contains("Error transformando XML de DATIO a ADA"));
    }

    @Test
    void testEscapeRegexCharacters() throws HandledException {
        mallaData.setErase2Jobname("JOB.WITH[SPECIAL]CHARS(AND)MORE");
        String xmlContent = """
            <JOB JOBNAME="JOB.WITH[SPECIAL]CHARS(AND)MORE">
                <CONFIG>special job</CONFIG>
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertFalse(result.contains("JOB.WITH[SPECIAL]CHARS(AND)MORE"));
    }

    @Test
    void testUpdateFilewatcherCmdlinePath_NullValues() throws HandledException {
        mallaData.setCopyUuaaRaw(null);
        mallaData.setTransferUuaaRaw(null);
        String xmlContent = """
            <JOB SUB_APPLICATION="CTD-FWATCHER-CCR">
                <CMDLINE>/path/external/test/file.csv</CMDLINE>
            </JOB>
            """;

        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        assertTrue(result.contains("/path/external/test/file.csv"));
    }

    private void assertTransformation(String xmlContent, String[] expectedContains, String[] expectedNotContains) throws HandledException {
        String result = mallaTransformerService.transformarDatioToAda(xmlContent, mallaData);

        for (String expected : expectedContains) {
            assertTrue(result.contains(expected), "Should contain: " + expected);
        }

        for (String notExpected : expectedNotContains) {
            assertFalse(result.contains(notExpected), "Should not contain: " + notExpected);
        }
    }
}
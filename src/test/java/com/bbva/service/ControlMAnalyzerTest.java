package com.bbva.service;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.service.metaknight.ControlMAnalyzer;
import com.bbva.service.metaknight.OptimizedGitRepositoryService;
import com.bbva.util.metaknight.XmlJobnameExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControlMAnalyzerTest {

    @TempDir
    Path tempDir;

    private OptimizedGitRepositoryService mockGitService;
    private ControlMAnalyzer controlMAnalyzer;

    @BeforeEach
    void setUp() {
        mockGitService = new TestOptimizedGitRepositoryService(tempDir);
    }

    @Test
    void testConstructor_Success() throws MallaGenerationException {
        // Given
        setupValidXmlFiles();

        // When
        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, false);

        // Then
        assertNotNull(controlMAnalyzer);
        assertEquals("test", controlMAnalyzer.getUuaa());
        assertEquals("TEST", controlMAnalyzer.getUuaaUpper());
        assertEquals("Daily", controlMAnalyzer.getFrequency());
        assertFalse(controlMAnalyzer.isTieneL1TMallas());
    }

    @Test
    void testConstructor_WithL1T() throws MallaGenerationException {
        // Given
        setupValidXmlFiles();

        // When
        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, true);

        // Then
        assertTrue(controlMAnalyzer.isTieneL1TMallas());
        assertNotNull(controlMAnalyzer.getKrbL1t());
        assertNotNull(controlMAnalyzer.getHmmL1t());
    }

    @Test
    void testConstructor_MonthlyFrequency() throws MallaGenerationException {
        setupValidXmlFiles("Monthly");

        controlMAnalyzer = new ControlMAnalyzer("TEST", "Monthly", mockGitService, false);

        assertEquals("Monthly", controlMAnalyzer.getFrequency());
        assertNotNull(controlMAnalyzer.getNamespace());
    }

    @Test
    void testConstructor_WeeklyFrequency() throws MallaGenerationException {
        setupValidXmlFiles("Weekly");

        controlMAnalyzer = new ControlMAnalyzer("TEST", "Weekly", mockGitService, false);

        assertEquals("Weekly", controlMAnalyzer.getFrequency());
    }

    @Test
    void testJobnameGeneration() throws MallaGenerationException {
        setupValidXmlFiles();

        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, false);

        assertNotNull(controlMAnalyzer.getTransfer());
        assertNotNull(controlMAnalyzer.getCopy());
        assertNotNull(controlMAnalyzer.getFw());
        assertNotNull(controlMAnalyzer.getHs());
        assertNotNull(controlMAnalyzer.getKbr());
        assertNotNull(controlMAnalyzer.getHr());
        assertNotNull(controlMAnalyzer.getKbm());
        assertNotNull(controlMAnalyzer.getHm());
        assertNotNull(controlMAnalyzer.getD1());
        assertNotNull(controlMAnalyzer.getD2());

        assertTrue(controlMAnalyzer.getTransfer().startsWith("TEST"));
        assertTrue(controlMAnalyzer.getCopy().startsWith("TEST"));
    }

    @Test
    void testJobnameGeneration_WithL1T() throws MallaGenerationException {
        setupValidXmlFiles();

        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, true);

        assertNotNull(controlMAnalyzer.getKrbL1t());
        assertNotNull(controlMAnalyzer.getHmmL1t());

        assertTrue(controlMAnalyzer.getKrbL1t().startsWith("TEST"));
        assertTrue(controlMAnalyzer.getHmmL1t().startsWith("TEST"));
    }

    @Test
    void testParentFolderAndNamespace() throws MallaGenerationException {
        setupValidXmlFiles();

        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, false);

        assertNotNull(controlMAnalyzer.getParentFolder());
        assertNotNull(controlMAnalyzer.getNamespace());
        assertEquals("pe.bbva.app-id-test.pro", controlMAnalyzer.getNamespace());
    }

    @Test
    void testLastJobnames() throws MallaGenerationException {
        setupValidXmlFiles();

        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, false);

        assertNotNull(controlMAnalyzer.getLastCp());
        assertNotNull(controlMAnalyzer.getLastVp());
        assertNotNull(controlMAnalyzer.getLastTp());

        assertEquals("TESTCP0001", controlMAnalyzer.getLastCp());
        assertEquals("TESTVP0001", controlMAnalyzer.getLastVp());
        assertEquals("TESTTP0001", controlMAnalyzer.getLastTp());
    }

    @Test
    void testGettersAndSetters() throws MallaGenerationException {
        setupValidXmlFiles();
        controlMAnalyzer = new ControlMAnalyzer("TEST", "Daily", mockGitService, false);

        assertNotNull(controlMAnalyzer.getUuaa());
        assertNotNull(controlMAnalyzer.getUuaaUpper());
        assertNotNull(controlMAnalyzer.getFrequency());
        assertNotNull(controlMAnalyzer.getTotalJobnames());
        assertNotNull(controlMAnalyzer.getXmlArray());
        assertNotNull(controlMAnalyzer.getTotalFolderJobnames());
        assertNotNull(controlMAnalyzer.getNamespace());
        assertNotNull(controlMAnalyzer.getJobXml());
        assertNotNull(controlMAnalyzer.getParentFolder());
    }

    private void setupValidXmlFiles() {
        setupValidXmlFiles("Daily");
    }

    private void setupValidXmlFiles(String frequency) {
        try {
            File globalDir = tempDir.resolve("Global").resolve("TEST").toFile();
            globalDir.mkdirs();

            String frequencyCode = getFrequencyCode(frequency);
            String xmlContent = createValidXmlContent();

            File xmlFile = new File(globalDir, "test_" + frequencyCode + ".xml");
            try (FileWriter writer = new FileWriter(xmlFile)) {
                writer.write(xmlContent);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFrequencyCode(String frequency) {
        return switch (frequency) {
            case "Daily" -> "DIA";
            case "Monthly" -> "MEN";
            case "Weekly" -> "SEM";
            default -> "DIA";
        };
    }

    private String createValidXmlContent() {
        return """
            <FOLDER>
                <VARIABLE NAME="%%SENTRY_JOB" VALUE="pe.bbva.app-id-test.pro.job"/>
                <JOB JOBNAME="TESTCP0001"></JOB>
                <JOB JOBNAME="TESTVP0001"></JOB>
                <JOB JOBNAME="TESTTP0001"></JOB>
                <JOB JOBNAME="TESTDP0001"></JOB>
                <JOB JOBNAME="TESTWP0001"></JOB>
            </FOLDER>
            """;
    }

    private static class TestOptimizedGitRepositoryService extends OptimizedGitRepositoryService {
        private final Path tempDir;

        public TestOptimizedGitRepositoryService(Path tempDir) {
            this.tempDir = tempDir;
        }

        @Override
        public String getUuaaDirectoryPath(String uuaa, String countryType) {
            return tempDir.resolve(countryType).resolve(uuaa).toString();
        }

        @Override
        public void cleanupCache() {
            // No-op for tests
        }
    }

    private static class EmptyGitRepositoryService extends OptimizedGitRepositoryService {
        private final Path tempDir;

        public EmptyGitRepositoryService(Path tempDir) {
            this.tempDir = tempDir;
        }

        @Override
        public String getUuaaDirectoryPath(String uuaa, String countryType) {
            // Return empty directory path
            return tempDir.resolve("empty").resolve(countryType).resolve(uuaa).toString();
        }

        @Override
        public void cleanupCache() {
            // No-op for tests
        }
    }

    private static class TestXmlJobnameExtractor extends XmlJobnameExtractor {
        @Override
        public List<String> extractJobnames(String xmlFilePath) {
            return Arrays.asList("TESTCP0001", "TESTVP0001", "TESTTP0001", "TESTDP0001", "TESTWP0001");
        }

        @Override
        public String extractNamespaceFromXml(String xmlFilePath) {
            return "pe.bbva.app-id-test.pro";
        }

        @Override
        public boolean isValidControlMXml(String xmlFilePath) {
            return xmlFilePath.contains("test_") && xmlFilePath.endsWith(".xml");
        }

        @Override
        public List<String> filterJobnamesByPattern(List<String> jobnames, String uuaa, String jobType) {
            return jobnames.stream()
                    .filter(job -> job.startsWith(uuaa + jobType))
                    .sorted()
                    .toList();
        }

        @Override
        public String getLastJobname(List<String> sortedJobnames) {
            if (sortedJobnames == null || sortedJobnames.isEmpty()) {
                return null;
            }
            return sortedJobnames.get(sortedJobnames.size() - 1);
        }

        @Override
        public String getNextJob(String previousJobname) {
            if (previousJobname == null) return null;

            String numberPart = previousJobname.substring(previousJobname.length() - 4);
            int currentNumber = Integer.parseInt(numberPart);
            int nextNumber = currentNumber + 1;
            String nextNumberFormatted = String.format("%04d", nextNumber);
            String prefix = previousJobname.substring(0, previousJobname.length() - 4);

            return prefix + nextNumberFormatted;
        }
    }
}
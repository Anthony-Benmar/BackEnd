package com.bbva.util.metaknight;

import com.bbva.core.exception.MallaGenerationException;
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

class XmlJobnameExtractorTest {

    @TempDir
    Path tempDir;

    private XmlJobnameExtractor xmlJobnameExtractor;
    private File validXmlFile;
    private File invalidXmlFile;
    private File emptyXmlFile;

    @BeforeEach
    void setUp() throws IOException {
        xmlJobnameExtractor = new XmlJobnameExtractor();
        createTestXmlFiles();
    }

    @Test
    void testExtractJobnames_Success() throws MallaGenerationException {
        // When
        List<String> jobnames = xmlJobnameExtractor.extractJobnames(validXmlFile.getAbsolutePath());

        // Then
        assertNotNull(jobnames);
        assertFalse(jobnames.isEmpty());
        assertTrue(jobnames.contains("TESTCP0001"));
        assertTrue(jobnames.contains("TESTVP0002"));
        assertTrue(jobnames.contains("TESTTP0003"));
        assertEquals(5, jobnames.size());
    }

    @Test
    void testExtractJobnames_EmptyFile() throws MallaGenerationException {
        // When
        List<String> jobnames = xmlJobnameExtractor.extractJobnames(emptyXmlFile.getAbsolutePath());

        // Then
        assertNotNull(jobnames);
        assertTrue(jobnames.isEmpty());
    }

    @Test
    void testExtractNamespaceFromXml_Success() throws MallaGenerationException {
        String namespace = xmlJobnameExtractor.extractNamespaceFromXml(validXmlFile.getAbsolutePath());

        assertNotNull(namespace);
        assertEquals("pe.bbva.app-id-test.pro", namespace);
    }

    @Test
    void testExtractNamespaceFromXml_NoNamespace() throws MallaGenerationException {
        String namespace = xmlJobnameExtractor.extractNamespaceFromXml(emptyXmlFile.getAbsolutePath());

        assertNull(namespace);
    }

    @Test
    void testFilterJobnamesByPattern_Success() {
        // Given
        List<String> jobnames = Arrays.asList(
                "TESTCP0001", "TESTCP0002", "TESTCP0003",
                "TESTVP0001", "TESTVP0002",
                "TESTTP0001",
                "OTHERCP0001"
        );

        // When
        List<String> filteredCp = xmlJobnameExtractor.filterJobnamesByPattern(jobnames, "TEST", "CP");
        List<String> filteredVp = xmlJobnameExtractor.filterJobnamesByPattern(jobnames, "TEST", "VP");
        List<String> filteredTp = xmlJobnameExtractor.filterJobnamesByPattern(jobnames, "TEST", "TP");

        // Then
        assertEquals(3, filteredCp.size());
        assertTrue(filteredCp.contains("TESTCP0001"));
        assertTrue(filteredCp.contains("TESTCP0002"));
        assertTrue(filteredCp.contains("TESTCP0003"));

        assertEquals(2, filteredVp.size());
        assertTrue(filteredVp.contains("TESTVP0001"));
        assertTrue(filteredVp.contains("TESTVP0002"));

        assertEquals(1, filteredTp.size());
        assertTrue(filteredTp.contains("TESTTP0001"));
    }

    @Test
    void testFilterJobnamesByPattern_EmptyList() {
        List<String> emptyList = Arrays.asList();

        List<String> filtered = xmlJobnameExtractor.filterJobnamesByPattern(emptyList, "TEST", "CP");

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testFilterJobnamesByPattern_NoMatches() {
        List<String> jobnames = Arrays.asList("OTHERCP0001", "DIFFERENTVP0001");

        List<String> filtered = xmlJobnameExtractor.filterJobnamesByPattern(jobnames, "TEST", "CP");

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testFilterJobnamesByPattern_CaseInsensitive() {
        List<String> jobnames = Arrays.asList("TESTCP0001", "testcp0002");

        List<String> filtered = xmlJobnameExtractor.filterJobnamesByPattern(jobnames, "test", "CP");

        assertEquals(1, filtered.size());
        assertTrue(filtered.contains("TESTCP0001"));
    }

    @Test
    void testFilterJobnamesByPattern_Sorted() {
        List<String> jobnames = Arrays.asList("TESTCP0003", "TESTCP0001", "TESTCP0002");

        List<String> filtered = xmlJobnameExtractor.filterJobnamesByPattern(jobnames, "TEST", "CP");

        assertEquals(3, filtered.size());
        assertEquals("TESTCP0001", filtered.get(0));
        assertEquals("TESTCP0002", filtered.get(1));
        assertEquals("TESTCP0003", filtered.get(2));
    }

    @Test
    void testGetLastJobname_Success() {
        List<String> sortedJobnames = Arrays.asList("TESTCP0001", "TESTCP0002", "TESTCP0003");

        String lastJobname = xmlJobnameExtractor.getLastJobname(sortedJobnames);

        assertEquals("TESTCP0003", lastJobname);
    }

    @Test
    void testGetLastJobname_EmptyList() {
        List<String> emptyList = Arrays.asList();

        String lastJobname = xmlJobnameExtractor.getLastJobname(emptyList);

        assertNull(lastJobname);
    }

    @Test
    void testGetLastJobname_NullList() {
        String lastJobname = xmlJobnameExtractor.getLastJobname(null);

        assertNull(lastJobname);
    }

    @Test
    void testGetLastJobname_SingleElement() {
        List<String> singleElement = Arrays.asList("TESTCP0001");

        String lastJobname = xmlJobnameExtractor.getLastJobname(singleElement);

        assertEquals("TESTCP0001", lastJobname);
    }

    @Test
    void testGetNextJob_Success() {
        String nextJob1 = xmlJobnameExtractor.getNextJob("TESTCP0001");
        String nextJob2 = xmlJobnameExtractor.getNextJob("TESTVP0009");
        String nextJob3 = xmlJobnameExtractor.getNextJob("TESTTP0099");

        assertEquals("TESTCP0002", nextJob1);
        assertEquals("TESTVP0010", nextJob2);
        assertEquals("TESTTP0100", nextJob3);
    }

    @Test
    void testGetNextJob_NullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xmlJobnameExtractor.getNextJob(null);
        });

        assertTrue(exception.getMessage().contains("Jobname anterior inválido"));
    }

    @Test
    void testGetNextJob_ShortInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xmlJobnameExtractor.getNextJob("ABC");
        });

        assertTrue(exception.getMessage().contains("Jobname anterior inválido"));
    }

    @Test
    void testGetNextJob_InvalidNumberFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xmlJobnameExtractor.getNextJob("TESTCPABCD");
        });

        assertTrue(exception.getMessage().contains("No se pudo extraer número de jobname"));
    }

    @Test
    void testGetNextJob_LeadingZeros() {
        String nextJob = xmlJobnameExtractor.getNextJob("TESTCP0001");

        assertEquals("TESTCP0002", nextJob);
    }

    @Test
    void testGetNextJob_MaxNumber() {
        String nextJob = xmlJobnameExtractor.getNextJob("TESTCP9999");

        assertEquals("TESTCP10000", nextJob);
    }

    @Test
    void testIsValidControlMXml_ValidFile() {
        boolean isValid = xmlJobnameExtractor.isValidControlMXml(validXmlFile.getAbsolutePath());

        assertTrue(isValid);
    }

    @Test
    void testIsValidControlMXml_EmptyFile() {
        boolean isValid = xmlJobnameExtractor.isValidControlMXml(emptyXmlFile.getAbsolutePath());

        assertFalse(isValid);
    }

    @Test
    void testIsValidControlMXml_InvalidFile() {
        boolean isValid = xmlJobnameExtractor.isValidControlMXml(invalidXmlFile.getAbsolutePath());

        assertFalse(isValid);
    }

    @Test
    void testIsValidControlMXml_NonExistentFile() {
        boolean isValid = xmlJobnameExtractor.isValidControlMXml("non_existent_file.xml");

        assertFalse(isValid);
    }

    @Test
    void testIsValidControlMXml_NonXmlFile() throws IOException {
        File textFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("This is not an XML file");
        }

        boolean isValid = xmlJobnameExtractor.isValidControlMXml(textFile.getAbsolutePath());

        assertFalse(isValid);
    }

    private void createTestXmlFiles() throws IOException {
        validXmlFile = tempDir.resolve("valid.xml").toFile();
        try (FileWriter writer = new FileWriter(validXmlFile)) {
            writer.write(createValidXmlContent());
        }

        invalidXmlFile = tempDir.resolve("invalid.xml").toFile();
        try (FileWriter writer = new FileWriter(invalidXmlFile)) {
            writer.write("Invalid XML content <unclosed tag");
        }

        emptyXmlFile = tempDir.resolve("empty.xml").toFile();
        try (FileWriter writer = new FileWriter(emptyXmlFile)) {
            writer.write("<FOLDER></FOLDER>");
        }
    }

    private String createValidXmlContent() {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <FOLDER>
                <VARIABLE NAME="%%SENTRY_JOB" VALUE="pe.bbva.app-id-test.pro.job"/>
                <JOB JOBNAME="TESTCP0001" DESCRIPTION="Copy job 1">
                    <TASK>Copy task</TASK>
                </JOB>
                <JOB JOBNAME="TESTVP0002" DESCRIPTION="Validation job 2">
                    <TASK>Validation task</TASK>
                </JOB>
                <JOB JOBNAME="TESTTP0003" DESCRIPTION="Transfer job 3">
                    <TASK>Transfer task</TASK>
                </JOB>
                <JOB JOBNAME="TESTDP0004" DESCRIPTION="Delete job 4">
                    <TASK>Delete task</TASK>
                </JOB>
                <JOB JOBNAME="TESTWP0005" DESCRIPTION="Watch job 5">
                    <TASK>Watch task</TASK>
                </JOB>
            </FOLDER>
            """;
    }
}
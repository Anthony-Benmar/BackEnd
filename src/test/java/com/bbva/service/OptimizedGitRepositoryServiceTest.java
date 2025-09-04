package com.bbva.service;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.fga.core.AppProperties;
import com.bbva.service.metaknight.OptimizedGitRepositoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptimizedGitRepositoryServiceTest {

    private OptimizedGitRepositoryService gitRepositoryService;
    private MockedStatic<AppProperties> appPropertiesMock;
    private Properties mockProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mockProperties = new Properties();
        mockProperties.setProperty("metaknight.user", "test-user");
        mockProperties.setProperty("metaknight.token", "test-token");

        appPropertiesMock = mockStatic(AppProperties.class);
        appPropertiesMock.when(AppProperties::getInstance).thenReturn(mockProperties);

        gitRepositoryService = new OptimizedGitRepositoryService();
    }

    @AfterEach
    void tearDown() {
        if (appPropertiesMock != null) {
            appPropertiesMock.close();
        }
        gitRepositoryService.cleanupCache();
    }

    @Test
    @DisplayName("getRepositoryPath - Crea directorio temporal exitosamente")
    void testGetRepositoryPath_Success() throws MallaGenerationException {
        String repositoryPath = gitRepositoryService.getRepositoryPath();

        assertNotNull(repositoryPath);
        assertTrue(repositoryPath.contains("optimized-controlm-cache-"));
        assertTrue(Files.exists(Paths.get(repositoryPath)));
    }

    @Test
    @DisplayName("getRepositoryPath - Directorio ya existe")
    void testGetRepositoryPath_DirectoryExists() throws MallaGenerationException {
        String firstPath = gitRepositoryService.getRepositoryPath();

        String secondPath = gitRepositoryService.getRepositoryPath();

        assertEquals(firstPath, secondPath);
        assertTrue(Files.exists(Paths.get(secondPath)));
    }

    @Test
    @DisplayName("cleanupCache - Limpia archivos temporales exitosamente")
    void testCleanupCache_Success() throws Exception {
        String repoPath = gitRepositoryService.getRepositoryPath();
        Path testFile = Paths.get(repoPath, "test.txt");
        Files.createFile(testFile);
        assertTrue(Files.exists(testFile));

        gitRepositoryService.cleanupCache();

        assertFalse(Files.exists(Paths.get(repoPath)));
    }

    @Test
    @DisplayName("cleanupCache - Directorio no existe")
    void testCleanupCache_DirectoryNotExists() {
        OptimizedGitRepositoryService service = new OptimizedGitRepositoryService();

        assertDoesNotThrow(() -> service.cleanupCache());
    }

    @Test
    @DisplayName("Test procesamiento de respuesta JSON")
    void testJsonResponseProcessing() throws Exception {
        String jsonResponse = "{\"children\":{\"values\":[{\"type\":\"FILE\",\"path\":{\"name\":\"test.xml\"}}]}}";
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("getJsonResponse", HttpURLConnection.class);
        method.setAccessible(true);
        JsonNode result = (JsonNode) method.invoke(service, mockConnection);

        assertNotNull(result);
        assertTrue(result.has("children"));
    }

    @Test
    @DisplayName("Test procesamiento de respuesta XML")
    void testXmlResponseProcessing() throws Exception {
        String xmlResponse = "<root><test>value</test></root>";
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(xmlResponse.getBytes()));

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("getXmlResponse", HttpURLConnection.class);
        method.setAccessible(true);
        Document result = (Document) method.invoke(service, mockConnection);

        assertNotNull(result);
        assertEquals("root", result.getDocumentElement().getNodeName());
    }

    @Test
    @DisplayName("Test extracción de nombres de archivos XML")
    void testExtractXmlFileNames() throws Exception {
        String jsonString = "{\"children\":{\"values\":[" +
                "{\"type\":\"FILE\",\"path\":{\"name\":\"test1.xml\"}}," +
                "{\"type\":\"FILE\",\"path\":{\"name\":\"test2.txt\"}}," +
                "{\"type\":\"FILE\",\"path\":{\"name\":\"test3.XML\"}}," +
                "{\"type\":\"DIRECTORY\",\"path\":{\"name\":\"folder\"}}" +
                "]}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("extractXmlFileNames", JsonNode.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<String> result = (java.util.List<String>) method.invoke(service, jsonNode);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("test1.xml"));
        assertTrue(result.contains("test3.XML"));
        assertFalse(result.contains("test2.txt"));
        assertFalse(result.contains("folder"));
    }

    @Test
    @DisplayName("Test conversión de documento XML a string")
    void testDocumentToString() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream("<test>content</test>".getBytes()));

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("documentToString", Document.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, doc);

        assertNotNull(result);
        assertTrue(result.contains("<test>content</test>"));
    }

    @Test
    @DisplayName("Test escritura de archivo")
    void testWriteStringToFile() throws Exception {
        Path testFile = tempDir.resolve("test.txt");
        String content = "Test content";

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("writeStringToFile", String.class, String.class);
        method.setAccessible(true);
        method.invoke(service, content, testFile.toString());

        assertTrue(Files.exists(testFile));
        String fileContent = Files.readString(testFile);
        assertEquals(content, fileContent);
    }

    @Test
    @DisplayName("Test eliminación recursiva de directorio")
    void testDeleteDirectoryRecursively() throws Exception {
        File testDir = tempDir.resolve("testDir").toFile();
        testDir.mkdirs();

        File subDir = new File(testDir, "subDir");
        subDir.mkdirs();

        File testFile = new File(testDir, "test.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("test content");
        }

        File subFile = new File(subDir, "sub.txt");
        try (FileWriter writer = new FileWriter(subFile)) {
            writer.write("sub content");
        }

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("deleteDirectoryRecursively", File.class);
        method.setAccessible(true);
        method.invoke(service, testDir);

        assertFalse(testDir.exists());
        assertFalse(subDir.exists());
        assertFalse(testFile.exists());
        assertFalse(subFile.exists());
    }

    @Test
    @DisplayName("Test manejo de errores en XML")
    void testXmlErrorHandling() throws Exception {
        String invalidXml = "invalid xml content";
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidXml.getBytes()));

        OptimizedGitRepositoryService service = spy(gitRepositoryService);

        java.lang.reflect.Method method = OptimizedGitRepositoryService.class
                .getDeclaredMethod("getXmlResponse", HttpURLConnection.class);
        method.setAccessible(true);

        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
            method.invoke(service, mockConnection);
        });
    }

    @Test
    @DisplayName("Test validación de códigos de error de MallaGenerationException")
    void testMallaGenerationExceptionErrorCodes() {
        MallaGenerationException configError = MallaGenerationException.configurationError("Config test");
        assertEquals("MALLA_CONFIGURATION_ERROR", configError.getErrorCode());

        MallaGenerationException xmlError = MallaGenerationException.xmlGenerationError("XML test", new RuntimeException());
        assertEquals("MALLA_XML_GENERATION_ERROR", xmlError.getErrorCode());

        MallaGenerationException validationError = MallaGenerationException.validationError("Validation test");
        assertEquals("MALLA_VALIDATION_ERROR", validationError.getErrorCode());
    }
}
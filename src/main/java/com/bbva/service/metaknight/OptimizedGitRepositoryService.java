package com.bbva.service.metaknight;

import com.bbva.core.exception.MallaGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OptimizedGitRepositoryService{
    private static final Logger LOGGER = Logger.getLogger(OptimizedGitRepositoryService.class.getName());
    private static final String PROJECT_KEY = "pe_pdit_app-id-31856_dsg";
    private static final String REPOSITORY_SLUG = "pe-dh-datio-xml-dimensions-controlm";
    private static final String BASE_BITBUCKET_URL = "https://bitbucket.globaldevtools.bbva.com/bitbucket";

    // Credencialesssss
    private static final String USERNAME = "patrick.andonayre";
    private static final String TOKEN = "BBDC-NTI1OTcxMTI4NDQyOnP5X4fegftIyIfXzQAECnloBO2p"; //poner en varables de entorno

    private final String sessionId = java.util.UUID.randomUUID().toString().substring(0, 8);
    private final String TEMP_REPO_PATH = System.getProperty("java.io.tmpdir") + "/optimized-controlm-cache-" + sessionId;

    public String getRepositoryPath() throws MallaGenerationException {
        try {
            Path tempPath = Paths.get(TEMP_REPO_PATH);

            // Crear directorio temporal si no existe
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
                LOGGER.info("Directorio temporal creado: " + TEMP_REPO_PATH);
            }

            return TEMP_REPO_PATH;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error configurando directorio temporal: " + e.getMessage());
        }
    }

    public String getUuaaDirectoryPath(String uuaa, String countryType) throws MallaGenerationException {
        try {
            String repoPath = getRepositoryPath();
            String uuaaPath = repoPath + "/" + countryType + "/" + uuaa.toUpperCase();

            // Verificar que la UUAA existe usando APIs REST -- Se puede mejorar ---############
            if (!directoryExistsInRepo(countryType + "/" + uuaa.toUpperCase())) {
                throw MallaGenerationException.configurationError(
                        "Directorio UUAA no encontrado: " + uuaaPath);
            }

            Path localPath = Paths.get(uuaaPath);
            if (Files.exists(localPath)) {
                deleteDirectoryRecursively(localPath.toFile());
            }
            Files.createDirectories(localPath);

            downloadUuaaXmlFiles(uuaa, countryType, localPath.toString());

            return uuaaPath;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error obteniendo directorio UUAA: " + e.getMessage());
        }
    }

    private boolean directoryExistsInRepo(String directoryPath) {
        try {
            String apiUrl = buildBrowseApiUrl(directoryPath);
            HttpURLConnection connection = createAuthenticatedConnection(apiUrl);

            int responseCode = connection.getResponseCode();
            return responseCode == 200;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error verificando directorio: " + directoryPath, e);
            return false;
        }
    }

    private void downloadUuaaXmlFiles(String uuaa, String countryType, String localPath) throws Exception {
        String directoryPath = countryType + "/" + uuaa.toUpperCase();
        List<String> xmlFiles = getXmlFilesFromRepo(directoryPath);

        LOGGER.info("Descargando " + xmlFiles.size() + " archivos XML para UUAA: " + uuaa);

        for (String xmlFile : xmlFiles) {
            downloadXmlFile(directoryPath + "/" + xmlFile, localPath + "/" + xmlFile);
        }

        LOGGER.info("Archivos XML descargados exitosamente para UUAA: " + uuaa);
    }

    private List<String> getXmlFilesFromRepo(String directoryPath) throws Exception {
        String apiUrl = buildBrowseApiUrl(directoryPath);
        HttpURLConnection connection = createAuthenticatedConnection(apiUrl);

        JsonNode response = getJsonResponse(connection);
        List<String> xmlFiles = new ArrayList<>();

        if (response != null && response.has("children")) {
            JsonNode children = response.get("children");
            if (children.has("values")) {
                for (JsonNode child : children.get("values")) {
                    if (child.get("type").asText().equals("FILE")) {
                        String fileName = child.get("path").get("name").asText();
                        if (fileName.toLowerCase().endsWith(".xml")) {
                            xmlFiles.add(fileName);
                        }
                    }
                }
            }
        }

        return xmlFiles;
    }

    private void downloadXmlFile(String remoteFilePath, String localFilePath) throws Exception {
        String apiUrl = buildRawApiUrl(remoteFilePath);
        HttpURLConnection connection = createAuthenticatedConnection(apiUrl);

        Document xmlDocument = getXmlResponse(connection);
        String xmlContent = documentToString(xmlDocument);

        writeStringToFile(xmlContent, localFilePath);

        LOGGER.info("Archivo XML FRESCO descargado: " + remoteFilePath + " -> " + localFilePath);
    }

    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        java.io.StringWriter writer = new java.io.StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    private void writeStringToFile(String content, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }

    private String buildBrowseApiUrl(String path) {
        return String.format("%s/rest/api/1.0/projects/%s/repos/%s/browse/%s",
                BASE_BITBUCKET_URL, PROJECT_KEY, REPOSITORY_SLUG, path);
    }

    private String buildRawApiUrl(String path) {
        return String.format("%s/rest/api/1.0/projects/%s/repos/%s/raw/%s",
                BASE_BITBUCKET_URL, PROJECT_KEY, REPOSITORY_SLUG, path);
    }

    private HttpURLConnection createAuthenticatedConnection(String url) throws IOException {
        URL urlConnection = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
        connection.setRequestMethod("GET");

        String encoded = Base64.getEncoder().encodeToString((USERNAME + ":" + TOKEN).getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encoded);

        return connection;
    }

    private JsonNode getJsonResponse(HttpURLConnection connection) throws IOException {
        try (InputStream input = connection.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(input);
        }
    }

    private Document getXmlResponse(HttpURLConnection connection) throws Exception {
        try (InputStream input = connection.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(input);
        }
    }

    public void cleanupCache() {
        try {
            Path tempPath = Paths.get(TEMP_REPO_PATH);
            if (Files.exists(tempPath)) {
                deleteDirectoryRecursively(tempPath.toFile());
            }
            LOGGER.info("Archivos temporales limpiados");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error limpiando archivos temporales: " + e.getMessage(), e);
        }
    }

    private void deleteDirectoryRecursively(File directory) throws IOException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectoryRecursively(file);
                    } else {
                        if (!file.delete()) {
                            LOGGER.warning("No se pudo eliminar archivo: " + file.getAbsolutePath());
                        }
                    }
                }
            }
            if (!directory.delete()) {
                LOGGER.warning("No se pudo eliminar directorio: " + directory.getAbsolutePath());
            }
        }
    }
}
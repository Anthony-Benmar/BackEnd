package com.bbva.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.InputStream;

public class BitbucketApiService {

    public static final String URL_BITBUCKET_API_BASE = "https://bitbucket.globaldevtools.bbva.com/bitbucket/rest/api/1.0/";
    public static final String URL_BITBUCKET_API_RAW = URL_BITBUCKET_API_BASE + "projects/%s/repos/%s/raw/%s";
    public static final String URL_BITBUCKET_API_REPOSITORY = URL_BITBUCKET_API_BASE + "projects/%s/repos/%s/%s/%s/%s";

    private String[] extractUrlParts(String url) throws IllegalArgumentException {
        String[] parts = url.split("/");
        if (parts.length < 10) {
            throw new IllegalArgumentException("URL del Pull Request no es válida.");
        }
        return parts;
    }

    private String buildApiUrl(String pullRequestUrl, String route, String action, String parameters) {
        String[] parts = extractUrlParts(pullRequestUrl);
        String project = parts[5];
        String repository = parts[7];
        String prId = parts[9];

        String baseUrl = String.format(URL_BITBUCKET_API_REPOSITORY, project, repository, route, prId, action);

        return (parameters == null || parameters.isEmpty())
                ? baseUrl
                : baseUrl + "?" + parameters;
    }

    public JsonNode getPullRequestInfo(String pullRequestUrl, String userName, String token) throws IOException {
        String route = "pull-requests";
        String apiUrl = buildApiUrl(pullRequestUrl, route,"","");
        HttpURLConnection connection = createAuthenticatedConnection(apiUrl, userName, token);
        return getJsonResponse(connection);
    }

    public JsonNode getPullRequestChanges(String pullRequestUrl, String userName, String token) throws IOException {
        String route = "pull-requests";
        String action = "changes";
        String apiUrl = buildApiUrl(pullRequestUrl, route, action,"");
        HttpURLConnection connection = createAuthenticatedConnection(apiUrl, userName, token);
        return getJsonResponse(connection);
    }

    public Document getPullRequestFileInfo(String pullRequestUrl, String userName, String token, String fileRoute, String commitId) throws IOException, ParserConfigurationException, SAXException {
        String parameters = (commitId != null && !commitId.isEmpty()) ? "at=" + commitId : "";
        String apiUrl = buildRawUrl(pullRequestUrl, fileRoute, parameters);
        HttpURLConnection connection = createAuthenticatedConnection(apiUrl, userName, token);
        return getXmlResponse(connection);
    }

    private String buildRawUrl(String pullRequestUrl, String fileRoute, String parameters) {
        String[] parts = extractUrlParts(pullRequestUrl);
        String project = parts[5];
        String repository = parts[7];

        String baseUrl = String.format(URL_BITBUCKET_API_RAW, project, repository, fileRoute);

        return (parameters == null || parameters.isEmpty())
                ? baseUrl
                : baseUrl + "?" + parameters;
    }

    protected HttpURLConnection createAuthenticatedConnection(String url, String userName, String token) throws IOException {
        URL urlConection = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlConection.openConnection();
        connection.setRequestMethod("GET");
        String encoded = Base64.getEncoder().encodeToString((userName + ":" + token).getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encoded);
        return connection;
    }

    protected JsonNode getJsonResponse(HttpURLConnection connection) throws IOException {
        try (InputStream input = connection.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(input);
        }
    }

    protected Document getXmlResponse(HttpURLConnection connection) throws IOException, ParserConfigurationException, SAXException {
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

}

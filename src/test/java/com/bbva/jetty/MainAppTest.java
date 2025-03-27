package com.bbva.jetty;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MainAppTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void main() throws Exception {
        // When
        MainApp.main(null);
        URI uri = new URI("http://localhost:8080/swagger/index.html");
        HttpURLConnection httpURLConnection = (HttpURLConnection)uri.toURL().openConnection();
        httpURLConnection.connect();

        // Then
        Assertions.assertEquals(HttpStatus.OK_200, httpURLConnection.getResponseCode());
        MainApp.stop();
    }

    @Test
    void getWebAppContext() throws Exception {
        // Given
        final String WEBAPP_RESOURCES_LOCATION = "META-INF/resources";
        URL webAppDir = Thread.currentThread().getContextClassLoader().getResource(WEBAPP_RESOURCES_LOCATION);
        assertNotNull(webAppDir);

        // When
        WebAppContext webAppContext = MainApp.getWebAppContext(webAppDir);

        // Then
        assertEquals("/", webAppContext.getContextPath());
        assertEquals(WEBAPP_RESOURCES_LOCATION + "/WEB-INF/web.xml", webAppContext.getDescriptor());
    }

}
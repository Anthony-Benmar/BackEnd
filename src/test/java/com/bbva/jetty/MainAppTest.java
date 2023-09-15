package com.bbva.jetty;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class MainAppTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void main() throws Exception {
        // When
        MainApp.main(null);
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
                "http://localhost:8080/swagger/index.html"
        ).openConnection();
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

    @Test
    void setLoggerHandler() throws Exception {
        // When
        SystemLambda.withEnvironmentVariable("GOOGLE_CLOUD_PROJECT", "fake-project-id")
                .execute(MainApp::setLoggerHandler);

        // Then
        Assertions.assertEquals(1, MainApp.ROOT_LOOGER.getHandlers().length);
    }
}
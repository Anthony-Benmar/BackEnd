package com.bbva.jetty;

import com.bbva.fga.utils.EnvironmentUtils;
import com.google.cloud.logging.LoggingHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class MainApp {

    public static final String WEBAPP_RESOURCES_LOCATION = "META-INF/resources";

    public static final Logger ROOT_LOOGER = Logger.getLogger("RootLogger");

    static Server server;

    public static void main(String[] args) throws Exception {

        if (!EnvironmentUtils.isLocalEnvironment()) {
            setLoggerHandler();
        }

        URL webAppDir = Thread.currentThread().getContextClassLoader().getResource(WEBAPP_RESOURCES_LOCATION);
        if (webAppDir == null) {
            throw new MainAppRuntimeException(
                    String.format("Unable to find %s directory into the JAR file", WEBAPP_RESOURCES_LOCATION)
            );
        }

        var webAppContext = getWebAppContext(webAppDir);

        var port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        server = new Server(port);
        server.setHandler(webAppContext);
        server.start();
    }

    static void stop() throws Exception {
        server.stop();
    }


    static void setLoggerHandler() {
        // Remove the default handler, which logs to stderr in a format Cloud Logging doesn't parse
        for (Handler handler : ROOT_LOOGER.getHandlers()) {
            ROOT_LOOGER.removeHandler(handler);
        }

        // Add the logger that logs in a format Cloud Logging does parse
        ROOT_LOOGER.addHandler(new LoggingHandler());
    }

    protected static WebAppContext getWebAppContext(URL webAppDir) throws URISyntaxException {
        var webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase(webAppDir.toURI().toString());
        webAppContext.setDescriptor(WEBAPP_RESOURCES_LOCATION + "/WEB-INF/web.xml");
        // webAppContext.setErrorHandler(new MainAppErrorHandler());
        return webAppContext;
    }
}
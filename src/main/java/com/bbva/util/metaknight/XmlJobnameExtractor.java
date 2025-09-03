package com.bbva.util.metaknight;

import com.bbva.core.exception.MallaGenerationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlJobnameExtractor {
    private static final Logger LOGGER = Logger.getLogger(XmlJobnameExtractor.class.getName());
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("pe\\.[^.]+\\.app-id-[^.]+\\.pro");
    public List<String> extractJobnames(String xmlFilePath) throws MallaGenerationException {
        try {
            List<String> jobnames = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            document.getDocumentElement().normalize();

            NodeList jobElements = document.getElementsByTagName("JOB");

            for (int i = 0; i < jobElements.getLength(); i++) {
                Element jobElement = (Element) jobElements.item(i);
                String jobname = jobElement.getAttribute("JOBNAME");

                if (jobname != null && !jobname.trim().isEmpty()) {
                    jobnames.add(jobname.trim());
                }
            }
            return jobnames;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error extrayendo jobnames de XML: " + xmlFilePath + " - " + e.getMessage());
        }
    }
    public String extractNamespaceFromXml(String xmlFilePath) throws MallaGenerationException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            document.getDocumentElement().normalize();

            NodeList variableElements = document.getElementsByTagName("VARIABLE");

            for (int i = 0; i < variableElements.getLength(); i++) {
                Element variableElement = (Element) variableElements.item(i);
                String nameAttribute = variableElement.getAttribute("NAME");

                if ("%%SENTRY_JOB".equals(nameAttribute)) {
                    String value = variableElement.getAttribute("VALUE");

                    if (value != null && !value.isEmpty()) {
                        Matcher matcher = NAMESPACE_PATTERN.matcher(value);
                        if (matcher.find()) {
                            String namespace = matcher.group();
                            return namespace;
                        }
                    }
                }
            }
            return null;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error extrayendo namespace de XML: " + xmlFilePath + " - " + e.getMessage());
        }
    }

    public List<String> filterJobnamesByPattern(List<String> jobnames, String uuaa, String jobType) {
        List<String> filtered = new ArrayList<>();

        String patternString = "^" + uuaa.toUpperCase() + jobType + "\\d+$";
        Pattern pattern = Pattern.compile(patternString);

        for (String jobname : jobnames) {
            if (pattern.matcher(jobname).matches()) {
                filtered.add(jobname);
            }
        }
        filtered.sort(String::compareTo);
        return filtered;
    }
    public String getLastJobname(List<String> sortedJobnames) {
        if (sortedJobnames == null || sortedJobnames.isEmpty()) {
            return null;
        }
        return sortedJobnames.get(sortedJobnames.size() - 1);
    }
    public String getNextJob(String previousJobname) {
        if (previousJobname == null || previousJobname.length() < 4) {
            throw new IllegalArgumentException("Jobname anterior inválido: " + previousJobname);
        }
        try {
            String numberPart = previousJobname.substring(previousJobname.length() - 4);
            int currentNumber = Integer.parseInt(numberPart);
            int nextNumber = currentNumber + 1;

            String nextNumberFormatted = String.format("%04d", nextNumber);
            String prefix = previousJobname.substring(0, previousJobname.length() - 4);
            String nextJobname = prefix + nextNumberFormatted;

            return nextJobname;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No se pudo extraer número de jobname: " + previousJobname);
        }
    }
    public boolean isValidControlMXml(String xmlFilePath) {
        try {
            File xmlFile = new File(xmlFilePath);
            if (!xmlFile.exists() || !xmlFile.isFile()) {
                return false;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList jobElements = document.getElementsByTagName("JOB");
            return jobElements.getLength() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "XML inválido: " + xmlFilePath + " - " + e.getMessage(), e);
            return false;
        }
    }
}
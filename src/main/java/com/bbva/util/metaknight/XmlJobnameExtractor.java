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

//Extractor de jobnames y namespaces desde archivos XML de Control-M
//xml_jobname_extractor.py
public class XmlJobnameExtractor {

    private static final Logger LOGGER = Logger.getLogger(XmlJobnameExtractor.class.getName());

    // Patrón para extraer namespace: pe.*.app-id-*.pro
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("pe\\.[^.]+\\.app-id-[^.]+\\.pro");

    //Extrae todos los JOBNAME values desde un archivo XML
    // extract_jobnames() en Python
    public List<String> extractJobnames(String xmlFilePath) throws MallaGenerationException {
        try {
            List<String> jobnames = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            // Normalizar el documento
            document.getDocumentElement().normalize();

            // Buscar todos los elementos JOB
            NodeList jobElements = document.getElementsByTagName("JOB");

            for (int i = 0; i < jobElements.getLength(); i++) {
                Element jobElement = (Element) jobElements.item(i);
                String jobname = jobElement.getAttribute("JOBNAME");

                if (jobname != null && !jobname.trim().isEmpty()) {
                    jobnames.add(jobname.trim());
                }
            }

            LOGGER.info("Extraídos " + jobnames.size() + " jobnames de " + xmlFilePath);
            return jobnames;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error extrayendo jobnames de XML: " + xmlFilePath + " - " + e.getMessage());
        }
    }

    //Extrae jobnames con detalles adicionales
    //extract_jobnames_with_details()
    public List<Map<String, String>> extractJobnamesWithDetails(String xmlFilePath) throws MallaGenerationException {
        try {
            List<Map<String, String>> jobs = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            document.getDocumentElement().normalize();

            NodeList jobElements = document.getElementsByTagName("JOB");

            for (int i = 0; i < jobElements.getLength(); i++) {
                Element jobElement = (Element) jobElements.item(i);

                Map<String, String> jobInfo = new HashMap<>();
                jobInfo.put("JOBNAME", getAttributeOrEmpty(jobElement, "JOBNAME"));
                jobInfo.put("APPLICATION", getAttributeOrEmpty(jobElement, "APPLICATION"));
                jobInfo.put("SUB_APPLICATION", getAttributeOrEmpty(jobElement, "SUB_APPLICATION"));
                jobInfo.put("DESCRIPTION", getAttributeOrEmpty(jobElement, "DESCRIPTION"));
                jobInfo.put("RUN_AS", getAttributeOrEmpty(jobElement, "RUN_AS"));
                jobInfo.put("NODEID", getAttributeOrEmpty(jobElement, "NODEID"));
                jobInfo.put("CREATED_BY", getAttributeOrEmpty(jobElement, "CREATED_BY"));
                jobInfo.put("PARENT_FOLDER", getAttributeOrEmpty(jobElement, "PARENT_FOLDER"));

                jobs.add(jobInfo);
            }

            return jobs;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error extrayendo detalles de jobnames: " + xmlFilePath + " - " + e.getMessage());
        }
    }

    //Extrae el namespace desde un archivo XML de Control-M
    //  extract_namespace_from_xml()
    public String extractNamespaceFromXml(String xmlFilePath) throws MallaGenerationException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            document.getDocumentElement().normalize();

            // Buscar todos los elementos VARIABLE con NAME="%%SENTRY_JOB"
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
                            LOGGER.info("Namespace extraído de " + xmlFilePath + ": " + namespace);
                            return namespace;
                        }
                    }
                }
            }

            LOGGER.warning("No se encontró namespace en " + xmlFilePath);
            return null;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error extrayendo namespace de XML: " + xmlFilePath + " - " + e.getMessage());
        }
    }

    //Num de jobs en un archivo
    public int countJobsInXml(String xmlFilePath) throws MallaGenerationException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            NodeList jobElements = document.getElementsByTagName("JOB");
            return jobElements.getLength();

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error contando jobs en XML: " + xmlFilePath + " - " + e.getMessage());
        }
    }

    //Filtra jobnames por un patrón específico
    //Útil para filtrar por tipo de job (CP, VP, TP, DP, WP)

    public List<String> filterJobnamesByPattern(List<String> jobnames, String uuaa, String jobType) {
        List<String> filtered = new ArrayList<>();

        // Patrón: UUAA + TIPO + NÚMEROS (sin letras después del tipo)
        String patternString = "^" + uuaa.toUpperCase() + jobType + "\\d+$";
        Pattern pattern = Pattern.compile(patternString);

        for (String jobname : jobnames) {
            if (pattern.matcher(jobname).matches()) {
                filtered.add(jobname);
            }
        }

        // Ordenar la lista
        filtered.sort(String::compareTo);

        LOGGER.info("Filtrados " + filtered.size() + " jobnames para patrón " + patternString);
        return filtered;
    }

    //Obtiene el último jobname de una lista ordenada
    public String getLastJobname(List<String> sortedJobnames) {
        if (sortedJobnames == null || sortedJobnames.isEmpty()) {
            return null;
        }

        return sortedJobnames.get(sortedJobnames.size() - 1);
    }

    //Calcula el siguiente jobname disponible
    //get_next_job()
    public String getNextJob(String previousJobname) {
        if (previousJobname == null || previousJobname.length() < 4) {
            throw new IllegalArgumentException("Jobname anterior inválido: " + previousJobname);
        }

        try {
            // Extraer los últimos 4 dígitos
            String numberPart = previousJobname.substring(previousJobname.length() - 4);
            int currentNumber = Integer.parseInt(numberPart);
            int nextNumber = currentNumber + 1;

            // Formatear con padding de ceros
            String nextNumberFormatted = String.format("%04d", nextNumber);

            // Construir el nuevo jobname
            String prefix = previousJobname.substring(0, previousJobname.length() - 4);
            String nextJobname = prefix + nextNumberFormatted;

            LOGGER.info("Siguiente jobname calculado: " + previousJobname + " -> " + nextJobname);
            return nextJobname;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("No se pudo extraer número de jobname: " + previousJobname);
        }
    }

    private String getAttributeOrEmpty(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        return value != null ? value.trim() : "";
    }

    // Extrae el parent folder desde el primer job encontrado en el XML
    public String extractParentFolder(String xmlFilePath) throws MallaGenerationException {
        try {
            List<Map<String, String>> jobsWithDetails = extractJobnamesWithDetails(xmlFilePath);

            if (!jobsWithDetails.isEmpty()) {
                String parentFolder = jobsWithDetails.get(0).get("PARENT_FOLDER");
                if (parentFolder != null && !parentFolder.trim().isEmpty()) {
                    LOGGER.info("Parent folder extraído: " + parentFolder);
                    return parentFolder.trim();
                }
            }

            LOGGER.warning("No se encontró parent folder en " + xmlFilePath);
            return null;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error extrayendo parent folder: " + xmlFilePath + " - " + e.getMessage());
        }
    }

    // Valida que un archivo XML sea válido para procesamiento
    public boolean isValidControlMXml(String xmlFilePath) {
        try {
            File xmlFile = new File(xmlFilePath);
            if (!xmlFile.exists() || !xmlFile.isFile()) {
                return false;
            }

            // Intentar parsear el XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Verificar que tenga al menos un elemento JOB
            NodeList jobElements = document.getElementsByTagName("JOB");
            return jobElements.getLength() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "XML inválido: " + xmlFilePath + " - " + e.getMessage(), e);
            return false;
        }
    }
}
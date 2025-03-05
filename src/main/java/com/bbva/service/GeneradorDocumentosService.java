package com.bbva.service;


import com.bbva.dto.jira.request.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.xwpf.usermodel.*;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.bbva.util.ApiJiraName;

public class GeneradorDocumentosService {
    private static final Logger LOGGER = Logger.getLogger(GeneradorDocumentosService.class.getName());
    public static final String C204MALLAS_BASE64 = "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";
    private JiraApiService jiraApiService;
    public static byte[] getDocumentoBytes() {
        return Base64.getDecoder().decode(C204MALLAS_BASE64);
    }

    public String generarDocumentosMallas(GeneradorDocumentosMallasRequest dto) {
        byte[] documento = getDocumentoBytes();
        try {

            documento = Files.readAllBytes(Path.of("C204 - MALLA - $MALLAS_v1.docx"));
            System.out.println(documento);
        } catch (IOException e) {

            e.printStackTrace();

        }
        return "";
    }

    public byte[] generarDocumentoModificado(GeneradorDocumentosMallasRequest dto) throws Exception {
        byte[] documentoBytes = getDocumentoBytes();
        this.jiraApiService = new JiraApiService();
        String metadata = getMetadataIssues(dto);
        JsonObject jsonResponse = JsonParser.parseString(metadata).getAsJsonObject();
        Map<String, Map<String, Long>> conteoMallas = conteoMallas(dto.getDataDocumentosMallas());
        Map<String, String> descripcionMallas = descripcionMallas(conteoMallas);
        Map<String, Map<String, String>> jobsDetail = buildJobsSummaryDetail(dto.getDataDocumentosMallas());
        Map<String, Map<String, List<String>>> jobSummaryType = buildJobsSummaryType(dto.getDataDocumentosMallas());
        List<Map.Entry<String, Map<String, List<String>>>> listJobSummary = new ArrayList<>(jobSummaryType.entrySet());
        List<Map.Entry<String, Map<String, String>>> listJobsDetail = new ArrayList<>(jobsDetail.entrySet());
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(documentoBytes))) {
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                String texto = run.getText(0);
                                if (texto != null) {

                                    if (texto.contains("$DESARROLLADOR")) {
                                        texto = texto.replace("$DESARROLLADOR", dto.getName());
                                    }
                                    if (texto.contains("$MALLASTOTALCANTIDAD")) {
                                        texto = texto.replace("$MALLASTOTALCANTIDAD", String.valueOf(conteoMallas.size()));
                                    }
                                    if (texto.contains("$MALLASJOBSTOTALCANTIDAD")) {
                                        texto = texto.replace("$MALLASJOBSTOTALCANTIDAD", String.valueOf(
                                                conteoMallas.values().stream()
                                                        .flatMap(innerMap -> innerMap.values().stream())
                                                        .mapToLong(Long::longValue)
                                                        .sum()
                                        ));
                                    }

                                    for (Map.Entry<String, String> entry : descripcionMallas.entrySet()) {
                                        if (texto.contains(entry.getKey())) {
                                            texto = texto.replace(entry.getKey(), entry.getValue());
                                        }
                                    }

                                    for (int i = 1; i <= 5; i++) {

                                        String summary = "";
                                        if (i <= listJobSummary.size()) {
                                            Map.Entry<String, Map<String, List<String>>> entry = listJobSummary.get(i - 1);

                                            summary = formatJobSummary(entry.getValue());
                                        }
                                        texto = texto.replace("$MALLASJOBS" + i, summary);


                                        StringBuilder finalSummary = new StringBuilder();
                                        if (i <= listJobsDetail.size()) {
                                            Map.Entry<String, Map<String, String>> entry = listJobsDetail.get(i - 1);
                                            String folder = entry.getKey();
                                            finalSummary.append("Folder: ").append(folder).append("\n");
                                            for (Map.Entry<String, String> jobEntry : entry.getValue().entrySet()) {

                                                finalSummary.append(jobEntry.getValue())
                                                        .append("\n\n");
                                            }
                                        }
                                        texto = texto.replace("$MALLASJOBSDETALLE" + i, finalSummary.toString());
                                    }


                                    if (texto.contains("\n")) {

                                        String[] lines = texto.split("\n");

                                        run.setText(lines[0], 0);

                                        for (int i = 1; i < lines.length; i++) {
                                            run.addBreak();
                                            run.setText(lines[i]);
                                        }
                                    } else {

                                        run.setText(texto, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int fixedTables = 3;
            int totalTables = document.getTables().size();
            int dynamicTablesTotal = totalTables - fixedTables;
            int requiredDynamicTables = conteoMallas.size();

            if (requiredDynamicTables < dynamicTablesTotal) {
                List<XWPFTable> allTables = new ArrayList<>(document.getTables());
                for (int i = fixedTables + requiredDynamicTables; i < allTables.size(); i++) {
                    XWPFTable tableToRemove = allTables.get(i);
                    int pos = document.getPosOfTable(tableToRemove);
                    document.removeBodyElement(pos);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);


            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Map<String, Long>>  conteoMallas (DataDocumentosMallas dataDocumentosMallas){
        Map<String, Map<String, Long>> result = new HashMap<>();
        for (DataDocumentosMallasFolders folder : dataDocumentosMallas.getFolders()) {
            String folderName = folder.getFolder();

            Map<String, Long> stateCount = folder.getJobnames().stream()
                    .collect(Collectors.groupingBy(DataDocumentosMallasJobName::getEstado, Collectors.counting()));
            result.put(folderName, stateCount);
        }
        return result;
    }

    private Map<String, String> descripcionMallas (Map<String, Map<String, Long>> conteoMallas){
        List<Map.Entry<String, Map<String, Long>>> folderList = new ArrayList<>(conteoMallas.entrySet());

        Map<String, String> placeholders = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            if (i <= folderList.size()) {
                String folderName = folderList.get(i - 1).getKey();
                Map<String, Long> innerMap = folderList.get(i - 1).getValue();
                long nuevos = innerMap.getOrDefault("nuevo", 0L);
                long modificado = innerMap.getOrDefault("modificado", 0L);
                long eliminado = innerMap.getOrDefault("eliminado", 0L);
                String summary = "Nuevos: " + nuevos + " jobs\n" +
                        "Modificados: " + modificado + " jobs\n" +
                        "Eliminados: " + eliminado + " jobs";
                placeholders.put("$MALLASDESCRIPCION" + i, "Se actualiza la malla: " + folderName);
                placeholders.put("$MALLASJOBSRESUMEN" + i, summary);
            } else {

                placeholders.put("$MALLASDESCRIPCION" + i, "");
                placeholders.put("$MALLASJOBSRESUMEN" + i, "");
            }
        }
        return  placeholders;
    }

    private Map<String, Map<String, String>> parseXml(String xml) {
        Map<String, Map<String, String>> result = new HashMap<>();

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));


            NodeList folderNodes = doc.getElementsByTagName("FOLDER");
            for (int i = 0; i < folderNodes.getLength(); i++) {
                Element folderElement = (Element) folderNodes.item(i);

                String folderName = folderElement.getAttribute("FOLDER_NAME");
                Map<String, String> jobsMap = new HashMap<>();


                NodeList jobNodes = folderElement.getElementsByTagName("JOB");
                for (int j = 0; j < jobNodes.getLength(); j++) {
                    Element jobElement = (Element) jobNodes.item(j);
                    String jobName = jobElement.getAttribute("JOBNAME");


                    NodeList variableNodes = jobElement.getElementsByTagName("VARIABLE");
                    for (int k = 0; k < variableNodes.getLength(); k++) {
                        Element variableElement = (Element) variableNodes.item(k);
                        String value= "";
                        if ("%%SENTRY_PARM".equals(variableElement.getAttribute("NAME"))) {
                            value = variableElement.getAttribute("VALUE");
                        }
                        jobsMap.put(jobName, value);
                    }
                }
                result.put(folderName, jobsMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String, Map<String, String>> buildJobsSummaryDetail(DataDocumentosMallas dataDocumentosMallas) {
        Map<String, Map<String, String>> summaryMap = new HashMap<>();

        for (DataDocumentosMallasFolders folderDto : dataDocumentosMallas.getFolders()) {
            Map<String, Map<String, String>> parsedResult = parseXml(folderDto.getXml());
            String folderName = folderDto.getFolder();

            Map<String, String> jobSummaryMap = new LinkedHashMap<>();


            Map<String, String> xmlJobs = parsedResult.get(folderName);

            if (xmlJobs != null) {

                List<DataDocumentosMallasJobName> sortedJobs = folderDto.getJobnames().stream()
                        .filter(jobDto -> xmlJobs.containsKey(jobDto.getJobName()))
                        .sorted((job1, job2) -> {

                            List<String> order = Arrays.asList("nuevo", "modificado", "eliminado");
                            String state1 = job1.getEstado().toLowerCase();
                            String state2 = job2.getEstado().toLowerCase();
                            int index1 = order.indexOf(state1);
                            int index2 = order.indexOf(state2);
                            return Integer.compare(index1, index2);
                        })
                        .collect(Collectors.toList());


                for (DataDocumentosMallasJobName jobDto : sortedJobs) {
                    String jobName = jobDto.getJobName();
                    String sentryParam = xmlJobs.get(jobName);

                    String summary = jobName + "\n" + sentryParam;
                    jobSummaryMap.put(jobName, summary);
                }
            }

            summaryMap.put(folderName, jobSummaryMap);
        }

        return summaryMap;
    }

    private Map<String, Map<String, List<String>>> buildJobsSummaryType(DataDocumentosMallas dataDocumentosMallas) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        for (DataDocumentosMallasFolders folder : dataDocumentosMallas.getFolders()) {
            String folderName = folder.getFolder();

            Map<String, List<String>> stateMap = new HashMap<>();
            stateMap.put("nuevo", new ArrayList<>());
            stateMap.put("modificado", new ArrayList<>());
            stateMap.put("eliminado", new ArrayList<>());

            for (DataDocumentosMallasJobName job : folder.getJobnames()) {
                String state = job.getEstado();
                if (stateMap.containsKey(state)) {
                    stateMap.get(state).add(job.getJobName());
                }
            }
            result.put(folderName, stateMap);
        }
        return result;
    }

    public String formatJobSummary(Map<String, List<String>> folderSummary) {
        StringBuilder sb = new StringBuilder();


        List<String> nuevos = folderSummary.getOrDefault("nuevo", Collections.emptyList());
        List<String> modificados = folderSummary.getOrDefault("modificado", Collections.emptyList());
        List<String> eliminados = folderSummary.getOrDefault("eliminado", Collections.emptyList());

        sb.append("Nuevos Jobs:\n");
        for (String job : nuevos) {
            sb.append(job).append("\n");
        }

        sb.append("\nModificados Jobs:\n");
        for (String job : modificados) {
            sb.append(job).append("\n");
        }

        sb.append("\nEliminados Jobs:\n");
        for (String job : eliminados) {
            sb.append(job).append("\n");
        }

        return sb.toString().trim();
    }

    public String getMetadataIssues(GeneradorDocumentosMallasRequest dto) throws Exception {
        var tickets = List.of(dto.getUrlJira());
        var query = "key%20in%20(" + String.join(",", tickets) + ")";

        var url = ApiJiraName.URL_API_JIRA_SQL + query + this.jiraApiService.getQuerySuffixURL();
        return this.jiraApiService.GetJiraAsync(dto.getUserName(), dto.getToken() ,url);
    }
}

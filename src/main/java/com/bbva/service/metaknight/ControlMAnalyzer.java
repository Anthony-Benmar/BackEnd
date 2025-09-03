package com.bbva.service.metaknight;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.util.metaknight.XmlJobnameExtractor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

// Analiza XMLs existentes para calcular siguiente jobnames disponibles - clase controlM en el cod que pasaron

@Getter
@Setter
public class ControlMAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(ControlMAnalyzer.class.getName());

    // Mapeo de frecuencias como en Python
    private static final Map<String, String> FREQUENCY_MAP = Map.of(
            "Daily", "DIA",
            "Monthly", "MEN",
            "Weekly", "SEM"
    );

    // Tipos de jobs que buscamos
    private static final String[] JOB_TYPES = {"CP", "VP", "TP", "DP", "WP"};
    private final OptimizedGitRepositoryService gitService;
    private final XmlJobnameExtractor xmlExtractor;

    // Datos calculados
    private String uuaa;
    private String uuaaUpper;
    private String frequency;
    private List<String> totalJobnames;
    private Map<String, Integer> xmlArray;
    private List<String> totalFolderJobnames;
    private String namespace;
    private String jobXml;
    private String parentFolder;

    // Últimos jobnames por tipo
    private String lastCp;
    private String lastVp;
    private String lastTp;
    private String lastDp;
    private String lastWp;

    // Siguientes jobnames disponibles
    private String transfer;
    private String copy;
    private String fw;
    private String hs;
    private String kbr;
    private String hr;
    private String kbm;
    private String hm;
    private String d1;
    private String d2;

    //Para L1T
    private boolean tieneL1TMallas;
    private String krbL1t; //se queda, solo para l1t
    private String hmmL1t; //se queda, solo para l1t

    public ControlMAnalyzer(String uuaa, String frequency, OptimizedGitRepositoryService gitService, boolean tieneL1TMallas)
            throws MallaGenerationException {

        this.gitService = gitService;
        this.xmlExtractor = new XmlJobnameExtractor();
        this.uuaa = uuaa.toLowerCase();
        this.uuaaUpper = uuaa.toUpperCase();
        this.frequency = frequency;
        this.tieneL1TMallas = tieneL1TMallas;

        // Ejecutar análisis inicial
        initializeAnalysis();
    }
    // Inicializa el análisis completo .- __init__
    private void initializeAnalysis() throws MallaGenerationException {
        LOGGER.info("Iniciando análisis Control-M para UUAA: " + uuaaUpper);

        // 1. Obtener jobnames ( get_jobname())
        analyzeJobnames();

        // 2. Encontrar XML con menos jobs (get_jobs_xml())
        findOptimalXml();

        // 3. Obtener últimos jobnames por tipo ( get_last_jobnames())
        findLastJobnames();

        // 4. Calcular siguientes jobnames disponibles
        calculateNextJobnames();

        LOGGER.info("Análisis Control-M completado exitosamente para UUAA: " + uuaaUpper);
    }

    //Analiza jobnames de XMLs - get_jobname()
    private void analyzeJobnames() throws MallaGenerationException {
        this.totalJobnames = new ArrayList<>();
        this.totalFolderJobnames = new ArrayList<>();
        this.xmlArray = new HashMap<>();
        String foundNamespace = null;

        String[] countryTypes = {"Global", "Local"};

        for (String countryType : countryTypes) {
            try {
                String uuaaPath = gitService.getUuaaDirectoryPath(uuaaUpper, countryType);
                File uuaaDir = new File(uuaaPath);

                if (!uuaaDir.exists() || !uuaaDir.isDirectory()) {
                    LOGGER.warning("Directorio no encontrado: " + uuaaPath);
                    continue;
                }

                LOGGER.info(countryType + " " + uuaaUpper);

                File[] xmlFiles = uuaaDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
                if (xmlFiles == null) {
                    continue;
                }

                for (File xmlFile : xmlFiles) {
                    if (!xmlExtractor.isValidControlMXml(xmlFile.getAbsolutePath())) {
                        continue;
                    }

                    List<String> jobnames = xmlExtractor.extractJobnames(xmlFile.getAbsolutePath());

                    if (!jobnames.isEmpty()) {
                        totalFolderJobnames.addAll(jobnames);

                        // Si el XML corresponde a la frecuencia especificada
                        String frequencyCode = FREQUENCY_MAP.get(frequency);
                        if (frequencyCode != null && xmlFile.getName().contains(frequencyCode)) {
                            totalJobnames.addAll(jobnames);
                            xmlArray.put(xmlFile.getName(), jobnames.size());

                            // Extraer namespace si aún no lo tenemos
                            if (foundNamespace == null) {
                                foundNamespace = xmlExtractor.extractNamespaceFromXml(xmlFile.getAbsolutePath());
                            }
                        }
                    }
                }

            } catch (Exception e) {
                LOGGER.warning("Error procesando directorio " + countryType + ": " + e.getMessage());
            }
        }

        this.namespace = foundNamespace;

        LOGGER.info("Total jobnames en folder: " + totalFolderJobnames.size());
        LOGGER.info("Total jobnames para frecuencia " + frequency + ": " + totalJobnames.size());
        LOGGER.info("Namespace encontrado: " + namespace);
    }

    // Encuentra el XML con menos jobs --  get_jobs_xml()
    private void findOptimalXml() throws MallaGenerationException {
        if (xmlArray.isEmpty()) {
            throw MallaGenerationException.configurationError(
                    "No se encontraron XMLs válidos para UUAA: " + uuaaUpper + " y frecuencia: " + frequency);
        }

        // Encontrar XML con menor número de jobnames
        String xmlWithLeastJobs = xmlArray.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (xmlWithLeastJobs != null) {
            // Remover extensión .xml
            this.jobXml = xmlWithLeastJobs.replace(".xml", "");
            this.parentFolder = this.jobXml;

            LOGGER.info("XML con menos jobnames: " + xmlWithLeastJobs + " (" + xmlArray.get(xmlWithLeastJobs) + " jobs)");
        } else {
            throw MallaGenerationException.configurationError(
                    "No se pudo determinar XML óptimo para UUAA: " + uuaaUpper);
        }
    }

    //Encuentra los últimos jobnames por tipo
    // get_last_jobnames()
    private void findLastJobnames() throws MallaGenerationException {

        for (String jobType : JOB_TYPES) {
            List<String> filteredJobnames = xmlExtractor.filterJobnamesByPattern(
                    totalFolderJobnames, uuaaUpper, jobType);

            String lastJobname = xmlExtractor.getLastJobname(filteredJobnames);

            // Asignar a la variable correspondiente
            switch (jobType) {
                case "CP" -> this.lastCp = lastJobname;
                case "VP" -> this.lastVp = lastJobname;
                case "TP" -> this.lastTp = lastJobname;
                case "DP" -> this.lastDp = lastJobname;
                case "WP" -> this.lastWp = lastJobname;
            }

            LOGGER.info("Último " + jobType + ": " + lastJobname + " (encontrados: " + filteredJobnames.size() + ")");
        }

        // Validar que se encontraron jobnames necesarios
        if (lastTp == null || lastVp == null || lastCp == null) {
            throw MallaGenerationException.configurationError(
                    "No se encontraron jobnames base necesarios para UUAA: " + uuaaUpper);
        }
    }

    // Calcula los siguientes jobnames disponibles
    private void calculateNextJobnames() {
        // Jobs principales
        this.transfer = xmlExtractor.getNextJob(lastTp);
        this.copy = lastDp != null ? xmlExtractor.getNextJob(lastDp) : uuaaUpper + "DP0001";
        this.fw = lastWp != null ? xmlExtractor.getNextJob(lastWp) : uuaaUpper + "WP0001";
        this.hs = xmlExtractor.getNextJob(lastVp);
        this.kbr = xmlExtractor.getNextJob(lastCp);

        // Jobs derivados
        this.hr = xmlExtractor.getNextJob(hs);
        this.kbm = xmlExtractor.getNextJob(kbr);
        this.hm = xmlExtractor.getNextJob(hr);

        //CONDICIONAL PARA L1T
        if (this.tieneL1TMallas){
            //L1T
            this.krbL1t = xmlExtractor.getNextJob(kbm);
            this.hmmL1t = xmlExtractor.getNextJob(krbL1t);
            this.d1 = xmlExtractor.getNextJob(hmmL1t);
            this.d2 = xmlExtractor.getNextJob(d1);
        }else{
            this.d1 = xmlExtractor.getNextJob(copy);
             this.d2 = xmlExtractor.getNextJob(d1);
        }

        LOGGER.info("Siguientes jobnames calculados:");
        LOGGER.info("  Transfer: " + transfer);
        LOGGER.info("  Copy: " + copy);
        LOGGER.info("  FileWatcher: " + fw);
        LOGGER.info("  Hammurabi Staging: " + hs);
        LOGGER.info("  Kirby Raw: " + kbr);
        LOGGER.info("  Hammurabi Raw: " + hr);
        LOGGER.info("  Kirby Master: " + kbm);
        LOGGER.info("  Hammurabi Master: " + hm);
        // L1t
        if (this.tieneL1TMallas){
            LOGGER.info("  Kirby L1T: " + krbL1t);
            LOGGER.info("  Hammurabi L1T: " + hmmL1t);
        }
        LOGGER.info("  Delete1: " + d1);
        LOGGER.info("  Delete2: " + d2);
    }

    // Obtiene un resumen del análisis realizado

    public Map<String, Object> getAnalysisSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("uuaa", uuaaUpper);
        summary.put("frequency", frequency);
        summary.put("namespace", namespace);
        summary.put("parentFolder", parentFolder);
        summary.put("totalJobsInFolder", totalFolderJobnames.size());
        summary.put("totalJobsForFrequency", totalJobnames.size());
        summary.put("xmlsAnalyzed", xmlArray.size());

        Map<String, String> nextJobnames = new HashMap<>();
        nextJobnames.put("transfer", transfer);
        nextJobnames.put("copy", copy);
        nextJobnames.put("fileWatcher", fw);
        nextJobnames.put("hammurabiStaging", hs);
        nextJobnames.put("kirbyRaw", kbr);
        nextJobnames.put("hammurabiRaw", hr);
        nextJobnames.put("kirbyMaster", kbm);
        nextJobnames.put("hammurabiMaster", hm);

        // L1T
        if (this.tieneL1TMallas){
            nextJobnames.put("kirbyL1t", krbL1t);
            nextJobnames.put("hammurabiL1t", hmmL1t);
        }
        nextJobnames.put("delete1", d1);
        nextJobnames.put("delete2", d2);
        summary.put("nextJobnames", nextJobnames);

        return summary;
    }
}
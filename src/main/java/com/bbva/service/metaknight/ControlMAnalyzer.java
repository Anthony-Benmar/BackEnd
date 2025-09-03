package com.bbva.service.metaknight;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.util.metaknight.XmlJobnameExtractor;
import lombok.Getter;
import lombok.Setter;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

@Getter
@Setter
public class ControlMAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(ControlMAnalyzer.class.getName());

    private static final Map<String, String> FREQUENCY_MAP = Map.of(
            "Daily", "DIA",
            "Monthly", "MEN",
            "Weekly", "SEM"
    );

    private static final String[] JOB_TYPES = {"CP", "VP", "TP", "DP", "WP"};
    private final OptimizedGitRepositoryService gitService;
    private final XmlJobnameExtractor xmlExtractor;

    private String uuaa;
    private String uuaaUpper;
    private String frequency;
    private List<String> totalJobnames;
    private Map<String, Integer> xmlArray;
    private List<String> totalFolderJobnames;
    private String namespace;
    private String jobXml;
    private String parentFolder;

    private String lastCp;
    private String lastVp;
    private String lastTp;
    private String lastDp;
    private String lastWp;

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

    private boolean tieneL1TMallas;
    private String krbL1t;
    private String hmmL1t;

    public ControlMAnalyzer(String uuaa, String frequency, OptimizedGitRepositoryService gitService, boolean tieneL1TMallas)
            throws MallaGenerationException {

        this.gitService = gitService;
        this.xmlExtractor = new XmlJobnameExtractor();
        this.uuaa = uuaa.toLowerCase();
        this.uuaaUpper = uuaa.toUpperCase();
        this.frequency = frequency;
        this.tieneL1TMallas = tieneL1TMallas;

        initializeAnalysis();
    }
    private void initializeAnalysis() throws MallaGenerationException {

        analyzeJobnames();

        findOptimalXml();

        findLastJobnames();

        calculateNextJobnames();
    }

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
                    continue;
                }
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

                        String frequencyCode = FREQUENCY_MAP.get(frequency);
                        if (frequencyCode != null && xmlFile.getName().contains(frequencyCode)) {
                            totalJobnames.addAll(jobnames);
                            xmlArray.put(xmlFile.getName(), jobnames.size());

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
    }

    private void findOptimalXml() throws MallaGenerationException {
        if (xmlArray.isEmpty()) {
            throw MallaGenerationException.configurationError(
                    "No se encontraron XMLs válidos para UUAA: " + uuaaUpper + " y frecuencia: " + frequency);
        }
        String xmlWithLeastJobs = xmlArray.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (xmlWithLeastJobs != null) {
            this.jobXml = xmlWithLeastJobs.replace(".xml", "");
            this.parentFolder = this.jobXml;

        } else {
            throw MallaGenerationException.configurationError(
                    "No se pudo determinar XML óptimo para UUAA: " + uuaaUpper);
        }
    }
    private void findLastJobnames() throws MallaGenerationException {

        for (String jobType : JOB_TYPES) {
            List<String> filteredJobnames = xmlExtractor.filterJobnamesByPattern(
                    totalFolderJobnames, uuaaUpper, jobType);

            String lastJobname = xmlExtractor.getLastJobname(filteredJobnames);

            switch (jobType) {
                case "CP" -> this.lastCp = lastJobname;
                case "VP" -> this.lastVp = lastJobname;
                case "TP" -> this.lastTp = lastJobname;
                case "DP" -> this.lastDp = lastJobname;
                case "WP" -> this.lastWp = lastJobname;
            }
        }

        if (lastTp == null || lastVp == null || lastCp == null) {
            throw MallaGenerationException.configurationError(
                    "No se encontraron jobnames base necesarios para UUAA: " + uuaaUpper);
        }
    }

    private void calculateNextJobnames() {

        this.transfer = xmlExtractor.getNextJob(lastTp);
        this.copy = lastDp != null ? xmlExtractor.getNextJob(lastDp) : uuaaUpper + "DP0001";
        this.fw = lastWp != null ? xmlExtractor.getNextJob(lastWp) : uuaaUpper + "WP0001";
        this.hs = xmlExtractor.getNextJob(lastVp);
        this.kbr = xmlExtractor.getNextJob(lastCp);

        this.hr = xmlExtractor.getNextJob(hs);
        this.kbm = xmlExtractor.getNextJob(kbr);
        this.hm = xmlExtractor.getNextJob(hr);

        if (this.tieneL1TMallas){
            this.krbL1t = xmlExtractor.getNextJob(kbm);
            this.hmmL1t = xmlExtractor.getNextJob(krbL1t);
            this.d1 = xmlExtractor.getNextJob(hmmL1t);
            this.d2 = xmlExtractor.getNextJob(d1);
        }else{
            this.d1 = xmlExtractor.getNextJob(copy);
             this.d2 = xmlExtractor.getNextJob(d1);
        }
    }
}
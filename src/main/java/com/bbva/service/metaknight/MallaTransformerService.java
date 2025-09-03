package com.bbva.service.metaknight;

import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MallaTransformerService {

    private static final String REGEX_PREFIX_OUTCOND_DOFORCEJOB = "^\\s*<(?:OUTCOND|DOFORCEJOB)[^>]*?";
    private static final String REGEX_SUFFIX_CLOSE_TAG = "[^>]*?/>\\s*[\\r\\n]*";
    private static final String REGEX_PREFIX_OUTCOND = "^\\s*<OUTCOND[^>]*?";
    private static final String REGEX_PREFIX_DOFORCEJOB = "^\\s*<DOFORCEJOB[^>]*?";
    private static final String REGEX_CFOK_PATTERN = "^\\s*<OUTCOND[^>]*?NAME=\"[^\"]*CF@OK[^\"]*\"[^>]*?/>\\s*[\\r\\n]*";

    public String transformarDatioToAda(String xmlDatio, MallaRequestDto mallaData) throws HandledException {
        try {
            String xmlModificado = xmlDatio;

            xmlModificado = replaceDatioInApplication(xmlModificado);

            xmlModificado = replaceCmdlineValueWhenSentryJob(xmlModificado);

            xmlModificado = addDotCloudInTransfer(xmlModificado);

            xmlModificado = replaceCtmfwWithDefaultBbvaCountry(xmlModificado);

            xmlModificado = replaceNodeidAndRunasForFilewatcher(xmlModificado);

            xmlModificado = addWildcardToFilewatcherPath(xmlModificado);

            xmlModificado = replaceArtifactoryHost(xmlModificado);

            xmlModificado = removeCopyHdfsJobs(xmlModificado);

            xmlModificado = removeJobByName(xmlModificado, mallaData.getErase2Jobname());

            xmlModificado = updateErase1DependenciesForL1T(xmlModificado, mallaData);

            if (mallaData.getHmmL1tJobname() != null) {
                xmlModificado = cleanupHmmL1tJobReferences(xmlModificado, mallaData);
            }

            xmlModificado = updateTransferJobOutconds(xmlModificado, mallaData);

            xmlModificado = updateFilewatcherInconds(xmlModificado, mallaData);

            xmlModificado = cleanupHmmMasterJob(xmlModificado, mallaData);

            xmlModificado = updateFilewatcherCmdlinePath(xmlModificado, mallaData);

            xmlModificado = addAdaEmailToAllJobs(xmlModificado);

            return xmlModificado;

        } catch (Exception e) {
            throw new HandledException("MALLA_TRANSFORM_ERROR",
                    "Error transformando XML de DATIO a ADA: " + e.getMessage(), e);
        }
    }
    private String addAdaEmailToAllJobs(String xmlContent) {
        Pattern pattern = Pattern.compile("(<DOMAIL[^>]*DEST=\")([^\"]*)(\"[^>]*>)");
        Matcher matcher = pattern.matcher(xmlContent);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String prefix = matcher.group(1);
            String currentEmail = matcher.group(2);
            String suffix = matcher.group(3);

            if (!currentEmail.contains("ada_dhm_pe.group@bbva.com")) {
                String newEmail = currentEmail + ";ada_dhm_pe.group@bbva.com";
                matcher.appendReplacement(result, Matcher.quoteReplacement(prefix + newEmail + suffix));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }
    private String replaceDatioInApplication(String xmlContent) {
        Pattern pattern = Pattern.compile("APPLICATION=\"([^\"]*)-DATIO\"");
        Matcher matcher = pattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String prefix = matcher.group(1);
            matcher.appendReplacement(result, "APPLICATION=\"" + prefix + "-ADA\"");
        }
        matcher.appendTail(result);
        return result.toString();
    }
    private String replaceCmdlineValueWhenSentryJob(String xmlContent) {
        Pattern jobPattern = Pattern.compile("<JOB.*?>", Pattern.DOTALL);
        Matcher matcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String jobTag = matcher.group();
            if (jobTag.contains("RUN_AS=\"sentry\"")) {
                String modifiedTag = jobTag.replace(
                        "CMDLINE=\"/opt/datio/sentry-pe/dataproc_sentry.py",
                        "CMDLINE=\"/opt/datio/sentry-pe-aws/dataproc_sentry.py"
                );
                matcher.appendReplacement(result, Matcher.quoteReplacement(modifiedTag));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(jobTag));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
    private String addDotCloudInTransfer(String xmlContent) {
        return xmlContent.replace(
                "CMDLINE=\"datax-agent --transferId %%PARM1",
                "CMDLINE=\"datax-agent --transferId %%PARM1._cloud"
        );
    }
    private String replaceCtmfwWithDefaultBbvaCountry(String xmlContent) {
        return xmlContent.replace(
                "CMDLINE=\"ctmfw",
                "CMDLINE=\"DEFAULT_BBVA_COUNTRY=pe;/opt/datio/filewatcher-s3/filewatcher.sh"
        );
    }

    private String replaceNodeidAndRunasForFilewatcher(String xmlContent) {
        Pattern jobPattern = Pattern.compile("<JOB.*?>", Pattern.DOTALL);
        Matcher matcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String jobTag = matcher.group();

            if (jobTag.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
                jobTag = jobTag.replaceAll("NODEID=\"[^\"]*\"", "NODEID=\"PE-SENTRY-00\"");
                jobTag = jobTag.replaceAll("RUN_AS=\"[^\"]*\"", "RUN_AS=\"sentry\"");
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(jobTag));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String addWildcardToFilewatcherPath(String xmlContent) {
        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
        Matcher jobMatcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (jobMatcher.find()) {
            String jobBlock = jobMatcher.group();

            if (jobBlock.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
                Pattern cmdlinePattern = Pattern.compile("(CMDLINE=\"[^\"]*?)(\\.csv|\\.dat)([^\"]*\")");
                Matcher cmdlineMatcher = cmdlinePattern.matcher(jobBlock);

                StringBuilder jobResult = new StringBuilder();
                while (cmdlineMatcher.find()) {
                    String prefix = cmdlineMatcher.group(1);
                    String extension = cmdlineMatcher.group(2);
                    String suffix = cmdlineMatcher.group(3);

                    String replacement = prefix + extension + "/*.csv" + suffix;
                    cmdlineMatcher.appendReplacement(jobResult, Matcher.quoteReplacement(replacement));
                }
                cmdlineMatcher.appendTail(jobResult);

                jobMatcher.appendReplacement(result, Matcher.quoteReplacement(jobResult.toString()));
            } else {
                jobMatcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
            }
        }
        jobMatcher.appendTail(result);
        return result.toString();
    }

    private String replaceArtifactoryHost(String xmlContent) {
        return xmlContent.replace(
                "artifactory-gdt.central-02.nextgen.igrupobbva",
                "artifactory-gdt.central-04.nextgen.igrupobbva"
        );
    }

    private String removeCopyHdfsJobs(String xmlContent) {
        Pattern pattern = Pattern.compile("<JOB[^>]*?DESCRIPTION=\"COPY \\(HDFS\\)[^>]*?>.*?</JOB>\\s*",
                Pattern.DOTALL);
        return pattern.matcher(xmlContent).replaceAll("");
    }

    private String removeJobByName(String xmlContent, String jobnamePorEliminar) {
        if (jobnamePorEliminar == null || jobnamePorEliminar.trim().isEmpty()) {
            return xmlContent;
        }
        String escapedJobname = escapeRegexCharacters(jobnamePorEliminar);

        Pattern pattern = Pattern.compile("<JOB[^>]*?JOBNAME=\"" + escapedJobname + "\"[^>]*?>.*?</JOB>\\s*",
                Pattern.DOTALL);
        return pattern.matcher(xmlContent).replaceAll("");
    }

    private String updateTransferJobOutconds(String xmlContent, MallaRequestDto datos) {
        if (datos.getTransferJobname() == null || datos.getCopyJobname() == null || datos.getFwJobname() == null) {
            return xmlContent;
        }
        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
        Matcher matcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String jobBlock = matcher.group();

            if (jobBlock.contains("JOBNAME=\"" + datos.getTransferJobname() + "\"")) {
                jobBlock = jobBlock.replace(datos.getCopyJobname(), datos.getFwJobname());
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String updateFilewatcherInconds(String xmlContent, MallaRequestDto datos) {
        if (datos.getFwJobname() == null || datos.getCopyJobname() == null || datos.getTransferJobname() == null) {
            return xmlContent;
        }
        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
        Matcher matcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String jobBlock = matcher.group();

            if (jobBlock.contains("JOBNAME=\"" + datos.getFwJobname() + "\"")) {
                jobBlock = jobBlock.replace(datos.getCopyJobname(), datos.getTransferJobname());
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
        }
        matcher.appendTail(result);
        return result.toString();
    }
    private String cleanupHmmMasterJob(String xmlContent, MallaRequestDto datos) {
        if (datos.getHmmMasterJobname() == null) {
            return xmlContent;
        }

        String escapedJobname = escapeRegexCharacters(datos.getHmmMasterJobname());
        Pattern jobPattern = Pattern.compile("(<JOB[^>]*?JOBNAME=\"" + escapedJobname + "\"[^>]*?>.*?</JOB>)",
                Pattern.DOTALL);

        Matcher jobMatcher = jobPattern.matcher(xmlContent);
        StringBuilder result = new StringBuilder();

        while (jobMatcher.find()) {
            String jobBlock = jobMatcher.group(1);

            if (datos.getHmmL1tJobname() != null) {
                if (datos.getErase1Jobname() != null) {
                    String escapedErase1 = escapeRegexCharacters(datos.getErase1Jobname());
                    Pattern erase1Pattern = Pattern.compile(REGEX_PREFIX_OUTCOND_DOFORCEJOB + escapedErase1 +
                            REGEX_SUFFIX_CLOSE_TAG, Pattern.MULTILINE);
                    jobBlock = erase1Pattern.matcher(jobBlock).replaceAll("");
                }
                if (datos.getErase2Jobname() != null) {
                    String escapedErase2 = escapeRegexCharacters(datos.getErase2Jobname());
                    Pattern erase2Pattern = Pattern.compile(REGEX_PREFIX_OUTCOND_DOFORCEJOB + escapedErase2 +
                            REGEX_SUFFIX_CLOSE_TAG, Pattern.MULTILINE);
                    jobBlock = erase2Pattern.matcher(jobBlock).replaceAll("");
                }
            } else {
                if (datos.getErase2Jobname() != null) {
                    String escapedErase2 = escapeRegexCharacters(datos.getErase2Jobname());
                    Pattern erasePattern = Pattern.compile(REGEX_PREFIX_OUTCOND_DOFORCEJOB + escapedErase2 +
                            REGEX_SUFFIX_CLOSE_TAG, Pattern.MULTILINE);
                    jobBlock = erasePattern.matcher(jobBlock).replaceAll("");
                }
            }

            Pattern cfokPattern = Pattern.compile(REGEX_CFOK_PATTERN,
                    Pattern.MULTILINE);
            jobBlock = cfokPattern.matcher(jobBlock).replaceAll("");

            jobMatcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
        }
        jobMatcher.appendTail(result);
        return result.toString();
    }

    private String cleanupHmmL1tJobReferences(String xmlContent, MallaRequestDto datos) {
        if (datos.getHmmL1tJobname() == null || datos.getErase2Jobname() == null) {
            return xmlContent;
        }

        String escapedL1TJobname = escapeRegexCharacters(datos.getHmmL1tJobname());
        Pattern jobPattern = Pattern.compile("(<JOB[^>]*?JOBNAME=\"" + escapedL1TJobname + "\"[^>]*?>.*?</JOB>)",
                Pattern.DOTALL);

        Matcher jobMatcher = jobPattern.matcher(xmlContent);
        StringBuilder result = new StringBuilder();

        while (jobMatcher.find()) {
            String jobBlock = jobMatcher.group(1);

            String escapedErase2 = escapeRegexCharacters(datos.getErase2Jobname());
            Pattern outcondPattern = Pattern.compile(REGEX_PREFIX_OUTCOND + escapedErase2 + REGEX_SUFFIX_CLOSE_TAG,
                    Pattern.MULTILINE);
            jobBlock = outcondPattern.matcher(jobBlock).replaceAll("");

            Pattern doforcePattern = Pattern.compile(REGEX_PREFIX_DOFORCEJOB  + escapedErase2 + REGEX_SUFFIX_CLOSE_TAG,
                    Pattern.MULTILINE);
            jobBlock = doforcePattern.matcher(jobBlock).replaceAll("");

            jobMatcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
        }
        jobMatcher.appendTail(result);
        return result.toString();
    }

    private String updateFilewatcherCmdlinePath(String xmlContent, MallaRequestDto datos) {
        if (datos.getCopyUuaaRaw() == null || datos.getTransferUuaaRaw() == null) {
            return xmlContent;
        }

        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
        Matcher matcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String jobBlock = matcher.group();

            if (jobBlock.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
                String pathToFind = "external/" + datos.getCopyUuaaRaw();
                String pathToReplace = "datax/" + datos.getTransferUuaaRaw();
                jobBlock = jobBlock.replace(pathToFind, pathToReplace);
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String escapeRegexCharacters(String input) {
        if (input == null) return null;

        return input.replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("?", "\\?")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("|", "\\|");
    }
    private String updateErase1DependenciesForL1T(String xmlContent, MallaRequestDto datos) {
        if (datos.getHmmL1tJobname() == null || datos.getKrbL1tJobname() == null) {
            return xmlContent;
        }

        if (datos.getErase1Jobname() == null || datos.getHmmMasterJobname() == null) {
            return xmlContent;
        }

        String xmlModificado = xmlContent;

        String oldErase1Pattern = datos.getHmmMasterJobname() + "-TO-" + datos.getErase1Jobname();
        String newErase1Pattern = datos.getHmmL1tJobname() + "-TO-" + datos.getErase1Jobname();
        xmlModificado = xmlModificado.replace(oldErase1Pattern, newErase1Pattern);

        return xmlModificado;
    }
}
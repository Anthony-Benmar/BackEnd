//package com.bbva.service.metaknight;
//
//import com.bbva.core.HandledException;
//import com.bbva.dto.metaknight.request.MallaRequestDto;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class MallaTransformerService {
//
//    /**
//     * Transforma el XML de DATIO a ADA aplicando todas las modificaciones necesarias
//     */
//    public String transformarDatioToAda(String xmlDatio, MallaRequestDto mallaData) throws HandledException {
//        try {
//            String xmlModificado = xmlDatio;
//
//            // 1. Reemplazar DATIO por ADA en APPLICATION
//            xmlModificado = replaceDatioInApplication(xmlModificado);
//
//            // 2. Modificar CMDLINE en jobs de sentry
//            xmlModificado = replaceCmdlineValueWhenSentryJob(xmlModificado);
//
//            // 3. Agregar ._cloud al transferId
//            xmlModificado = addDotCloudInTransfer(xmlModificado);
//
//            // 4. Reemplazar ctmfw con filewatcher.sh
//            xmlModificado = replaceCtmfwWithDefaultBbvaCountry(xmlModificado);
//
//            // 5. Cambiar NODEID y RUN_AS del FileWatcher
//            xmlModificado = replaceNodeidAndRunasForFilewatcher(xmlModificado);
//
//            // 6. Agregar wildcard al path del filewatcher
//            xmlModificado = addWildcardToFilewatcherPath(xmlModificado);
//
//            // 7. Cambiar host de artifactory
//            xmlModificado = replaceArtifactoryHost(xmlModificado);
//
//            // 8. Eliminar jobs de COPY (HDFS)
//            xmlModificado = removeCopyHdfsJobs(xmlModificado);
//
//            // 9. Eliminar job erase2
//            xmlModificado = removeJobByName(xmlModificado, mallaData.getErase2Jobname());
//
//            // 10. Actualizar condiciones de transferencia
//            xmlModificado = updateTransferJobOutconds(xmlModificado, mallaData);
//
//            // 11. Actualizar condiciones de filewatcher
//            xmlModificado = updateFilewatcherInconds(xmlModificado, mallaData);
//
//            // 12. Limpiar job hmm_master
//            xmlModificado = cleanupHmmMasterJob(xmlModificado, mallaData);
//
//            // 13. Actualizar path del filewatcher
//            xmlModificado = updateFilewatcherCmdlinePath(xmlModificado, mallaData);
//
//            return xmlModificado;
//
//        } catch (Exception e) {
//            throw new HandledException("MALLA_TRANSFORM_ERROR",
//                    "Error transformando XML de DATIO a ADA: " + e.getMessage(), e);
//        }
//    }
//
//    private String replaceDatioInApplication(String xmlContent) {
//        Pattern pattern = Pattern.compile("APPLICATION=\"([^\"]*)-DATIO\"");
//        return pattern.matcher(xmlContent).replaceAll("APPLICATION=\"$1-ADA\"");
//    }
//
//    private String replaceCmdlineValueWhenSentryJob(String xmlContent) {
//        Pattern jobPattern = Pattern.compile("<JOB.*?>", Pattern.DOTALL);
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobTag = match.group();
//            if (jobTag.contains("RUN_AS=\"sentry\"")) {
//                return jobTag.replace(
//                        "CMDLINE=\"/opt/datio/sentry-pe/dataproc_sentry.py",
//                        "CMDLINE=\"/opt/datio/sentry-pe-aws/dataproc_sentry.py"
//                );
//            }
//            return jobTag;
//        });
//    }
//
//    private String addDotCloudInTransfer(String xmlContent) {
//        return xmlContent.replace(
//                "CMDLINE=\"datax-agent --transferId %%PARM1",
//                "CMDLINE=\"datax-agent --transferId %%PARM1._cloud"
//        );
//    }
//
//    private String replaceCtmfwWithDefaultBbvaCountry(String xmlContent) {
//        return xmlContent.replace(
//                "CMDLINE=\"ctmfw",
//                "CMDLINE=\"DEFAULT_BBVA_COUNTRY=pe;/opt/datio/filewatcher-s3/filewatcher.sh"
//        );
//    }
//
//    private String replaceNodeidAndRunasForFilewatcher(String xmlContent) {
//        Pattern jobPattern = Pattern.compile("<JOB.*?>", Pattern.DOTALL);
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobTag = match.group();
//            if (jobTag.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
//                jobTag = jobTag.replaceAll("NODEID=\"[^\"]*\"", "NODEID=\"PE-SENTRY-00\"");
//                jobTag = jobTag.replaceAll("RUN_AS=\"[^\"]*\"", "RUN_AS=\"sentry\"");
//            }
//            return jobTag;
//        });
//    }
//
//    private String addWildcardToFilewatcherPath(String xmlContent) {
//        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobBlock = match.group();
//            if (jobBlock.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
//                Pattern cmdlinePattern = Pattern.compile("(CMDLINE=\"[^\"]*?)(\\.csv|\\.dat)([^\"]*\")");
//                return cmdlinePattern.matcher(jobBlock).replaceAll("$1$2/*.csv$3");
//            }
//            return jobBlock;
//        });
//    }
//
//    private String replaceArtifactoryHost(String xmlContent) {
//        return xmlContent.replace(
//                "artifactory-gdt.central-02.nextgen.igrupobbva",
//                "artifactory-gdt.central-04.nextgen.igrupobbva"
//        );
//    }
//
//    private String removeCopyHdfsJobs(String xmlContent) {
//        Pattern pattern = Pattern.compile("<JOB[^>]*?DESCRIPTION=\"COPY \\(HDFS\\)[^>]*?>.*?</JOB>\\s*",
//                Pattern.DOTALL);
//        return pattern.matcher(xmlContent).replaceAll("");
//    }
//
//    private String removeJobByName(String xmlContent, String jobnamePorEliminar) {
//        String safeJobname = Pattern.quote(jobnamePorEliminar);
//        Pattern pattern = Pattern.compile("<JOB[^>]*?JOBNAME=\"" + safeJobname + "\"[^>]*?>.*?</JOB>\\s*",
//                Pattern.DOTALL);
//        return pattern.matcher(xmlContent).replaceAll("");
//    }
//
//    private String updateTransferJobOutconds(String xmlContent, MallaRequestDto datos) {
//        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobBlock = match.group();
//            if (jobBlock.contains("JOBNAME=\"" + datos.getTransferJobname() + "\"")) {
//                return jobBlock.replace(datos.getCopyJobname(), datos.getFwJobname());
//            }
//            return jobBlock;
//        });
//    }
//
//    private String updateFilewatcherInconds(String xmlContent, MallaRequestDto datos) {
//        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobBlock = match.group();
//            if (jobBlock.contains("JOBNAME=\"" + datos.getFwJobname() + "\"")) {
//                return jobBlock.replace(datos.getCopyJobname(), datos.getTransferJobname());
//            }
//            return jobBlock;
//        });
//    }
//
//    private String cleanupHmmMasterJob(String xmlContent, MallaRequestDto datos) {
//        String safeJobname = Pattern.quote(datos.getHmmMasterJobname());
//        Pattern jobPattern = Pattern.compile("(<JOB[^>]*?JOBNAME=\"" + safeJobname + "\"[^>]*?>.*?</JOB>)",
//                Pattern.DOTALL);
//
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobBlock = match.group(1);
//
//            // Eliminar referencias a erase2_jobname
//            String safeErase2 = Pattern.quote(datos.getErase2Jobname());
//            Pattern erasePattern = Pattern.compile("^\\s*<(?:OUTCOND|DOFORCEJOB)[^>]*?" + safeErase2 +
//                    "[^>]*?/>\\s*[\\r\\n]*", Pattern.MULTILINE);
//            jobBlock = erasePattern.matcher(jobBlock).replaceAll("");
//
//            // Eliminar OUTCOND con CF@OK
//            Pattern cfokPattern = Pattern.compile("^\\s*<OUTCOND[^>]*?NAME=\"[^\"]*CF@OK[^\"]*\"[^>]*?/>\\s*[\\r\\n]*",
//                    Pattern.MULTILINE);
//            jobBlock = cfokPattern.matcher(jobBlock).replaceAll("");
//
//            return jobBlock;
//        });
//    }
//
//    private String updateFilewatcherCmdlinePath(String xmlContent, MallaRequestDto datos) {
//        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
//        return jobPattern.matcher(xmlContent).replaceAll(match -> {
//            String jobBlock = match.group();
//            if (jobBlock.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
//                String pathToFind = "external/" + datos.getCopyUuaaRaw();
//                String pathToReplace = "datax/" + datos.getTransferUuaaRaw();
//                return jobBlock.replace(pathToFind, pathToReplace);
//            }
//            return jobBlock;
//        });
//    }
//}
package com.bbva.service.metaknight;

import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.MallaRequestDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class MallaTransformerService {

    private static final Logger LOGGER = Logger.getLogger(MallaTransformerService.class.getName());

    //XML DATIO A ADA -- No tocar logs
    public String transformarDatioToAda(String xmlDatio, MallaRequestDto mallaData) throws HandledException {
        try {
            String xmlModificado = xmlDatio;

            LOGGER.info("Iniciando transformación DATIO -> ADA");

            // 1. Reemplazar DATIO por ADA en APPLICATION
            xmlModificado = replaceDatioInApplication(xmlModificado);
            LOGGER.fine("✅ Paso 1 completado: APPLICATION DATIO->ADA");

            // 2. Modificar CMDLINE en jobs de sentry
            xmlModificado = replaceCmdlineValueWhenSentryJob(xmlModificado);
            LOGGER.fine("✅ Paso 2 completado: CMDLINE sentry jobs");

            // 3. Agregar ._cloud al transferId
            xmlModificado = addDotCloudInTransfer(xmlModificado);
            LOGGER.fine("✅ Paso 3 completado: ._cloud agregado");

            // 4. Reemplazar ctmfw con filewatcher.sh
            xmlModificado = replaceCtmfwWithDefaultBbvaCountry(xmlModificado);
            LOGGER.fine("✅ Paso 4 completado: ctmfw -> filewatcher.sh");

            // 5. Cambiar NODEID y RUN_AS del FileWatcher
            xmlModificado = replaceNodeidAndRunasForFilewatcher(xmlModificado);
            LOGGER.fine("✅ Paso 5 completado: NODEID y RUN_AS FileWatcher");

            // 6. Agregar wildcard al path del filewatcher
            xmlModificado = addWildcardToFilewatcherPath(xmlModificado);
            LOGGER.fine("✅ Paso 6 completado: wildcard path agregado");

            // 7. Cambiar host de artifactory
            xmlModificado = replaceArtifactoryHost(xmlModificado);
            LOGGER.fine("✅ Paso 7 completado: artifactory host cambiado");

            // 8. Eliminar jobs de COPY (HDFS)
            xmlModificado = removeCopyHdfsJobs(xmlModificado);
            LOGGER.fine("✅ Paso 8 completado: COPY HDFS jobs eliminados");

            // 9. Eliminar job erase2
            xmlModificado = removeJobByName(xmlModificado, mallaData.getErase2Jobname());
            LOGGER.fine("✅ Paso 9 completado: erase2 job eliminado");

            // 10. Actualizar condiciones de transferencia
            xmlModificado = updateTransferJobOutconds(xmlModificado, mallaData);
            LOGGER.fine("✅ Paso 10 completado: condiciones transferencia actualizadas");

            // 11. Actualizar condiciones de filewatcher
            xmlModificado = updateFilewatcherInconds(xmlModificado, mallaData);
            LOGGER.fine("✅ Paso 11 completado: condiciones filewatcher actualizadas");

            // 12. Limpiar job hmm_master
            xmlModificado = cleanupHmmMasterJob(xmlModificado, mallaData);
            LOGGER.fine("✅ Paso 12 completado: hmm_master job limpiado");

            // 13. Actualizar path del filewatcher
            xmlModificado = updateFilewatcherCmdlinePath(xmlModificado, mallaData);
            LOGGER.fine("✅ Paso 13 completado: path filewatcher actualizado");

            LOGGER.info("Transformación DATIO -> ADA completada exitosamente");
            return xmlModificado;

        } catch (Exception e) {
            LOGGER.severe("❌ Error en transformación: " + e.getMessage());
            throw new HandledException("MALLA_TRANSFORM_ERROR",
                    "Error transformando XML de DATIO a ADA: " + e.getMessage(), e);
        }
    }
    // replace_datio_in_application()
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

    //replace_cmdlinevalue_when_sentry_job()
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

    // add_dot_cloud_in_transfer()
    private String addDotCloudInTransfer(String xmlContent) {
        return xmlContent.replace(
                "CMDLINE=\"datax-agent --transferId %%PARM1",
                "CMDLINE=\"datax-agent --transferId %%PARM1._cloud"
        );
    }

    // replace_ctmfw_with_default_bbva_country()
    private String replaceCtmfwWithDefaultBbvaCountry(String xmlContent) {
        return xmlContent.replace(
                "CMDLINE=\"ctmfw",
                "CMDLINE=\"DEFAULT_BBVA_COUNTRY=pe;/opt/datio/filewatcher-s3/filewatcher.sh"
        );
    }

    //replace_nodeid_and_runas_for_filewatcher()
    private String replaceNodeidAndRunasForFilewatcher(String xmlContent) {
        Pattern jobPattern = Pattern.compile("<JOB.*?>", Pattern.DOTALL);
        Matcher matcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String jobTag = matcher.group();

            if (jobTag.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
                // Reemplazar NODEID
                jobTag = jobTag.replaceAll("NODEID=\"[^\"]*\"", "NODEID=\"PE-SENTRY-00\"");
                // Reemplazar RUN_AS
                jobTag = jobTag.replaceAll("RUN_AS=\"[^\"]*\"", "RUN_AS=\"sentry\"");
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(jobTag));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    //add_wildcard_to_filewatcher_path()
    private String addWildcardToFilewatcherPath(String xmlContent) {
        Pattern jobPattern = Pattern.compile("<JOB.*?</JOB>", Pattern.DOTALL);
        Matcher jobMatcher = jobPattern.matcher(xmlContent);

        StringBuilder result = new StringBuilder();
        while (jobMatcher.find()) {
            String jobBlock = jobMatcher.group();

            if (jobBlock.contains("SUB_APPLICATION=\"CTD-FWATCHER-CCR\"")) {
                // Aplicar el patrón de wildcard dentro del job del filewatcher
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

    // replace_artifactory_host()
    private String replaceArtifactoryHost(String xmlContent) {
        return xmlContent.replace(
                "artifactory-gdt.central-02.nextgen.igrupobbva",
                "artifactory-gdt.central-04.nextgen.igrupobbva"
        );
    }

    // remove_copy_hdfs_jobs()
    private String removeCopyHdfsJobs(String xmlContent) {
        Pattern pattern = Pattern.compile("<JOB[^>]*?DESCRIPTION=\"COPY \\(HDFS\\)[^>]*?>.*?</JOB>\\s*",
                Pattern.DOTALL);
        return pattern.matcher(xmlContent).replaceAll("");
    }

    // remove_job_by_name()
    private String removeJobByName(String xmlContent, String jobnamePorEliminar) {
        if (jobnamePorEliminar == null || jobnamePorEliminar.trim().isEmpty()) {
            return xmlContent;
        }

        // Escape manual de caracteres especiales para regex
        String escapedJobname = escapeRegexCharacters(jobnamePorEliminar);

        Pattern pattern = Pattern.compile("<JOB[^>]*?JOBNAME=\"" + escapedJobname + "\"[^>]*?>.*?</JOB>\\s*",
                Pattern.DOTALL);
        return pattern.matcher(xmlContent).replaceAll("");
    }

    // update_transfer_job_outconds()
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

    //update_filewatcher_inconds()
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

    //cleanup_hmm_master_job()
    private String cleanupHmmMasterJob(String xmlContent, MallaRequestDto datos) {
        if (datos.getHmmMasterJobname() == null || datos.getErase2Jobname() == null) {
            return xmlContent;
        }

        String escapedJobname = escapeRegexCharacters(datos.getHmmMasterJobname());
        Pattern jobPattern = Pattern.compile("(<JOB[^>]*?JOBNAME=\"" + escapedJobname + "\"[^>]*?>.*?</JOB>)",
                Pattern.DOTALL);

        Matcher jobMatcher = jobPattern.matcher(xmlContent);
        StringBuilder result = new StringBuilder();

        while (jobMatcher.find()) {
            String jobBlock = jobMatcher.group(1);

            // Eliminar referencias a erase2_jobname
            String escapedErase2 = escapeRegexCharacters(datos.getErase2Jobname());
            Pattern erasePattern = Pattern.compile("^\\s*<(?:OUTCOND|DOFORCEJOB)[^>]*?" + escapedErase2 +
                    "[^>]*?/>\\s*[\\r\\n]*", Pattern.MULTILINE);
            jobBlock = erasePattern.matcher(jobBlock).replaceAll("");

            // Eliminar OUTCOND con CF@OK
            Pattern cfokPattern = Pattern.compile("^\\s*<OUTCOND[^>]*?NAME=\"[^\"]*CF@OK[^\"]*\"[^>]*?/>\\s*[\\r\\n]*",
                    Pattern.MULTILINE);
            jobBlock = cfokPattern.matcher(jobBlock).replaceAll("");

            jobMatcher.appendReplacement(result, Matcher.quoteReplacement(jobBlock));
        }
        jobMatcher.appendTail(result);
        return result.toString();
    }

    // update_filewatcher_cmdline_path()
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

    // Validar luego ####
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
}
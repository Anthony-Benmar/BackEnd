package com.bbva.service.metaknight;

import com.bbva.core.HandledException;
import com.bbva.core.exception.MallaGenerationException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import com.bbva.util.metaknight.SchemaProcessor;
import com.bbva.util.metaknight.XmlMallaGenerator;
import com.bbva.util.metaknight.MallaConstants;
import com.bbva.util.metaknight.validation.MallaValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MallaGeneratorService {

    private static final Logger LOGGER = Logger.getLogger(MallaGeneratorService.class.getName());

    private final XmlMallaGenerator xmlGenerator = new XmlMallaGenerator();
    private final MallaTransformerService transformerService = new MallaTransformerService();
    private final MallaValidator mallaValidator = new MallaValidator();
    private final OptimizedGitRepositoryService gitRepositoryService = new OptimizedGitRepositoryService();

    public Map<String, String> generarMallasXml(IngestaRequestDto request, SchemaProcessor schemaProcessor)
            throws HandledException {

        LOGGER.info("Iniciando generación de mallas XML con datos reales para UUAA: " + request.getUuaaMaster());

        try {
            // Validar datos de entrada
            mallaValidator.validarDatosIngesta(request);

            // Construir datos para la malla usando datos reales del repositorio
            MallaRequestDto mallaData = construirDatosMallaConDatosReales(request, schemaProcessor);

            // Validar datos de malla construidos
            mallaValidator.validarDatosMalla(mallaData);

            LOGGER.info("Generando XML base DATIO para UUAA: " + mallaData.getUuaa());

            // Generar XML base (DATIO)
            String xmlDatio = xmlGenerator.generarFlujoCompletoXml(mallaData);
            mallaValidator.validarXmlGenerado(xmlDatio, "DATIO");

            LOGGER.info("Transformando XML a ADA para UUAA: " + mallaData.getUuaa());

            // Generar XML transformado (ADA)
            String xmlAda = transformerService.transformarDatioToAda(xmlDatio, mallaData);
            mallaValidator.validarXmlGenerado(xmlAda, "ADA");

            // Preparar resultado
            Map<String, String> archivosXml = new HashMap<>();
            String baseFileName = "malla_diaria_" + mallaData.getUuaaLowercase();

            archivosXml.put(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.DATIO_SUFFIX, xmlDatio);
            archivosXml.put(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.ADA_SUFFIX, xmlAda);

            LOGGER.info("Mallas XML generadas exitosamente para UUAA: " + mallaData.getUuaa());

            return archivosXml;

        } catch (MallaGenerationException e) {
            LOGGER.log(Level.SEVERE, "Error específico generando mallas XML: " + e.getFullMessage(), e);
            throw new HandledException("MALLA_GENERATION_ERROR", e.getFullMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado generando mallas XML: " + e.getMessage(), e);
            throw new HandledException("MALLA_GENERATION_ERROR",
                    "Error inesperado generando archivos XML de malla: " + e.getMessage(), e);
        }finally {
            try {
                gitRepositoryService.cleanupCache();
                LOGGER.info("✅ Archivos temporales limpiados para UUAA: " + request.getUuaaMaster());
            } catch (Exception cleanupError) {
                LOGGER.log(Level.WARNING, "⚠️ Error limpiando archivos temporales: " + cleanupError.getMessage(), cleanupError);
            }
        }
    }

    private MallaRequestDto construirDatosMallaConDatosReales(IngestaRequestDto request, SchemaProcessor schemaProcessor)
            throws MallaGenerationException {

        LOGGER.info("Construyendo datos de malla con datos reales del repositorio para UUAA: " + request.getUuaaMaster());

        try {
            MallaRequestDto mallaData = new MallaRequestDto();

            // Datos temporales basados en fecha/hora actual
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmmss");

            mallaData.setCreationDate(now.format(dateFormat));
            mallaData.setCreationTime(now.format(timeFormat));

            // Datos básicos del proyecto
            mallaData.setCreationUser(request.getRegistroDev()); // XP del desarrollador
            mallaData.setUuaa(request.getUuaaMaster().toUpperCase());
            mallaData.setUuaaLowercase(request.getUuaaMaster().toLowerCase());

            // Usar ControlMAnalyzer para obtener datos reales del repositorio
            generarDatosConControlMAnalyzer(mallaData, request, schemaProcessor);

            LOGGER.info("Datos de malla construidos exitosamente con datos reales para UUAA: " + mallaData.getUuaa());

            return mallaData;

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error construyendo datos de malla con datos reales: " + e.getMessage());
        }
    }

    private void generarDatosConControlMAnalyzer(MallaRequestDto mallaData, IngestaRequestDto request,
                                                 SchemaProcessor schemaProcessor) throws MallaGenerationException {

        try {
            String uuaa = mallaData.getUuaa();
            String uuaaLower = mallaData.getUuaaLowercase();

            // 1. ANALIZAR REPOSITORIO CONTROL-M - REVISAR
            LOGGER.info("Conectando a repositorio Control-M para UUAA: " + uuaa);
            ControlMAnalyzer controlM = new ControlMAnalyzer(uuaa, request.getFrecuencia(), gitRepositoryService, request.isTieneL1T());

            // 2. DATOS REALES EXTRAÍDOS DEL REPOSITORIO
            // VALIDAR DESPUÉS #
            String realNamespace = controlM.getNamespace();
            String realParentFolder = controlM.getParentFolder();

            // 3. JOBNAMES REALES CALCULADOS AUTOMÁTICAMENTE
            String realTransferJobname = controlM.getTransfer();
            String realCopyJobname = controlM.getCopy();
            String realFwJobname = controlM.getFw();
            String realHmmStgJobname = controlM.getHs();
            String realKrbRawJobname = controlM.getKbr();
            String realHmmRawJobname = controlM.getHr();
            String realKrbMasterJobname = controlM.getKbm();
            String realHmmMasterJobname = controlM.getHm();
            String realErase1Jobname = controlM.getD1();
            String realErase2Jobname = controlM.getD2();

            //JobNames L1T calculados
//            String realKrbL1tJobname = controlM.getKrbL1t();
//            String realHmmL1tJobname = controlM.getHmmL1t();

            //L1T
            if (request.isTieneL1T()) {
                // 8. JOBNAMES L1T REALES CALCULADOS
                String realKrbL1tJobname = controlM.getKrbL1t();
                String realHmmL1tJobname = controlM.getHmmL1t();

                mallaData.setKrbL1tJobname(realKrbL1tJobname);
                mallaData.setHmmL1tJobname(realHmmL1tJobname);

                // L1T source name
                mallaData.setL1tSourceName(schemaProcessor.getDfMasterName().toLowerCase() + "_l1t");

                LOGGER.info("Jobs L1T configurados: " + realKrbL1tJobname + ", " + realHmmL1tJobname);
            } else {
                // SIN L1T: dejar campos null para mantener flujo original
                mallaData.setKrbL1tJobname(null);
                mallaData.setHmmL1tJobname(null);
                mallaData.setL1tSourceName(null);
                LOGGER.info("L1T no requerido, manteniendo flujo original");
            }

            // 4. CONFIGURAR DATOS REALES EN MALLA
            mallaData.setNamespace(realNamespace);
            mallaData.setParentFolder(realParentFolder);

            mallaData.setTeamEmail(request.getTeamEmail());

            // 5. JOBNAMES REALES CALCULADOS SECUENCIALMENTE
            mallaData.setTransferJobname(realTransferJobname);
            mallaData.setCopyJobname(realCopyJobname);
            mallaData.setFwJobname(realFwJobname);
            mallaData.setHmmStgJobname(realHmmStgJobname);
            mallaData.setKrbRawJobname(realKrbRawJobname);
            mallaData.setHmmRawJobname(realHmmRawJobname);
            mallaData.setKrbMasterJobname(realKrbMasterJobname);
            mallaData.setHmmMasterJobname(realHmmMasterJobname);
            mallaData.setErase1Jobname(realErase1Jobname);
            mallaData.setErase2Jobname(realErase2Jobname);

            //JOBS L1T
//            mallaData.setKrbL1tJobname(realKrbL1tJobname);
//            mallaData.setHmmL1tJobname(realHmmL1tJobname);


            // 6. DATOS DEL SCHEMA (si está disponible) o valores por defecto
            if (schemaProcessor != null) {
                String tag = schemaProcessor.getTag();
                String dfMasterName = schemaProcessor.getDfMasterName();
                String dfRawName = schemaProcessor.getDfRawName();

                // Usar nombres reales del schema
                mallaData.setTransferSourceName(dfRawName.toUpperCase());
                mallaData.setRawSourceName(dfRawName.toLowerCase());
                mallaData.setMasterSourceName(dfMasterName.toLowerCase());

                // Generar job IDs con tag real del schema
                generarJobIdsConTagReal(mallaData, uuaaLower, tag);

                // Transfer name basado en datos reales
//                mallaData.setTransferName(mallaUtils.generarTransferName(dfRawName));
//                mallaData.setTransferUuaaRaw(mallaUtils.extraerUuaaRawDeTransfer(mallaData.getTransferName()));

                mallaData.setTransferName(request.getTransferName());
                mallaData.setTransferUuaaRaw(schemaProcessor.getDfUuaa());
                mallaData.setTransferTimeFrom(request.getTransferTimeFrom());

                //L1T sourceName
                mallaData.setL1tSourceName(dfMasterName.toLowerCase() + "_l1t");
            }
            // 7. CONFIGURACIONES ADICIONALES
//            mallaData.setTransferTimeFrom(MallaConstants.DEFAULT_TRANSFER_TIME);
            mallaData.setCopyUuaaRaw(MallaConstants.DEFAULT_COPY_UUAA_RAW);
            mallaData.setCreateNums(MallaConstants.DEFAULT_CREATE_NUMS);

            // 8. LOG DEL RESUMEN DE ANÁLISIS
            Map<String, Object> summary = controlM.getAnalysisSummary();
            LOGGER.info("=== RESUMEN CONTROL-M ANALYZER ===");
            LOGGER.info("UUAA: " + summary.get("uuaa"));
            LOGGER.info("Namespace real: " + summary.get("namespace"));
            LOGGER.info("Parent folder real: " + summary.get("parentFolder"));
            LOGGER.info("Total jobs en repositorio: " + summary.get("totalJobsInFolder"));
            LOGGER.info("XMLs analizados: " + summary.get("xmlsAnalyzed"));
            LOGGER.info("==================================");

        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error generando datos con ControlMAnalyzer: " + e.getMessage());
        }
    }

    // Genera Job IDs con tag específico (real del schema o por defecto)

    private void generarJobIdsConTagReal(MallaRequestDto mallaData, String uuaaLower, String tag) {

        // Generar IDs usando el tag proporcionado, que sale del schema
        mallaData.setHmmStgJobid(String.format(MallaConstants.HMM_STG_ID_PATTERN, uuaaLower, tag));
        mallaData.setKrbRawJobid(String.format(MallaConstants.KRB_RAW_ID_PATTERN, uuaaLower, tag));
        mallaData.setHmmRawJobid(String.format(MallaConstants.HMM_RAW_ID_PATTERN, uuaaLower, tag));
        mallaData.setKrbMasterJobid(String.format(MallaConstants.KRB_MASTER_ID_PATTERN, uuaaLower, tag));
        mallaData.setHmmMasterJobid(String.format(MallaConstants.HMM_MASTER_ID_PATTERN, uuaaLower, tag));

        // NUEVOS: Jobs L1T
        mallaData.setKrbL1tJobid(String.format(MallaConstants.KRB_L1T_ID_PATTERN, uuaaLower, tag));
        mallaData.setHmmL1tJobid(String.format(MallaConstants.HMM_L1T_ID_PATTERN, uuaaLower, tag));

        LOGGER.info("Job IDs generados con tag: " + tag);
    }
}
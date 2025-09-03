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

        try {
            mallaValidator.validarDatosIngesta(request);

            MallaRequestDto mallaData = construirDatosMallaConDatosReales(request, schemaProcessor);

            mallaValidator.validarDatosMalla(mallaData);

            String xmlDatio = xmlGenerator.generarFlujoCompletoXml(mallaData);
            mallaValidator.validarXmlGenerado(xmlDatio, "DATIO");

            String xmlAda = transformerService.transformarDatioToAda(xmlDatio, mallaData);
            mallaValidator.validarXmlGenerado(xmlAda, "ADA");

            Map<String, String> archivosXml = new HashMap<>();
            String baseFileName = "malla_diaria_" + mallaData.getUuaaLowercase();

            archivosXml.put(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.DATIO_SUFFIX, xmlDatio);
            archivosXml.put(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.ADA_SUFFIX, xmlAda);

            return archivosXml;

        } catch (MallaGenerationException e) {
            throw new HandledException("MALLA_GENERATION_ERROR", e.getFullMessage(), e);
        } catch (Exception e) {
            throw new HandledException("MALLA_GENERATION_ERROR",
                    "Error inesperado generando archivos XML de malla: " + e.getMessage(), e);
        }finally {
            try {
                gitRepositoryService.cleanupCache();
                LOGGER.info("✅ Archivos temporales limpiados para UUAA: " + request.getUuaaMaster());
            } catch (Exception cleanupError) {
                LOGGER.log(Level.WARNING, "Error limpiando archivos temporales: " + cleanupError.getMessage(), cleanupError);
            }
        }
    }

    private MallaRequestDto construirDatosMallaConDatosReales(IngestaRequestDto request, SchemaProcessor schemaProcessor)
            throws MallaGenerationException {

        try {
            MallaRequestDto mallaData = new MallaRequestDto();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmmss");

            mallaData.setCreationDate(now.format(dateFormat));
            mallaData.setCreationTime(now.format(timeFormat));
            mallaData.setCreationUser(request.getRegistroDev());
            mallaData.setUuaa(request.getUuaaMaster().toUpperCase());
            mallaData.setUuaaLowercase(request.getUuaaMaster().toLowerCase());

            generarDatosConControlMAnalyzer(mallaData, request, schemaProcessor);

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

            ControlMAnalyzer controlM = new ControlMAnalyzer(uuaa, request.getFrecuencia(), gitRepositoryService, request.isTieneL1T());

            String realNamespace = controlM.getNamespace();
            String realParentFolder = controlM.getParentFolder();

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

            if (request.isTieneL1T()) {
                String realKrbL1tJobname = controlM.getKrbL1t();
                String realHmmL1tJobname = controlM.getHmmL1t();

                mallaData.setKrbL1tJobname(realKrbL1tJobname);
                mallaData.setHmmL1tJobname(realHmmL1tJobname);
                mallaData.setL1tSourceName(schemaProcessor.getDfMasterName().toLowerCase() + "_l1t");
            } else {
                mallaData.setKrbL1tJobname(null);
                mallaData.setHmmL1tJobname(null);
                mallaData.setL1tSourceName(null);
            }

            mallaData.setNamespace(realNamespace);
            mallaData.setParentFolder(realParentFolder);
            mallaData.setTeamEmail(request.getTeamEmail());

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

            if (schemaProcessor != null) {
                String tag = schemaProcessor.getTag();
                String dfMasterName = schemaProcessor.getDfMasterName();
                String dfRawName = schemaProcessor.getDfRawName();

                mallaData.setTransferSourceName(dfRawName.toUpperCase());
                mallaData.setRawSourceName(dfRawName.toLowerCase());
                mallaData.setMasterSourceName(dfMasterName.toLowerCase());

                generarJobIdsConTagReal(mallaData, uuaaLower, tag);

                mallaData.setTransferName(request.getTransferName());
                mallaData.setTransferUuaaRaw(schemaProcessor.getDfUuaa());
                mallaData.setTransferTimeFrom(request.getTransferTimeFrom());

                mallaData.setL1tSourceName(dfMasterName.toLowerCase() + "_l1t");
            }
            mallaData.setCopyUuaaRaw(MallaConstants.DEFAULT_COPY_UUAA_RAW);
            mallaData.setCreateNums(MallaConstants.DEFAULT_CREATE_NUMS);
        } catch (Exception e) {
            throw MallaGenerationException.configurationError(
                    "Error generando datos con ControlMAnalyzer: " + e.getMessage());
        }
    }
    private void generarJobIdsConTagReal(MallaRequestDto mallaData, String uuaaLower, String tag) {

        mallaData.setHmmStgJobid(String.format(MallaConstants.HMM_STG_ID_PATTERN, uuaaLower, tag));
        mallaData.setKrbRawJobid(String.format(MallaConstants.KRB_RAW_ID_PATTERN, uuaaLower, tag));
        mallaData.setHmmRawJobid(String.format(MallaConstants.HMM_RAW_ID_PATTERN, uuaaLower, tag));
        mallaData.setKrbMasterJobid(String.format(MallaConstants.KRB_MASTER_ID_PATTERN, uuaaLower, tag));
        mallaData.setHmmMasterJobid(String.format(MallaConstants.HMM_MASTER_ID_PATTERN, uuaaLower, tag));

        mallaData.setKrbL1tJobid(String.format(MallaConstants.KRB_L1T_ID_PATTERN, uuaaLower, tag));
        mallaData.setHmmL1tJobid(String.format(MallaConstants.HMM_L1T_ID_PATTERN, uuaaLower, tag));
    }
}
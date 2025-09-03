package com.bbva.util.metaknight.validation;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import com.bbva.util.metaknight.MallaConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MallaValidator {

    private static final Pattern UUAA_PATTERN = Pattern.compile(MallaConstants.Validation.UUAA_REGEX);
    private static final Pattern JOBNAME_PATTERN = Pattern.compile(MallaConstants.Validation.JOBNAME_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    public void validarDatosIngesta(IngestaRequestDto request) throws MallaGenerationException {
        List<String> errores = new ArrayList<>();

        if (request == null) {
            throw MallaGenerationException.validationError("El request no puede ser nulo");
        }

        if (request.isGenerarMallas()) {
            validarCamposRequeridosParaMalla(request, errores);
            validarFormatosParaMalla(request, errores);
        }

        if (!errores.isEmpty()) {
            throw MallaGenerationException.validationError(
                    "Errores de validación para malla: " + String.join(", ", errores)
            );
        }
    }

    public void validarDatosMalla(MallaRequestDto mallaData) throws MallaGenerationException {
        List<String> errores = new ArrayList<>();

        if (mallaData == null) {
            throw MallaGenerationException.validationError("Los datos de malla no pueden ser nulos");
        }

        validarCamposRequeridos(mallaData, errores);
        validarFormatos(mallaData, errores);
        validarJobnames(mallaData, errores);
        validarConsistenciaDatos(mallaData, errores);

        if (!errores.isEmpty()) {
            throw MallaGenerationException.validationError(
                    "Errores de validación en datos de malla: " + String.join(", ", errores)
            );
        }
    }

    private void validarCamposRequeridosParaMalla(IngestaRequestDto request, List<String> errores) {
        if (esNuloOVacio(request.getUuaaMaster())) {
            errores.add("UUAA Master es requerida para generar mallas");
        }

        if (esNuloOVacio(request.getRegistroDev())) {
            errores.add("Registro de desarrollador es requerido para generar mallas");
        }

        if (esNuloOVacio(request.getNombreDev())) {
            errores.add("Nombre de desarrollador es requerido para generar mallas");
        }

        if (esNuloOVacio(request.getProyecto())) {
            errores.add("Nombre de proyecto es requerido para generar mallas");
        }
    }

    private void validarFormatosParaMalla(IngestaRequestDto request, List<String> errores) {
        if (request.getUuaaMaster() != null && !UUAA_PATTERN.matcher(request.getUuaaMaster()).matches()) {
            errores.add("UUAA Master debe tener exactamente 4 caracteres alfabéticos");
        }
        if (request.getRegistroDev() != null && request.getRegistroDev().length() < 6) {
            errores.add("Registro de desarrollador debe tener al menos 6 caracteres");
        }
    }
    private void validarCamposRequeridos(MallaRequestDto mallaData, List<String> errores) {
        if (esNuloOVacio(mallaData.getCreationUser())) {
            errores.add("Usuario de creación es requerido");
        }
        if (esNuloOVacio(mallaData.getUuaa())) {
            errores.add("UUAA es requerida");
        }
        if (esNuloOVacio(mallaData.getNamespace())) {
            errores.add("Namespace es requerido");
        }
        if (esNuloOVacio(mallaData.getParentFolder())) {
            errores.add("Parent folder es requerido");
        }
        if (esNuloOVacio(mallaData.getCreationDate())) {
            errores.add("Fecha de creación es requerida");
        }
        if (esNuloOVacio(mallaData.getCreationTime())) {
            errores.add("Hora de creación es requerida");
        }
    }
    private void validarFormatos(MallaRequestDto mallaData, List<String> errores) {
        if (mallaData.getUuaa() != null && !UUAA_PATTERN.matcher(mallaData.getUuaa()).matches()) {
            errores.add("UUAA debe tener exactamente 4 caracteres alfabéticos");
        }

        if (mallaData.getTeamEmail() != null && !EMAIL_PATTERN.matcher(mallaData.getTeamEmail()).matches()) {
            errores.add("Email del equipo tiene formato inválido");
        }
        if (mallaData.getCreationDate() != null) {
            if (!mallaData.getCreationDate().matches("^\\d{8}$")) {
                errores.add("Fecha de creación debe tener formato YYYYMMDD");
            }
        }
        if (mallaData.getCreationTime() != null) {
            if (!mallaData.getCreationTime().matches("^\\d{6}$")) {
                errores.add("Hora de creación debe tener formato HHMMSS");
            }
        }
        if (mallaData.getTransferTimeFrom() != null) {
            if (!mallaData.getTransferTimeFrom().matches("^\\d{4}$")) {
                errores.add("Hora de transferencia debe tener formato HHMM");
            }
        }
    }
    private void validarJobnames(MallaRequestDto mallaData, List<String> errores) {
        validarJobname(mallaData.getTransferJobname(), "Transfer", errores);
        validarJobname(mallaData.getCopyJobname(), "Copy", errores);
        validarJobname(mallaData.getFwJobname(), "FileWatcher", errores);
        validarJobname(mallaData.getHmmStgJobname(), "Hammurabi Staging", errores);
        validarJobname(mallaData.getKrbRawJobname(), "Kirby Raw", errores);
        validarJobname(mallaData.getHmmRawJobname(), "Hammurabi Raw", errores);
        validarJobname(mallaData.getKrbMasterJobname(), "Kirby Master", errores);
        validarJobname(mallaData.getHmmMasterJobname(), "Hammurabi Master", errores);
        validarJobname(mallaData.getErase1Jobname(), "Erase1", errores);
        validarJobname(mallaData.getErase2Jobname(), "Erase2", errores);
    }
    private void validarJobname(String jobname, String tipo, List<String> errores) {

        if (esNuloOVacio(jobname)) {
            errores.add(String.format("Jobname de %s es requerido", tipo));
            return;
        }
        if (jobname.length() < MallaConstants.Validation.MIN_JOBNAME_LENGTH ||
                jobname.length() > MallaConstants.Validation.MAX_JOBNAME_LENGTH) {
            errores.add(String.format("Jobname de %s debe tener entre %d y %d caracteres",
                    tipo, MallaConstants.Validation.MIN_JOBNAME_LENGTH, MallaConstants.Validation.MAX_JOBNAME_LENGTH));
        }

        if (!JOBNAME_PATTERN.matcher(jobname).matches()) {
            errores.add(String.format("Jobname de %s debe ser alfanumérico", tipo));
        }
    }

    private void validarConsistenciaDatos(MallaRequestDto mallaData, List<String> errores) {
        if (mallaData.getUuaa() != null && mallaData.getUuaaLowercase() != null) {
            if (!mallaData.getUuaa().toLowerCase().equals(mallaData.getUuaaLowercase())) {
                errores.add("UUAA y uuaaLowercase no son consistentes");
            }
        }
        if (mallaData.getUuaa() != null) {
            String expectedPrefix = mallaData.getUuaa().toUpperCase();

            if (mallaData.getTransferJobname() != null && !mallaData.getTransferJobname().startsWith(expectedPrefix)) {
                errores.add("Transfer jobname no tiene el prefijo esperado de UUAA");
            }
        }
        if (mallaData.getNamespace() != null && mallaData.getUuaaLowercase() != null) {
            if (!mallaData.getNamespace().contains(mallaData.getUuaaLowercase())) {
                errores.add("Namespace no contiene la UUAA en minúsculas");
            }
        }
    }
    private boolean esNuloOVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
    public void validarXmlGenerado(String xmlContent, String tipo) throws MallaGenerationException {
        if (esNuloOVacio(xmlContent)) {
            throw MallaGenerationException.xmlGenerationError(
                    String.format("XML de tipo %s está vacío", tipo), null
            );
        }
        if (!xmlContent.trim().startsWith("<")) {
            throw MallaGenerationException.xmlGenerationError(
                    String.format("XML de tipo %s no tiene formato válido", tipo), null
            );
        }
        if (!xmlContent.contains("<JOB")) {
            throw MallaGenerationException.xmlGenerationError(
                    String.format("XML de tipo %s no contiene jobs válidos", tipo), null
            );
        }
        String[] elementosRequeridos = {"JOBNAME=", "APPLICATION=", "CMDLINE="};
        for (String elemento : elementosRequeridos) {
            if (!xmlContent.contains(elemento)) {
                throw MallaGenerationException.xmlGenerationError(
                        String.format("XML de tipo %s no contiene elemento requerido: %s", tipo, elemento), null
                );
            }
        }
    }
}
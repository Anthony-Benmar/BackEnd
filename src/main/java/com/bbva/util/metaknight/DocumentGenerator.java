package com.bbva.util.metaknight;

import com.bbva.dto.metaknight.request.IngestaRequestDto;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.io.*;
import java.util.*;

public class DocumentGenerator {

    private Rules rules = new Rules();
    private BaseFunctions baseFunctions = new BaseFunctions();

    /**
     * Cargar template como base64 desde resources/templates/metaknight
     */
    private String loadTemplateAsBase64(String templateName) {
        String templatePath = "templates/metaknight/" + templateName;

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath);
            if (inputStream == null) {
                throw new FileNotFoundException("Template no encontrado: " + templatePath);
            }

            byte[] templateBytes = inputStream.readAllBytes();
            inputStream.close();
            return Base64.getEncoder().encodeToString(templateBytes);

        } catch (IOException e) {
            throw new RuntimeException("Error cargando template: " + templatePath, e);
        }
    }

    /**
     * Generar documento C204 para Hammurabi
     */
    public byte[] generarDocumentoC204Hammurabi(IngestaRequestDto request, SchemaProcessor schemaProcessor) throws Exception {

        String templateBase64 = loadTemplateAsBase64("hammurabi_template_C204.docx");

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool());
        replacements.put("[titulo_proyecto]", request.getProyecto());
        replacements.put("[developer]", request.getNombreDev());
        replacements.put("[XP]", request.getRegistroDev());
        replacements.put("[df_name]", schemaProcessor.getDfMasterName());
        replacements.put("[SM]", request.getSm());
        replacements.put("[PO]", request.getPo());
        replacements.put("[job_staging_id]", schemaProcessor.getIdJsonStaging());
        replacements.put("[job_raw_id]", schemaProcessor.getIdJsonRaw());
        replacements.put("[job_master_id]", schemaProcessor.getIdJsonMaster());

        return generarDocumentoC204(templateBase64, replacements);
    }

    /**
     * Generar documento C204 para Kirby
     */
    public byte[] generarDocumentoC204Kirby(IngestaRequestDto request, SchemaProcessor schemaProcessor) throws Exception {

        String templateBase64 = loadTemplateAsBase64("kirby_template_C204.docx");

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool());
        replacements.put("[titulo_proyecto]", request.getProyecto());
        replacements.put("[developer]", request.getNombreDev());
        replacements.put("[XP]", request.getRegistroDev());
        replacements.put("[df_name]", schemaProcessor.getDfMasterName());
        replacements.put("[SM]", request.getSm());
        replacements.put("[PO]", request.getPo());
        replacements.put("[job_raw_id]", schemaProcessor.getIdJsonRaw());
        replacements.put("[job_master_id]", schemaProcessor.getIdJsonMaster());

        return generarDocumentoC204(templateBase64, replacements);
    }

    /**
     * Generar documento C204 genérico
     */
    private byte[] generarDocumentoC204(String templateBase64, Map<String, String> replacements) throws Exception {

        byte[] templateBytes = Base64.getDecoder().decode(templateBase64);

        try (InputStream templateStream = new ByteArrayInputStream(templateBytes);
             XWPFDocument document = new XWPFDocument(templateStream)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : replacements.entrySet()) {
                            text = text.replace(entry.getKey(), entry.getValue());
                        }
                        run.setText(text, 0);
                    }
                }
            }

            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                String text = run.getText(0);
                                if (text != null) {
                                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                        text = text.replace(entry.getKey(), entry.getValue());
                                    }
                                    run.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }
}
package com.bbva.util.metaknight;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseFunctions {

    private static final String NO_RE_DEFINITION_MESSAGE = "No se pudo definir RE para el formato lógico.";
    private static final String NO_RULE_DESCRIPTION_MESSAGE = "No se pudo describir la regla para el formato lógico.";

    /**
     * Convierte una configuración de regla a formato personalizado
     * Equivalente a convert_to_custom_format en Python
     */
    public String convertToCustomFormat(Map<String, Object> data) {
        StringBuilder result = new StringBuilder("{\n");
        result.append("   class = \"").append(data.get("class")).append("\"\n");
        result.append("   config = {\n");

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) data.get("config");

        appendConfigEntries(result, config);

        result.append("   }\n}");
        return result.toString();
    }
    private void appendConfigEntries(StringBuilder result, Map<String, Object> config) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.append("      ").append(key).append(" = ");

            if (value instanceof List<?> list) {
                appendListValue(result, list);
            } else if (value instanceof Boolean boolValue) {
                result.append(boolValue ? "true" : "false");
            } else if (value instanceof String) {
                result.append("\"").append(value).append("\"");
            } else {
                result.append(value);
            }
            result.append("\n");
        }
    }
    private void appendListValue(StringBuilder result, List<?> list) {
        result.append("[");
        for (int i = 0; i < list.size(); i++) {
            result.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
    }

    /**
     * Obtiene expresión regular para formato lógico
     * Equivalente a get_regular_expression en Python
     */
    public String getRegularExpression(String logicalFormat) {
        if (logicalFormat == null) {
            return NO_RE_DEFINITION_MESSAGE;
        }

        if (logicalFormat.contains("ALPHANUMERIC")) {
            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            Matcher matcher = pattern.matcher(logicalFormat);
            if (matcher.find()) {
                String contentInsideParentheses = matcher.group(1);
                return "^(.{" + contentInsideParentheses + "})$";
            } else {
                return NO_RE_DEFINITION_MESSAGE;
            }
        } else if ("DATE".equals(logicalFormat)) {
            return "^([1-9]{1}[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        } else {
            return NO_RE_DEFINITION_MESSAGE;
        }
    }

    /**
     * Convierte configuración de input a formato Hammurabi
     */
    public String convertInputToSelectedFormat(Map<String, Object> inputJson) {
        @SuppressWarnings("unchecked")
        Map<String, Object> input = (Map<String, Object>) inputJson.get("input");
        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) input.get("options");
        @SuppressWarnings("unchecked")
        List<String> paths = (List<String>) input.get("paths");
        @SuppressWarnings("unchecked")
        Map<String, Object> schema = (Map<String, Object>) input.get("schema");

        return String.format("""
            input {
                options {
                    overrideSchema = %s
                    includeMetadataAndDeleted = %s
                }
                paths = [
                    "%s"
                ]
                schema {
                    path = %s
                }
                type = "%s"
            }
            """,
                options.get("overrideSchema").toString().toLowerCase(),
                options.get("includeMetadataAndDeleted").toString().toLowerCase(),
                paths.get(0),
                schema.get("path"),
                input.get("type")
        ).trim();
    }

    /**
     * Convierte configuración de dataframe a formato Hammurabi
     */
    public String convertJsonToSelectedFormat(Map<String, Object> inputJson) {
        return String.format("""
            dataFrameInfo {
                cutoffDate = ${?CUTOFF_DATE}
                frequencyRuleExecution = "%s"
                targetPathName = "%s"
                physicalTargetName = "%s"
                uuaa = "%s"
                subset = "%s"
            }
            """,
                inputJson.get("frequencyRuleExecution"),
                inputJson.get("targetPathName"),
                inputJson.get("physicalTargetName"),
                inputJson.get("uuaa"),
                inputJson.get("subset")
        ).trim();
    }

    /**
     * Convierte configuración de input staging a formato Hammurabi
     */
    public String convertStagingInputToSelectedFormat(Map<String, Object> inputJson) {
        @SuppressWarnings("unchecked")
        Map<String, Object> input = (Map<String, Object>) inputJson.get("input");
        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) input.get("options");
        @SuppressWarnings("unchecked")
        List<String> paths = (List<String>) input.get("paths");
        @SuppressWarnings("unchecked")
        Map<String, Object> schema = (Map<String, Object>) input.get("schema");

        return String.format("""
            input {
                options {
                    delimiter= "%s"
                    header="false"
                    castMode="notPermissive"
                    charset="UTF-8"
                }
                paths = [
                    "%s"
                ]
                schema {
                    path = %s
                }
                type = "%s"
            }
            """,
                options.get("delimiter"),
                paths.get(0),
                schema.get("path"),
                input.get("type")
        ).trim();
    }

    /**
     * Convierte configuración staging JSON a formato Hammurabi
     */
    public String convertStagingJsonToSelectedFormat(Map<String, Object> inputJson) {
        return String.format("""
            dataFrameInfo {
                cutoffDate = ${?CUTOFF_DATE}
                targetPathName = "%s"
                physicalTargetName =  "%s"
                uuaa = "%s"
            }
            """,
                inputJson.get("targetPathName"),
                inputJson.get("physicalTargetName"),
                inputJson.get("uuaa")
        ).trim();
    }

    /**
     * Convierte JSON final a formato de salida
     */
    public String convertFinalJsonToSelectedFormat(Map<String, Object> inputJson) {
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) inputJson.get("params");

        return String.format("""
            {
                "_id": "%s",
                "description": "%s",
                "kind": "processing",
                "params": {
                    "configUrl": %s,
                    "sparkHistoryEnabled": "false"
                },
                "runtime": "hammurabi-lts",
                "size": "M",
                "streaming": false
            }
            """,
                inputJson.get("_id"),
                inputJson.get("description"),
                params.get("configUrl")
        ).trim();
    }

    /**
     * Obtiene descripción de regla para formato lógico
     */
    public String getRuleDescription(String logicalFormat) {
        if (logicalFormat == null) {
            return "No se pudo describir la regla para el formato lógico.";
        }

        if (logicalFormat.contains("ALPHANUMERIC")) {
            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            Matcher matcher = pattern.matcher(logicalFormat);
            if (matcher.find()) {
                String contentInsideParentheses = matcher.group(1);
                return "Comprobación del formato alfabetico de longitud 1 al " + contentInsideParentheses;
            } else {
                return "No se pudo describir la regla para el formato lógico.";
            }
        } else if ("DATE".equals(logicalFormat)) {
            return "^([1-9]{1}[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        } else {
            return "No se pudo describir la regla para el formato lógico.";
        }
    }
}
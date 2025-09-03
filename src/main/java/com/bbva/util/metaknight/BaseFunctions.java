package com.bbva.util.metaknight;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseFunctions {

    private static final String BOOLEAN_TRUE = "true";
    private static final String BOOLEAN_FALSE = "false";
    private static final String NO_RE_DEFINITION_MESSAGE = "No se pudo definir RE para el formato lógico.";
    private static final String NO_RULE_DESCRIPTION_MESSAGE = "No se pudo describir la regla para el formato lógico";
    private static final Set<String> KEYS_REQUIRING_QUOTES = Set.of("columns", "column", "format");

    public String convertToCustomFormat(Map<String, Object> data) {
        StringBuilder result = new StringBuilder("        {\n");
        result.append("            class = \"").append(data.get("class")).append("\"\n");
        result.append("            config {\n");

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) data.get("config");

        appendConfigEntries(result, config);

        result.append("            }\n");
        result.append("        }");
        return result.toString();
    }
    private void appendConfigEntries(StringBuilder result, Map<String, Object> config) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.append("                ").append(key).append(" ");

            appendConfigValue(result, key, value, "                ");
        }
    }

    private void appendConfigValue(StringBuilder result, String key, Object value, String indentation) {
        if (value instanceof Map<?, ?>) {
            appendMapValue(result, value, indentation);
        } else if (value instanceof List<?> list) {
            appendListValueBasedOnKey(result, key, list);
        } else if (value instanceof Boolean boolValue) {
            appendBooleanValue(result, boolValue);
        } else if (value instanceof String string) {
            appendStringValue(result, key, string);
        } else {
            result.append("= ").append(value).append("\n");
        }
    }

    private void appendMapValue(StringBuilder result, Object value, String indentation) {
        result.append("{\n");
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) value;

        if ("                ".equals(indentation)) {
            appendNestedConfigEntries(result, nestedMap);
        } else {
            appendDeeplyNestedConfigEntries(result, nestedMap);
        }

        result.append(indentation).append("}\n");
    }

    private void appendListValueBasedOnKey(StringBuilder result, String key, List<?> list) {
        result.append("= ");
        if (KEYS_REQUIRING_QUOTES.contains(key.toLowerCase())) {
            appendListValueWithQuotes(result, list);
        } else {
            appendListValue(result, list);
        }
        result.append("\n");
    }

    private void appendBooleanValue(StringBuilder result, Boolean boolValue) {
        result.append("= ").append(boolValue ? BOOLEAN_TRUE : BOOLEAN_FALSE).append("\n");
    }

    private void appendStringValue(StringBuilder result, String key, String string) {
        if (KEYS_REQUIRING_QUOTES.contains(key.toLowerCase())) {
            result.append("= \"").append(string).append("\"\n");
        } else {
            result.append("= ").append(string).append("\n");
        }
    }

    private void appendNestedConfigEntries(StringBuilder result, Map<String, Object> config) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.append("                    ").append(key).append(" ");

            if (value instanceof Map<?, ?>) {
                appendMapValue(result, value, "                    ");
            } else if (value instanceof List<?> list) {
                result.append("= ");
                appendListValue(result, list);
                result.append("\n");
            } else if (value instanceof Boolean boolValue) {
                appendBooleanValue(result, boolValue);
            } else if (value instanceof String) {
                result.append("= ").append(value).append("\n");
            } else {
                result.append("= ").append(value).append("\n");
            }
        }
    }

    private void appendDeeplyNestedConfigEntries(StringBuilder result, Map<String, Object> config) {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.append("                        ").append(key).append(" ");

            if (value instanceof List<?> list) {
                result.append("= ");
                appendListValue(result, list);
                result.append("\n");
            } else if (value instanceof Boolean boolValue) {
                appendBooleanValue(result, boolValue);
            } else if (value instanceof String) {
                result.append("= ").append(value).append("\n");
            } else {
                result.append("= ").append(value).append("\n");
            }
        }
    }
    private void appendListValue(StringBuilder result, List<?> list) {
        result.append("[");
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
    }

    private void appendListValueWithQuotes(StringBuilder result, List<?> list) {
        result.append("[");
        for (int i = 0; i < list.size(); i++) {
            Object elem = list.get(i);
            if (elem instanceof String str) {
                result.append("\"").append(str).append("\"");
            } else {
                result.append(elem);
            }
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
    }

    public String getRegularExpression(String logicalFormat) {
        if (logicalFormat == null) {
            return NO_RE_DEFINITION_MESSAGE;
        }

        if (logicalFormat.contains("ALPHANUMERIC")) {
            return extractAlphanumericPattern(logicalFormat);
        } else if ("DATE".equals(logicalFormat)) {
            return "^([1-9]{1}[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        } else {
            return NO_RE_DEFINITION_MESSAGE;
        }
    }

    private String extractAlphanumericPattern(String logicalFormat) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(logicalFormat);
        if (matcher.find()) {
            String contentInsideParentheses = matcher.group(1);
            return "^(.{" + contentInsideParentheses + "})$";
        } else {
            return NO_RE_DEFINITION_MESSAGE;
        }
    }

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

    public String getRuleDescription(String logicalFormat) {
        if (logicalFormat == null) {
            return NO_RULE_DESCRIPTION_MESSAGE;
        }

        if (logicalFormat.contains("ALPHANUMERIC")) {
            return extractAlphanumericDescription(logicalFormat);
        } else if ("DATE".equals(logicalFormat)) {
            return "^([1-9]{1}[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        } else {
            return NO_RULE_DESCRIPTION_MESSAGE;
        }
    }

    private String extractAlphanumericDescription(String logicalFormat) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(logicalFormat);
        if (matcher.find()) {
            String contentInsideParentheses = matcher.group(1);
            return "Comprobación del formato alfabetico de longitud 1 al " + contentInsideParentheses;
        } else {
            return NO_RULE_DESCRIPTION_MESSAGE;
        }
    }
}
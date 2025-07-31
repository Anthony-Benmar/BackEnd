package com.bbva.util.apijiramet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class DmJiraValidationMethods {

    private static final String BACKLOG_ESPERADO = "2461936";
    private static final String FIELDS = "fields";

    private DmJiraValidationMethods() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<String> validarSubtarea(JsonObject subtask) {
        JsonObject fields = subtask.getAsJsonObject(FIELDS);

        String summaryRaw = getString(fields, "summary");
        String summary = summaryRaw != null ? summaryRaw.toUpperCase() : "";
        String tipo = getNestedString(fields, "issuetype", "name");
        String prioridad = getNestedString(fields, "priority", "name");
        String status = getNestedString(fields, "status", "name");
        String backlog = getFirstFromArray(fields.get("customfield_13300"));
        String descripcion = getString(fields, "description");
        List<String> labels = getListFromArray(fields.get("labels"));

        String tipoSubtask = determinarTipoSubtask(summary);

        List<String> errores = new ArrayList<>();
        if ("Otro".equals(tipoSubtask)) errores.add("nombre inválido");
        if (!"Sub-task".equalsIgnoreCase(tipo)) errores.add("tipo incorrecto");
        if (!"Medium".equalsIgnoreCase(prioridad)) errores.add("prioridad incorrecta");
        if (!"New".equalsIgnoreCase(status)) errores.add("status inválido: se esperaba 'New'");
        if (!BACKLOG_ESPERADO.equals(backlog)) errores.add("Team Backlog distinto");
        if (descripcion == null || descripcion.trim().isEmpty()) errores.add("descripción vacía");
        if (!contieneLabelTipo(labels, tipoSubtask)) errores.add("falta label tipo");
        if (!contieneLabelDM(labels)) errores.add("falta label _DM");

        return errores;
    }

    public static List<String> obtenerDetallesValidacion(JsonObject subtask) {
        JsonObject fields = subtask.getAsJsonObject(FIELDS);

        String summaryRaw = getString(fields, "summary");
        String summary = summaryRaw != null ? summaryRaw.toUpperCase() : "";
        String tipo = getNestedString(fields, "issuetype", "name");
        String prioridad = getNestedString(fields, "priority", "name");
        String status = getNestedString(fields, "status", "name");
        String backlog = getFirstFromArray(fields.get("customfield_13300"));
        String descripcion = getString(fields, "description");
        List<String> labels = getListFromArray(fields.get("labels"));

        String tipoSubtask = determinarTipoSubtask(summary);
        String labelEsperado = obtenerLabelEsperado(tipoSubtask);

        List<String> detalles = new ArrayList<>();
        detalles.add("Otro".equals(tipoSubtask)
                ? "Incorrecto: Nombre inválido"
                : "Correcto: Nombre válido (" + tipoSubtask + ")");
        detalles.add("Sub-task".equalsIgnoreCase(tipo)
                ? "Correcto: Tipo Subtarea"
                : "Incorrecto: Tipo no es Subtarea");
        detalles.add("Medium".equalsIgnoreCase(prioridad)
                ? "Correcto: Prioridad Medium"
                : "Incorrecto: Prioridad distinta de Medium");
        detalles.add("New".equalsIgnoreCase(status)
                ? "Correcto: Status New"
                : "Incorrecto: Status inválido (se esperaba 'New')");
        detalles.add(BACKLOG_ESPERADO.equals(backlog)
                ? "Correcto: Team Backlog PE DATA MODELLING"
                : "Incorrecto: Team Backlog distinto a PE DATA MODELLING");
        detalles.add((descripcion != null && !descripcion.trim().isEmpty())
                ? "Correcto: Descripción presente"
                : "Incorrecto: Descripción vacía");
        detalles.add(contieneLabelTipo(labels, tipoSubtask)
                ? "Correcto: Label tipo presente (" + labelEsperado + ")"
                : "Incorrecto: Falta label tipo (" + labelEsperado + ")");
        detalles.add(contieneLabelDM(labels)
                ? "Correcto: Sufijo label _DM"
                : "Incorrecto: Falta de sufijo _DM");

        return detalles;
    }

    public static String determinarTipoSubtask(String summary) {
        summary = summary.toUpperCase();
        if (summary.contains("VISADO")) return "Visado";
        if (summary.contains("VOLCADO")) return "Volcado";
        if (summary.contains("CORRECION")) return "Correcion";
        return "Otro";
    }

    public static String obtenerLabelEsperado(String tipoSubtask) {
        switch (tipoSubtask) {
            case "Visado": return "#TTV_Visado";
            case "Volcado": return "#TTV_Dictamen";
            case "Correcion": return "#TTV_Correccion";
            default: return "#TTV_*";
        }
    }

    public static boolean contieneLabelTipo(List<String> labels, String tipoSubtask) {
        String labelEsperado = obtenerLabelEsperado(tipoSubtask);
        return labels.stream().anyMatch(label -> label.equalsIgnoreCase(labelEsperado));
    }

    public static boolean contieneLabelDM(List<String> labels) {
        return labels.stream().anyMatch(label -> label.toUpperCase().endsWith("_DM"));
    }

    private static String getString(JsonObject obj, String memberName) {
        JsonElement el = obj.get(memberName);
        return el != null && el.isJsonPrimitive() ? el.getAsString() : null;
    }

    private static String getNestedString(JsonObject obj, String memberName, String nestedMember) {
        JsonElement el = obj.get(memberName);
        if (el != null && el.isJsonObject()) {
            JsonObject nested = el.getAsJsonObject();
            return getString(nested, nestedMember);
        }
        return null;
    }

    private static String getFirstFromArray(JsonElement arrayElement) {
        if (arrayElement != null && arrayElement.isJsonArray()) {
            JsonArray array = arrayElement.getAsJsonArray();
            if (array.size() > 0) return array.get(0).getAsString();
        }
        return "";
    }

    private static List<String> getListFromArray(JsonElement arrayElement) {
        List<String> list = new ArrayList<>();
        if (arrayElement != null && arrayElement.isJsonArray()) {
            for (JsonElement elem : arrayElement.getAsJsonArray()) {
                list.add(elem.getAsString());
            }
        }
        return list;
    }
}
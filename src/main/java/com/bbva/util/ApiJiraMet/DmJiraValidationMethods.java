package com.bbva.util.ApiJiraMet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DmJiraValidationMethods {

    private static final String BACKLOG_ESPERADO = "2461936";

    public static List<String> validarSubtarea(JsonObject subtask) {
        List<String> errores = new ArrayList<>();

        String summary = subtask.getAsJsonObject("fields").get("summary").getAsString().toUpperCase();
        String tipo = subtask.getAsJsonObject("fields").get("issuetype").getAsJsonObject().get("name").getAsString();
        String prioridad = subtask.getAsJsonObject("fields").get("priority").getAsJsonObject().get("name").getAsString();
        String status = subtask.getAsJsonObject("fields").get("status").getAsJsonObject().get("name").getAsString();
        String backlog = subtask.getAsJsonObject("fields").getAsJsonArray("customfield_13300").get(0).getAsString();
        String descripcion = subtask.getAsJsonObject("fields").get("description").getAsString();
        JsonArray labelsJson = subtask.getAsJsonObject("fields").getAsJsonArray("labels");

        String tipoSubtask = determinarTipoSubtask(summary);

        List<String> labels = new ArrayList<>();
        for (JsonElement label : labelsJson) {
            labels.add(label.getAsString());
        }

        if (tipoSubtask.equals("Otro")) errores.add("nombre inválido");
        if (!tipo.equalsIgnoreCase("Sub-task")) errores.add("tipo incorrecto");
        if (!prioridad.equalsIgnoreCase("Medium")) errores.add("prioridad incorrecta");
        if (!status.equalsIgnoreCase("New")) errores.add("status inválido: se esperaba 'New'");
        if (!backlog.equals(BACKLOG_ESPERADO)) errores.add("Team Backlog distinto");
        if (descripcion == null || descripcion.trim().isEmpty()) errores.add("descripción vacía");
        if (!contieneLabelTipo(labels, tipoSubtask)) errores.add("falta label tipo");
        if (!contieneLabelDM(labels)) errores.add("falta label _DM");

        return errores;
    }

    public static List<String> obtenerDetallesValidacion(JsonObject subtask) {
        List<String> detalles = new ArrayList<>();

        String summary = subtask.getAsJsonObject("fields").get("summary").getAsString().toUpperCase();
        String tipo = subtask.getAsJsonObject("fields").get("issuetype").getAsJsonObject().get("name").getAsString();
        String prioridad = subtask.getAsJsonObject("fields").get("priority").getAsJsonObject().get("name").getAsString();
        String status = subtask.getAsJsonObject("fields").get("status").getAsJsonObject().get("name").getAsString();
        String backlog = subtask.getAsJsonObject("fields").getAsJsonArray("customfield_13300").get(0).getAsString();
        String descripcion = subtask.getAsJsonObject("fields").get("description").getAsString();
        JsonArray labelsJson = subtask.getAsJsonObject("fields").getAsJsonArray("labels");

        String tipoSubtask = determinarTipoSubtask(summary);

        List<String> labels = new ArrayList<>();
        for (JsonElement label : labelsJson) {
            labels.add(label.getAsString());
        }

        detalles.add(tipoSubtask.equals("Otro")
                ? "Incorrecto: Nombre inválido"
                : "Correcto: Nombre válido (" + tipoSubtask + ")");

        detalles.add(tipo.equalsIgnoreCase("Sub-task")
                ? "Correcto: Tipo Subtarea"
                : "Incorrecto: Tipo no es Subtarea");

        detalles.add(prioridad.equalsIgnoreCase("Medium")
                ? "Correcto: Prioridad Medium"
                : "Incorrecto: Prioridad distinta de Medium");

        detalles.add(status.equalsIgnoreCase("New")
                ? "Correcto: Status New"
                : "Incorrecto: Status inválido (se esperaba 'New')");

        detalles.add(backlog.equals(BACKLOG_ESPERADO)
                ? "Correcto: Team Backlog PE DATA MODELLING"
                : "Incorrecto: Team Backlog distinto a PE DATA MODELLING");

        detalles.add((descripcion != null && !descripcion.trim().isEmpty())
                ? "Correcto: Descripción presente"
                : "Incorrecto: Descripción vacía");

        String labelEsperado = obtenerLabelEsperado(tipoSubtask);
        detalles.add(contieneLabelTipo(labels, tipoSubtask)
                ? "Correcto: Label tipo presente (" + labelEsperado + ")"
                : "Incorrecto: Falta label tipo (" + labelEsperado + ")");

        detalles.add(contieneLabelDM(labels)
                ? "Correcto: Prefijo label _DM"
                : "Incorrecto: Falta de prefijo _DM");

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
}

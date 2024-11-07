package com.bbva.common.jiraValidador;

import java.util.*;

public class JiraValidatorConstantes {
    public static final Map<String, List<String>> DEVELOPS_TYPES;
    public static final Map<String, List<String>> ATTACHS_BY_DEVELOP_TYPES;
    public static final Map<String, List<String>> VOBO_BY_DEVELOP_TYPES;
    public static final Map<String, List<String>> SUBTASKS_SPECIALS;
    public static final Map<String, List<String>> SUBTASKS_BY_DEVELOP_TYPES;
    public static final Map<String, Map<String, Object>> CRITERIA_BY_DEVELOP_TYPES;
    public static final Map<String, Map<String, Object>> SUBTASKS_TYPE_OWNER;
    public static final Map<String, List<String>> LABELS_BY_DEVELOP_TYPES;
    public static final Map<String, List<String>> TICKET_HU_TYPES;

    static {
        Map<String, List<String>> mapDevTypes = new HashMap<>();
        mapDevTypes.put("mallas", new ArrayList<>(List.of("Control M")));
        mapDevTypes.put("host", new ArrayList<>(List.of("host")));
        mapDevTypes.put("hammurabi", new ArrayList<>(List.of("hammurabi")));
        mapDevTypes.put("migrationtool", new ArrayList<>(List.of("migrationtool")));
        mapDevTypes.put("smartcleaner", new ArrayList<>(List.of("smartcleaner")));
        mapDevTypes.put("ingesta", new ArrayList<>(List.of("ingesta", "kirby")));
        mapDevTypes.put("procesamiento", new ArrayList<>(List.of("procesamiento")));
        mapDevTypes.put("operativizacion", new ArrayList<>(List.of("operativizaci")));
        mapDevTypes.put("productivizacion", new ArrayList<>(List.of("productivizaci")));
        mapDevTypes.put("scaffolder", new ArrayList<>(List.of("assets")));
        mapDevTypes.put("sparkcompactor", new ArrayList<>(List.of("sparkcompactor")));
        mapDevTypes.put("json global", new ArrayList<>(List.of("json")));
        mapDevTypes.put("teradata", new ArrayList<>(List.of("Creación de archivo")));

        DEVELOPS_TYPES = Collections.unmodifiableMap(mapDevTypes);

        Map<String, List<String>> mapAttachByDevTypes = new HashMap<>();
        mapAttachByDevTypes.put("mallas", new ArrayList<>(List.of("C204","P110")));
        mapAttachByDevTypes.put("prs", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("hammurabi", new ArrayList<>(List.of("C204","RC")));
        mapAttachByDevTypes.put("host", new ArrayList<>(List.of("C204","P110")));
        mapAttachByDevTypes.put("migrationtool", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("smartcleaner", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("ingesta", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("procesamiento", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("operativizacion", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("productivizacion", new ArrayList<>(List.of("")));
        mapAttachByDevTypes.put("scaffolder", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("json global", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("teradata", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("sparkcompactor", new ArrayList<>(List.of("C204")));

        ATTACHS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapAttachByDevTypes);

        Map<String, List<String>> mapLabelsByDevTypes = new HashMap<>();
        mapLabelsByDevTypes.put("mallas", new ArrayList<>(List.of("release","ReleaseMallasDatio")));
        mapLabelsByDevTypes.put("prs", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("productivizacion", new ArrayList<>(List.of("ReleaseScaffolderDatio")));
        mapLabelsByDevTypes.put("host", new ArrayList<>(List.of("releaseTransmisionDatio")));
        mapLabelsByDevTypes.put("hammurabi", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("migrationtool", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("smartcleaner", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("ingesta", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("procesamiento", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("operativizacion", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("scaffolder", new ArrayList<>(List.of("ReleaseScaffolderDatio")));
        mapLabelsByDevTypes.put("sparkcompactor", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("json global", new ArrayList<>(List.of("ReleasePRDatio")));
        mapLabelsByDevTypes.put("teradata", new ArrayList<>(List.of("ReleasePRDatio")));

        LABELS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapLabelsByDevTypes);

        Map<String, List<String>> mapTicketHuTypes = new HashMap<>();
        mapTicketHuTypes.put("mallas", new ArrayList<>(List.of("Dependency")));
        mapTicketHuTypes.put("prs", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("productivizacion", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("host", new ArrayList<>(List.of("Dependency")));
        mapTicketHuTypes.put("hammurabi", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("migrationtool", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("smartcleaner", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("ingesta", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("procesamiento", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("operativizacion", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("scaffolder", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("sparkcompactor", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("json global", new ArrayList<>(List.of("Story")));
        mapTicketHuTypes.put("teradata", new ArrayList<>(List.of("Story")));

        TICKET_HU_TYPES = Collections.unmodifiableMap(mapTicketHuTypes);

        Map<String, List<String>> mapVoBoByDevTypes = new HashMap<>();
        mapVoBoByDevTypes.put("mallas", new ArrayList<>(List.of("PO","AT","DEV")));
        mapVoBoByDevTypes.put("scaffolder", new ArrayList<>(List.of("PO")));

        VOBO_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapVoBoByDevTypes);

        Map<String, Map<String, Object>> mapCriteriaByDevTypes = new HashMap<>();
        String acceptanceCriteria = "Como {0} declaramos que este Pase se encuentra listo para transitar por las etapas de Certificación Técnica, QA y Despliegue a Producción. La documentación adjunta corresponde al {1} así como las Historias de Usuario enlazadas a este pase.";
        mapCriteriaByDevTypes.put("mallas", Map.of("texto", acceptanceCriteria));
        mapCriteriaByDevTypes.put("host", Map.of("texto", acceptanceCriteria));

        mapCriteriaByDevTypes.put("prs", Map.of(
                "texto", "Desarrollo según lineamientos globales ONE y de Data Quality Assurance Perú."
        ));

        // Texto común para los tipos de desarrollo
        String commonText = "Desarrollo según lineamientos globales ONE y de Data Quality Assurance Perú.";

        // Agregar tipos de desarrollo que comparten el mismo texto
        List<String> devTypesWithCommonText = List.of(
                "hammurabi", "migrationtool", "smartcleaner",
                "ingesta", "procesamiento", "operativizacion",
                "scaffolder", "sparkcompactor", "json global",
                "teradata","productivizacion"
        );

        // Añadir cada tipo de desarrollo al mapa con el texto común
        for (String devType : devTypesWithCommonText) {
            mapCriteriaByDevTypes.put(devType, Map.of("texto", commonText));
        }

        CRITERIA_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapCriteriaByDevTypes);

        Map<String, List<String>> mapSubtasksByDevTypes = new HashMap<>();
        mapSubtasksByDevTypes.put("mallas", new ArrayList<>(List.of("[P110][AT]", "[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("prs", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("productivizacion", new ArrayList<>(List.of("[VB][PO]", "[VB][QA]")));
        mapSubtasksByDevTypes.put("host", new ArrayList<>(List.of("[P110][AT]", "[C204][PO]", "[C204][QA]","[P110][GC]")));
        mapSubtasksByDevTypes.put("hammurabi", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("migrationtool", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("smartcleaner", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("ingesta", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("procesamiento", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("operativizacion", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("scaffolder", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("sparkcompactor", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("json global", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("teradata", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));

        SUBTASKS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapSubtasksByDevTypes);


        Map<String, List<String>> mapSubtasksSpecials = new HashMap<>();
        mapSubtasksSpecials.put("cambio dummy", new ArrayList<>(List.of("[AT]","[QA]"))); //VB = VoBo [VB]
        mapSubtasksSpecials.put("ruta critica", new ArrayList<>(List.of("[KM]"))); //VB = VoBo [VB]
        mapSubtasksSpecials.put("ticket integracion", new ArrayList<>(List.of("[AT]", "[QA]"))); //VB = VoBo [VB]

        SUBTASKS_SPECIALS = Collections.unmodifiableMap(mapSubtasksSpecials);

        Map<String, Map<String, Object>> mapSubtasksTipoOwner = new HashMap<>();
        mapSubtasksTipoOwner.put("sm", new HashMap<>(Map.of(
                "label", "SM",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[P110][AT]","[VB][AT]","[C204][AT]")),
                "rol", new ArrayList<>(List.of("1","2"))
                )));
        mapSubtasksTipoOwner.put("po", new HashMap<>(Map.of(
                "label", "PO",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[C204][PO]","[VB][PO]")),
                "rol", new ArrayList<>(List.of("5","8"))
                )));
        mapSubtasksTipoOwner.put("so", new HashMap<>(Map.of(
                "label", "SO",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[VB][SO]")),
                "rol", new ArrayList<>(List.of("9","10"))
                )));
        mapSubtasksTipoOwner.put("km", new HashMap<>(Map.of(
                "label", "KM",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[VB][KM]")),
                "rol", new ArrayList<>(List.of("13","14"))
                )));
        mapSubtasksTipoOwner.put("gc", new HashMap<>(Map.of(
                "label", "GC",
                "validateEmailFromLideres", false,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", false,
                "advertenciaEstadoInicial", true,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[P110][GC]"))
                )));
        mapSubtasksTipoOwner.put("qa", new HashMap<>(Map.of(
                "label", "QA",
                "validateEmailFromLideres", false,
                "validateEmailContractor", false,
                "advertenciaReadyToVerify", false,
                "advertenciaEstadoInicial", true,
                "status", new ArrayList<>(List.of("Ready")),
                "items", new ArrayList<>(List.of("[C204][QA]", "[VB][QA]"))
                )));
    SUBTASKS_TYPE_OWNER = Collections.unmodifiableMap(mapSubtasksTipoOwner);

    }
}

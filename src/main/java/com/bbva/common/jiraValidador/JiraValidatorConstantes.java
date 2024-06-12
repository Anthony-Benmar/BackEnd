package com.bbva.common.jiraValidador;

import java.util.*;

public class JiraValidatorConstantes {
    public static final Map<String, List<String>> DEVELOPS_TYPES;
    public static final Map<String, List<String>> ATTACHS_BY_DEVELOP_TYPES;
    public static final Map<String, List<String>> VOBO_BY_DEVELOP_TYPES;
    //public static final Map<String, List<String>> SUBTASKS_SPECIALS;
    public static final Map<String, List<String>> SUBTASKS_BY_DEVELOP_TYPES;

    public static final Map<String, Map<String, Object>> CRITERIA_BY_DEVELOP_TYPES;

    public static final Map<String, Map<String, Object>> SUBTASKS_TYPE_OWNER;

    static {
        Map<String, List<String>> mapDevTypes = new HashMap<>();
        mapDevTypes.put("mallas", new ArrayList<>(List.of("Control M")));
        mapDevTypes.put("host", new ArrayList<>(List.of("host")));
        mapDevTypes.put("hammurabi", new ArrayList<>(List.of("hammurabi")));
        mapDevTypes.put("migrationtool", new ArrayList<>(List.of("migrationtool")));
        mapDevTypes.put("smartcleaner", new ArrayList<>(List.of("smartcleaner")));
        mapDevTypes.put("ingesta", new ArrayList<>(List.of("ingesta", "kirby")));
        mapDevTypes.put("procesamiento", new ArrayList<>(List.of("procesamiento")));
        mapDevTypes.put("operativizacion", new ArrayList<>(List.of("operativizacion")));
        mapDevTypes.put("productivizacion", new ArrayList<>());
        mapDevTypes.put("scaffolder", new ArrayList<>(List.of("assets")));
        mapDevTypes.put("sparkcompactor", new ArrayList<>(List.of("sparkcompactor")));
        mapDevTypes.put("json global", new ArrayList<>(List.of("json")));
        mapDevTypes.put("teradata", new ArrayList<>(List.of("Creación de archivo")));

        DEVELOPS_TYPES = Collections.unmodifiableMap(mapDevTypes);

        Map<String, List<String>> mapAttachByDevTypes = new HashMap<>();
        mapAttachByDevTypes.put("mallas", new ArrayList<>(List.of("C204","P110")));
        mapAttachByDevTypes.put("prs", new ArrayList<>(List.of("C204")));
        mapAttachByDevTypes.put("hammurabi", new ArrayList<>(List.of("C204", "Reglas de Calidad")));
        ATTACHS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapAttachByDevTypes);


        Map<String, List<String>> mapVoBoByDevTypes = new HashMap<>();
        mapVoBoByDevTypes.put("mallas", new ArrayList<>(List.of("PO","AT","DEV")));
        mapVoBoByDevTypes.put("scaffolder", new ArrayList<>(List.of("PO")));
        mapVoBoByDevTypes.put("cambio dummy", new ArrayList<>(List.of("[AT]","[QA]"))); //VB = VoBo [VB]
        mapVoBoByDevTypes.put("ruta critica", new ArrayList<>(List.of("[KM]"))); //VB = VoBo [VB]
        mapVoBoByDevTypes.put("ticket integracion", new ArrayList<>(List.of("[AT]", "[QA]"))); //VB = VoBo [VB]
        VOBO_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapVoBoByDevTypes);


        Map<String, Map<String, Object>> mapCriteriaByDevTypes = new HashMap<>();
        mapCriteriaByDevTypes.put("mallas",Map.of(
                "texto", "Como equipo declaramos que el siguiente Pase está listo para transitar por las etapas de Certificación QA y Pase a Producción, y la documentación Adjunta corresponde {0} así como las Historias de Usuario enlazadas a este pase."
        ));
        mapCriteriaByDevTypes.put("prs", Map.of(
                "texto", "Desarrollo según los Lineamientos del Equipo de DQA"
        ));
        mapCriteriaByDevTypes.put("productivizacion", Map.of(
                "texto", "Despliegue según los Lineamientos del Equipo de DQA"
        ));
        CRITERIA_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapCriteriaByDevTypes);

        Map<String, List<String>> mapSubtasksByDevTypes = new HashMap<>();
        mapSubtasksByDevTypes.put("mallas", new ArrayList<>(List.of("[P110][AT]", "[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("prs", new ArrayList<>(List.of("[C204][PO]", "[C204][QA]")));
        mapSubtasksByDevTypes.put("productivizacion", new ArrayList<>(List.of("[VB][PO]", "[VB][QA]")));
        SUBTASKS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapSubtasksByDevTypes);


        Map<String, Map<String, Object>> mapSubtasksTipoOwner = new HashMap<>();
        mapSubtasksTipoOwner.put("sm", new HashMap<>(Map.of(
                "label", "SM",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[P110][AT]","[VB][AT]","[C204][AT]"))
                )));
        mapSubtasksTipoOwner.put("po", new HashMap<>(Map.of(
                "label", "PO",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[C204][PO]","[VB][PO]"))
                )));
        mapSubtasksTipoOwner.put("so", new HashMap<>(Map.of(
                "label", "SO",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[VB][SO]"))
                )));
        mapSubtasksTipoOwner.put("km", new HashMap<>(Map.of(
                "label", "KM",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
                "items", new ArrayList<>(List.of("[VB][KM]"))
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


//    public static final Map<String, Map<String, Object>> SUBTASKS_TIPO_OWNER;

//    Map<String, Map<String, Object>> mapSubtasksTipoOwner = new HashMap<>();
//        mapSubtasksTipoOwner.put("sm", new HashMap<>(Map.of(
//                "label", "SM",
//                "validateEmailFromLideres", true,
//                "validateEmailContractor", true,
//                "advertenciaReadyToVerify", true,
//                "advertenciaEstadoInicial", false,
//                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
//                "items", new ArrayList<>(List.of("[P110][AT]","[VB][AT]","[C204][AT]"))
//                )));
//        mapSubtasksTipoOwner.put("po", new HashMap<>(Map.of(
//                "label", "PO",
//                "validateEmailFromLideres", true,
//                "validateEmailContractor", true,
//                "advertenciaReadyToVerify", true,
//                "advertenciaEstadoInicial", false,
//                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
//                "items", new ArrayList<>(List.of("[C204][PO]","[VB][PO]"))
//                )));
//        mapSubtasksTipoOwner.put("so", new HashMap<>(Map.of(
//                "label", "SO",
//                "validateEmailFromLideres", true,
//                "validateEmailContractor", true,
//                "advertenciaReadyToVerify", true,
//                "advertenciaEstadoInicial", false,
//                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
//                "items", new ArrayList<>(List.of("[VB][SO]"))
//                )));
//        mapSubtasksTipoOwner.put("km", new HashMap<>(Map.of(
//                "label", "KM",
//                "validateEmailFromLideres", true,
//                "validateEmailContractor", true,
//                "advertenciaReadyToVerify", true,
//                "advertenciaEstadoInicial", false,
//                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
//                "items", new ArrayList<>(List.of("[VB][KM]"))
//                )));
//        mapSubtasksTipoOwner.put("gc", new HashMap<>(Map.of(
//                "label", "GC",
//                "validateEmailFromLideres", false,
//                "validateEmailContractor", true,
//                "advertenciaReadyToVerify", false,
//                "advertenciaEstadoInicial", true,
//                "status", new ArrayList<>(List.of("Accepted", "Ready To Verify")),
//                "items", new ArrayList<>(List.of("[P110][GC]"))
//                )));
//        mapSubtasksTipoOwner.put("qa", new HashMap<>(Map.of(
//                "label", "QA",
//                "validateEmailFromLideres", false,
//                "validateEmailContractor", false,
//                "advertenciaReadyToVerify", false,
//                "advertenciaEstadoInicial", true,
//                "status", new ArrayList<>(List.of("Ready")),
//                "items", new ArrayList<>(List.of("[C204][QA]", "[VB][QA]"))
//                )));
//    SUBTASKS_TIPO_OWNER = Collections.unmodifiableMap(mapSubtasksTipoOwner);
//

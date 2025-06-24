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
    public static final String MALLAS = "mallas";
    public static final String HAMMURABI = "hammurabi";
    public static final String HOST = "host";
    public static final String MIGRATIONTOOL = "migrationtool";
    public static final String SMARTCLEANER = "smartcleaner";
    public static final String INGESTA = "ingesta";
    public static final String KIRBY = "kirby";
    public static final String PROCESAMIENTO = "procesamiento";
    public static final String OPERATIVIZACION = "operativizacion";
    public static final String PRODUCTIVIZACION = "productivizacion";
    public static final String SCAFFOLDER = "scaffolder";
    public static final String SPARKCOMPACTOR = "sparkcompactor";
    public static final String JSON_GLOBAL = "json global";
    public static final String TERADATA = "teradata";
    public static final String RELEASEMALLASDATIO = "ReleaseMallasDatio";
    public static final String RELEASEPRDATIO = "ReleasePRDatio";
    public static final String RELEASESCAFFOLDERDATIO = "ReleaseScaffolderDatio";
    public static final List<String> STORY = List.of("Story", "Historia");
    public static final String P110_AT = "[P110][AT]";
    public static final String C204_PO = "[C204][PO]";
    public static final String C204_QA = "[C204][QA]";
    public static final String TEXTO = "texto";
    public static final String FIELDS = "fields";
    public static final String CHANGELOG = "changelog";
    public static final String HISTORIES = "histories";
    public static final String FIELD = "field";
    public static final String TEAM_BACKLOG = "Team Backlog";
    public static final String SUMMARY = "summary";
    public static final String ISSUETYPE = "issuetype";
    public static final String ATTACHMENT = "attachment";
    public static final String ISSUELINKS = "issuelinks";
    public static final String CREATED = "created";
    public static final String ITEMS = "items";
    public static final String CUSTOMFIELD_10004 = "customfield_10004";
    public static final String CUSTOMFIELD_10264 = "customfield_10264";
    public static final String CUSTOMFIELD_13300 = "customfield_13300";
    public static final String CUSTOMFIELD_13301 = "customfield_13301";
    public static final String MSG_RULE_INVALID = "Esta regla no es válida para este tipo de desarrollo.";
    public static final String INWARD = "inward";
    public static final String OUTWARD = "outward";
    public static final String STATUS = "status";
    public static final String INWARD_ISSUE = "inwardIssue";
    public static final String OUTWARD_ISSUE = "outwardIssue";
    public static final String DEPLOYED = "Deployed";
    public static final String VB_KM = "[VB][KM]";
    public static final String VB_SO = "[VB][SO]";
    public static final String VB_DEV = "[VB][DEV]";
    public static final String VB_ADA = "[VB][ADA]";
    public static final String VB_ALPHA = "[VB][ALPHA]";
    public static final String P110_GC = "[P110][GC]";
    public static final String SUBTASKS = "subtasks";
    public static final String ACCEPTED = "Accepted";
    public static final String DISCARDED = "Discarded";
    public static final String KEY_IN = "key%20in%20(";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String MSG_SUBTAREA = "Subtarea ";
    public static final String ISSUES = "issues";
    public static final String MSG_RULE_VALID = "Es válido: %s";
    public static final String ACCEPTANCE_CRITERIA_MESH = "Como {0} declaramos que este Pase se encuentra listo para transitar por las etapas de Certificación Técnica, QA y Despliegue a Producción";
    public static final String ACCEPTANCE_CRITERIA_COMMON_TEXT = "Desarrollo según lineamientos globales ONE y de Data Quality Assurance Perú";
    public static final String MSG_RULE_CRITERIOFORMATO_MESH = "Criterio de aceptación no cumple con el formato requerido. \"" + ACCEPTANCE_CRITERIA_MESH.replace("{0}", "(Nombre de Equipo) ") + "\"";
    public static final String MSG_RULE_CRITERIOFORMATO_PR = "Criterio de aceptación no cumple con el formato requerido.  " + ACCEPTANCE_CRITERIA_COMMON_TEXT;
    public static final String MSG_RULE_TIPODESARROLLO = "Tipo de desarrollo no encontrado en los criterios de aceptación";
    public static final String MSG_RULE_CRITEROACEPTACION = "Sin Criterio de Aceptación";
    public static final String MSG_RULE_EXCEPTION_RLB = "Proviene del tabero RLB, por lo que no tiene dependencia asociada y, en consecuencia, esta regla no es aplicable.";
    public static final String MSG_RULE_ASIGNEE_DQA = "Asignado a Tablero de DQA";
    public static final String LABEL = "label";
    public static final List<String> DEPENDENCY = List.of("Dependency","Dependencia");
    public static final String READY_TO_VERIFY = "Ready To Verify";
    public static final String READY = "Ready";
    public static final String TEST = "Test";
    public static final String READY_TO_DEPLOY ="Ready To Deploy";
    public static final String IN_PROGRESS = "In Progress";
    public static final String EN_PROGRESO = "En progreso";
    public static final String MSG_RULE_NOFEATURE = "Sin Feature Link asociado";
    public static final String LABELS = "labels";
    public static final String MSG_RULE_NOSUBTAREA = "Faltan alguna de las siguientes subtareas: ";
    public static final String MSG_RULE_RECOMENDATIONSUBTAREA = "Se recomienda validar las subtareas adicional: ";
    public static final String IS_CHILD_ITEM_OF = "is child item of";
    public static final String IS_PARENT_ITEM_OF = "is parent item of";
    public static final String MSG_RULE_NODEPENDENCY = "Ticket no cuenta con Dependencia Asociada.";
    public static final String MSG_UUAA = "Se encontro UUAAs ";
    public static final String MSG_COORDINATION_MESSAGE = "de ser necesario coordinar con el SM / QE";
    public static final String MESSAGE = "message";
    public static final String ISVALID = "isValid";
    public static final String ISWARNING = "isWarning";
    public static final String HELPMESSAGE = "helpMessage";
    public static final String GROUP = "group";
    public static final String C204 = "C204";
    public static final String P110 = "P110";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String ASSIGNEE = "assignee";
    public static final String PRS = "prs";
    public static final String TEAM_BACK_LOG_DQA_ID = "2461905";
    public static final String QA = "[QA]";
    public static final String KEY = "key";
    public static final String MSG_ERROR_RULE = "Ocurrió un error en la validación de la regla. Favor de contactar con DQA.";

    static {
        Map<String, List<String>> mapDevTypes = new HashMap<>();
        mapDevTypes.put(MALLAS, new ArrayList<>(List.of("Control M")));
        mapDevTypes.put(HOST, new ArrayList<>(List.of(HOST)));
        mapDevTypes.put(HAMMURABI, new ArrayList<>(List.of(HAMMURABI)));
        mapDevTypes.put(MIGRATIONTOOL, new ArrayList<>(List.of(MIGRATIONTOOL)));
        mapDevTypes.put(SMARTCLEANER, new ArrayList<>(List.of(SMARTCLEANER)));
        mapDevTypes.put(INGESTA, new ArrayList<>(List.of(INGESTA, KIRBY)));
        mapDevTypes.put(PROCESAMIENTO, new ArrayList<>(List.of(PROCESAMIENTO)));
        mapDevTypes.put(OPERATIVIZACION, new ArrayList<>(List.of("operativizaci")));
        mapDevTypes.put(PRODUCTIVIZACION, new ArrayList<>(List.of("productivizaci")));
        mapDevTypes.put(SCAFFOLDER, new ArrayList<>(List.of("assets")));
        mapDevTypes.put(SPARKCOMPACTOR, new ArrayList<>(List.of(SPARKCOMPACTOR)));
        mapDevTypes.put(JSON_GLOBAL, new ArrayList<>(List.of("json")));
        mapDevTypes.put(TERADATA, new ArrayList<>(List.of("Creación de archivo")));

        DEVELOPS_TYPES = Collections.unmodifiableMap(mapDevTypes);

        Map<String, List<String>> mapAttachByDevTypes = new HashMap<>();
        mapAttachByDevTypes.put(MALLAS, new ArrayList<>(List.of(C204,P110)));
        mapAttachByDevTypes.put(PRS, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(HAMMURABI, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(HOST, new ArrayList<>(List.of(C204,P110)));
        mapAttachByDevTypes.put(MIGRATIONTOOL, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(SMARTCLEANER, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(INGESTA, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(PROCESAMIENTO, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(OPERATIVIZACION, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(PRODUCTIVIZACION, new ArrayList<>(List.of("")));
        mapAttachByDevTypes.put(SCAFFOLDER, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(JSON_GLOBAL, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(TERADATA, new ArrayList<>(List.of(C204)));
        mapAttachByDevTypes.put(SPARKCOMPACTOR, new ArrayList<>(List.of(C204)));

        ATTACHS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapAttachByDevTypes);

        Map<String, List<String>> mapLabelsByDevTypes = new HashMap<>();
        mapLabelsByDevTypes.put(MALLAS, new ArrayList<>(List.of("release", RELEASEMALLASDATIO)));
        mapLabelsByDevTypes.put(PRS, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(PRODUCTIVIZACION, new ArrayList<>(List.of(RELEASESCAFFOLDERDATIO)));
        mapLabelsByDevTypes.put(HOST, new ArrayList<>(List.of("releaseTransmisionDatio")));
        mapLabelsByDevTypes.put(HAMMURABI, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(MIGRATIONTOOL, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(SMARTCLEANER, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(INGESTA, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(PROCESAMIENTO, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(OPERATIVIZACION, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(SCAFFOLDER, new ArrayList<>(List.of(RELEASESCAFFOLDERDATIO)));
        mapLabelsByDevTypes.put(SPARKCOMPACTOR, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(JSON_GLOBAL, new ArrayList<>(List.of(RELEASEPRDATIO)));
        mapLabelsByDevTypes.put(TERADATA, new ArrayList<>(List.of(RELEASEPRDATIO)));

        LABELS_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapLabelsByDevTypes);

        Map<String, List<String>> mapTicketHuTypes = new HashMap<>();
        mapTicketHuTypes.put(MALLAS, DEPENDENCY);
        mapTicketHuTypes.put(PRS, STORY);
        mapTicketHuTypes.put(PRODUCTIVIZACION, STORY);
        mapTicketHuTypes.put(HOST, DEPENDENCY);
        mapTicketHuTypes.put(HAMMURABI, STORY);
        mapTicketHuTypes.put(MIGRATIONTOOL, STORY);
        mapTicketHuTypes.put(SMARTCLEANER, STORY);
        mapTicketHuTypes.put(INGESTA, STORY);
        mapTicketHuTypes.put(PROCESAMIENTO, STORY);
        mapTicketHuTypes.put(OPERATIVIZACION, STORY);
        mapTicketHuTypes.put(SCAFFOLDER, STORY);
        mapTicketHuTypes.put(SPARKCOMPACTOR, STORY);
        mapTicketHuTypes.put(JSON_GLOBAL, STORY);
        mapTicketHuTypes.put(TERADATA, STORY);

        TICKET_HU_TYPES = Collections.unmodifiableMap(mapTicketHuTypes);

        Map<String, List<String>> mapVoBoByDevTypes = new HashMap<>();
        mapVoBoByDevTypes.put(MALLAS, new ArrayList<>(List.of("PO","AT","DEV")));
        mapVoBoByDevTypes.put(SCAFFOLDER, new ArrayList<>(List.of("PO")));

        VOBO_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapVoBoByDevTypes);

        Map<String, Map<String, Object>> mapCriteriaByDevTypes = new HashMap<>();

        mapCriteriaByDevTypes.put(MALLAS, Map.of(TEXTO, ACCEPTANCE_CRITERIA_MESH));
        mapCriteriaByDevTypes.put(HOST, Map.of(TEXTO, ACCEPTANCE_CRITERIA_MESH));

        List<String> devTypesWithCommonText = List.of(PRS,
                HAMMURABI, MIGRATIONTOOL, SMARTCLEANER,
                INGESTA, PROCESAMIENTO, OPERATIVIZACION,
                SCAFFOLDER, SPARKCOMPACTOR, JSON_GLOBAL,
                TERADATA, PRODUCTIVIZACION
        );

        for (String devType : devTypesWithCommonText) {
            mapCriteriaByDevTypes.put(devType, Map.of(TEXTO, ACCEPTANCE_CRITERIA_COMMON_TEXT));
        }

        CRITERIA_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapCriteriaByDevTypes);

        Map<String, List<String>> mapSubtasksByDevTypes = new HashMap<>();
        mapSubtasksByDevTypes.put(MALLAS, new ArrayList<>(List.of(P110_AT, C204_PO, C204_QA, VB_DEV, VB_ADA)));
        mapSubtasksByDevTypes.put(PRS, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(PRODUCTIVIZACION, new ArrayList<>(List.of("[VB][PO]", "[VB][QA]", VB_ADA)));
        mapSubtasksByDevTypes.put(HOST, new ArrayList<>(List.of(P110_AT, C204_PO, C204_QA, P110_GC)));
        mapSubtasksByDevTypes.put(HAMMURABI, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(MIGRATIONTOOL, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(SMARTCLEANER, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(INGESTA, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(PROCESAMIENTO, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(OPERATIVIZACION, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(SCAFFOLDER, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(SPARKCOMPACTOR, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(JSON_GLOBAL, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));
        mapSubtasksByDevTypes.put(TERADATA, new ArrayList<>(List.of(C204_PO, C204_QA, VB_ADA)));

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
                STATUS, new ArrayList<>(List.of(ACCEPTED, READY_TO_VERIFY)),
                ITEMS, new ArrayList<>(List.of(P110_AT,"[VB][AT]","[C204][AT]")),
                "rol", new ArrayList<>(List.of("1","2","3"))
                )));
        mapSubtasksTipoOwner.put("po", new HashMap<>(Map.of(
                "label", "PO",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                STATUS, new ArrayList<>(List.of(ACCEPTED, READY_TO_VERIFY)),
                ITEMS, new ArrayList<>(List.of(C204_PO,"[VB][PO]")),
                "rol", new ArrayList<>(List.of("5","8"))
                )));
        mapSubtasksTipoOwner.put("so", new HashMap<>(Map.of(
                "label", "SO",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                STATUS, new ArrayList<>(List.of(ACCEPTED, READY_TO_VERIFY)),
                ITEMS, new ArrayList<>(List.of(VB_SO)),
                "rol", new ArrayList<>(List.of("9","10"))
                )));
        mapSubtasksTipoOwner.put("km", new HashMap<>(Map.of(
                "label", "KM",
                "validateEmailFromLideres", true,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", true,
                "advertenciaEstadoInicial", false,
                STATUS, new ArrayList<>(List.of(ACCEPTED, READY_TO_VERIFY)),
                ITEMS, new ArrayList<>(List.of(VB_KM)),
                "rol", new ArrayList<>(List.of("13","14"))
                )));
        mapSubtasksTipoOwner.put("gc", new HashMap<>(Map.of(
                "label", "GC",
                "validateEmailFromLideres", false,
                "validateEmailContractor", true,
                "advertenciaReadyToVerify", false,
                "advertenciaEstadoInicial", true,
                STATUS, new ArrayList<>(List.of(ACCEPTED, READY_TO_VERIFY)),
                ITEMS, new ArrayList<>(List.of(P110_GC))
                )));
        mapSubtasksTipoOwner.put("qa", new HashMap<>(Map.of(
                "label", "QA",
                "validateEmailFromLideres", false,
                "validateEmailContractor", false,
                "advertenciaReadyToVerify", false,
                "advertenciaEstadoInicial", true,
                STATUS, new ArrayList<>(List.of(READY)),
                ITEMS, new ArrayList<>(List.of(C204_QA, "[VB][QA]"))
                )));
    SUBTASKS_TYPE_OWNER = Collections.unmodifiableMap(mapSubtasksTipoOwner);

    }
}

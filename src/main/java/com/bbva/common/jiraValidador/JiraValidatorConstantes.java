package com.bbva.common.jiraValidador;

import java.util.*;

public class JiraValidatorConstantes {
    public static final Map<String, List<String>> DEVELOPS_TYPES;
    public static final Map<String, List<String>> ATTACHS_BY_DEVELOP_TYPES;
    public static final Map<String, List<String>> VOBO_BY_DEVELOP_TYPES;

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
        VOBO_BY_DEVELOP_TYPES = Collections.unmodifiableMap(mapAttachByDevTypes);
    }


}

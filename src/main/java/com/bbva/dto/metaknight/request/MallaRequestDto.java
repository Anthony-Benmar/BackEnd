package com.bbva.dto.metaknight.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MallaRequestDto {

    // Datos del usuario y proyecto - YA SE TIENEN
    private String creationUser;        // XP del usuario
    private String parentFolder;        // Folder padre en Control-M
    private String uuaa;                // UUAA master
    private String namespace;           // Namespace del proyecto
    private String teamEmail;           // Email del equipo

    // Configuración de transferencia - Se traen del schema
    private String transferJobname;     // Nombre del job de transferencia
    private String transferSourceName;  // Nombre de la fuente de datos
    private String transferTimeFrom;    // Hora de inicio de transferencia
    private String transferName;        // Nombre de la transferencia
    private String transferUuaaRaw;     // UUAA raw para transferencia

    // Configuración de copia -- REVISAR
    private String copyJobname;         // Nombre del job de copia
    private String copyUuaaRaw;         // UUAA raw para copia

    // Configuración de FileWatcher
    private String fwJobname;           // Nombre del job de FileWatcher
    private String createNums;          // Números de configuración (por defecto: "1 30 10 3 5")

    // Jobs de Hammurabi Staging
    private String hmmStgJobname;       // Nombre del job Hammurabi Staging
    private String hmmStgJobid;         // ID del job Hammurabi Staging

    // Jobs de Kirby Raw
    private String krbRawJobname;       // Nombre del job Kirby Raw
    private String krbRawJobid;         // ID del job Kirby Raw
    private String rawSourceName;       // Nombre de la fuente raw

    // Jobs de Hammurabi Raw
    private String hmmRawJobname;       // Nombre del job Hammurabi Raw
    private String hmmRawJobid;         // ID del job Hammurabi Raw

    // Jobs de Kirby Master
    private String krbMasterJobname;    // Nombre del job Kirby Master
    private String masterSourceName;    // Nombre de la fuente master
    private String krbMasterJobid;      // ID del job Kirby Master

    // Jobs de Hammurabi Master
    private String hmmMasterJobname;    // Nombre del job Hammurabi Master
    private String hmmMasterJobid;      // ID del job Hammurabi Master

    // Jobs de limpieza
    private String erase1Jobname;       // Nombre del job de borrado 1
    private String erase2Jobname;       // Nombre del job de borrado 2

    // Metadatos de creación
    private String creationDate;        // Fecha de creación (YYYYMMDD)
    private String creationTime;        // Hora de creación (HHMMSS)

    // Campos calculados
    private String uuaaLowercase;       // UUAA en minúsculas
}
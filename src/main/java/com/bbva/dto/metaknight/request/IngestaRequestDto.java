package com.bbva.dto.metaknight.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngestaRequestDto {
    private String frecuencia;
    private String uuaaMaster;
    private String tipoArchivo;
    private String delimitador;
    private String particiones;
    private boolean tieneL1T;
    private boolean tieneCompactacion;
    private String sdatool;
    private String proyecto;
    private String sm;
    private String po;
    private String nombreDev;
    private String registroDev;
    private String schemaRawBase64;
    private String schemaMasterBase64;

    // Para trabajar con Jira
    private String username;
    private String token;
    private String ticketJira;
}

package com.bbva.entities.source;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleFuenteTdsEntity {
    @SerializedName("tipo_origen")
    private String tipo_origen;
    @SerializedName("comentarios_tds")
    private String comentarios_tds;
    @SerializedName("periodicidad")
    private String periodicidad;
    @SerializedName("detalla_periodicidad")
    private String detalla_periodicidad;
    @SerializedName("tipo_descarga_datio")
    private String tipo_descarga_datio;
    @SerializedName("informacion_master")
    private String informacion_master;
    @SerializedName("campos")
    private String campos;
    @SerializedName("uuaa_raw")
    private String uuaa_raw;
    @SerializedName("nombre_origen")
    private String nombre_origen;
}
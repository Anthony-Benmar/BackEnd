package com.bbva.entities.source;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class InformacionGeneralEntity {
    @SerializedName("id")
    private String id;
    @SerializedName("ruta_master")
    private String ruta_master;
    @SerializedName("descripcion_tds")
    private String descripcion_tds;
    @SerializedName("fuente_tds")
    private String fuente_tds;
    @SerializedName("estado_tds")
    private String estado_tds;
    @SerializedName("reemplazo")
    private List<ReemplazoEntity> reemplazo;
    @SerializedName("dictamen_url")
    private String dictamen_url;
    @SerializedName("uuaa_master")
    private String uuaa_master;
    @SerializedName("estado_deuda")
    private String estado_deuda;
    @SerializedName("tipologia")
    private String tipologia;
    @SerializedName("owner_global_model")
    private String owner_global_model;
}
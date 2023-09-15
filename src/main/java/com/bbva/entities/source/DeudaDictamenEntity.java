package com.bbva.entities.source;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeudaDictamenEntity {
    @SerializedName("estado_fuente")
    private String estadoFuente;
    @SerializedName("comentarios_deuda")
    private String comentariosDeuda;
    @SerializedName("nivel_deuda")
    private String nivelDeuda;
}

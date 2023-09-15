package com.bbva.entities.buc;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldEntity {
    @SerializedName("id")
    private String id;
    @SerializedName("id_fuente")
    private String id_fuente;
    @SerializedName("fuente_dictaminada")
    private String fuente_dictaminada;
    @SerializedName("campo_dictaminado")
    private String campo_dictaminado;
}

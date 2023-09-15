package com.bbva.entities.external;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoFolioEntity {
    @SerializedName("id")
    private float id;
    @SerializedName("descripcion")
    private String descripcion;
}

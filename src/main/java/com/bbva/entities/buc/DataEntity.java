package com.bbva.entities.buc;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DataEntity {
    @SerializedName("bucmap")
    private BucmapEntity bucmap;
    @SerializedName("resolucion")
    private ResolucionEntity resolucion;
}

package com.bbva.entities.buc;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class ResolucionEntity {
    @SerializedName("estado_resolucion")
    private String estado_resolucion;
    @SerializedName("resolucion")
    private String resolucion;
    @SerializedName("comentario_resolucion")
    private String comentario_resolucion;
    @SerializedName("logica")
    private String logica;
    @SerializedName("fields")
    ArrayList<FieldEntity> fields = new ArrayList<FieldEntity>();
}

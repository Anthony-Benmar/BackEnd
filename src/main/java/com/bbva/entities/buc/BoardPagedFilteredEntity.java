package com.bbva.entities.buc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BoardPagedFilteredEntity {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("folioId")
    @Expose
    public Integer folioId;
    @SerializedName("folioCodigo")
    @Expose
    public String folioCodigo;
    @SerializedName("proyectoId")
    @Expose
    public Integer proyectoId;
    @SerializedName("sdatool")
    @Expose
    public String sdatool;
    @SerializedName("proyectoNombre")
    @Expose
    public String proyectoNombre;
    @SerializedName("casoUsoId")
    @Expose
    public Integer casoUsoId;
    @SerializedName("casoUsoCodigo")
    @Expose
    public String casoUsoCodigo;
    @SerializedName("casoUsoNombre")
    @Expose
    public String casoUsoNombre;
    @SerializedName("tipoEstadoId")
    @Expose
    public Integer tipoEstadoId;
    @SerializedName("tipoEstadoNombre")
    @Expose
    public String tipoEstadoNombre;
    @SerializedName("bucFuenteCampo")
    @Expose
    public List<BucFuenteCampo> bucFuenteCampo = null;
}

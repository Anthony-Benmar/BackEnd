package com.bbva.entities.external;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class GobiernoEntity {
    @SerializedName("sdatoolId")
    private String sdatoolId;
    @SerializedName("uc_nombre_fuente")
    private String uc_nombre_fuente;
    @SerializedName("uc_descripcion_fuente")
    private String uc_descripcion_fuente;
    @SerializedName("depth_numero_meses")
    private String depth_numero_meses;
    @SerializedName("uc_tipo_frecuencia")
    private String uc_tipo_frecuencia;
    @SerializedName("codigo_folio")
    private String codigo_folio;
    @SerializedName("fecha_folio_registrado")
    private String fecha_folio_registrado;
    @SerializedName("estado_folio")
    EstadoFolioEntity estado_folio;
    @SerializedName("tipo_folio")
    TipoFolioEntity tipo_folio;
    @SerializedName("inicio_sprint")
    private String inicio_sprint;
    @SerializedName("analyst_ca_id")
    private String analyst_ca_id;
    @SerializedName("resolution_source_type")
    ResolutionSourceTypeEntity Resolution_source_type;
    @SerializedName("resolution_source_date")
    private String resolution_source_date;
    @SerializedName("id_folio_reutilizado")
    private String id_folio_reutilizado = null;
    @SerializedName("resolution_comment_desc")
    private String resolution_comment_desc;
    @SerializedName("descripcion_historyingest")
    private String descripcion_historyingest;
    @SerializedName("descripcion_historycomment")
    private String descripcion_historycomment;
    @SerializedName("debtstatus_tipo_fuente")
    DebtStatusTipoFuenteEntity Debtstatus_tipo_fuente;
    @SerializedName("debt_comentario_descripcion_fuente")
    private String debt_comentario_descripcion_fuente;
    @SerializedName("tipo_origen_fuente")
    TipoOrigenFuenteEntity tipo_origenFuente;
    @SerializedName("id_fuente_antigua")
    private String id_fuente_antigua;
    @SerializedName("nombre_fuente")
    private String nombre_fuente;
    @SerializedName("descripcion_fuente")
    private String descripcion_fuente;
    @SerializedName("descarga_tipo_datio")
    DescargaTipoDatioEntity descarga_tipo_datio;
    @SerializedName("numero_campos")
    private float numero_campos;
    @SerializedName("raw_func_map_id")
    private String raw_func_map_id = null;
    @SerializedName("master_func_map_id")
    private String master_func_map_id;
    @SerializedName("tipo_frecuencia")
    TipoFrecuenciaEntity tipo_frecuenciaObject;
    @SerializedName("descripcion_nivel_1")
    private String descripcion_nivel_1;
    @SerializedName("descripcion_nivel_2")
    private String descripcion_nivel_2;
    @SerializedName("sourceStructure")
    ArrayList<Object> sourceStructure = new ArrayList < Object > ();
}
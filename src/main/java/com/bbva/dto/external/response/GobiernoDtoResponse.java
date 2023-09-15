package com.bbva.dto.external.response;

import com.bbva.entities.external.*;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class GobiernoDtoResponse {
    private String sdatoolId;
    private String uc_nombre_fuente;
    private String uc_descripcion_fuente;
    private String depth_numero_meses;
    private String uc_tipo_frecuencia;
    private String codigo_folio;
    private String fecha_folio_registrado;
    private EstadoFolioEntity estado_folio;
    private TipoFolioEntity tipo_folio;
    private String inicio_sprint;
    private String analyst_ca_id;
    private ResolutionSourceTypeEntity resolution_source_type;
    private String resolution_source_date;
    private String id_folio_reutilizado = null;
    private String resolution_comment_desc;
    private String descripcion_historyingest;
    private String descripcion_historycomment;
    DebtStatusTipoFuenteEntity Debtstatus_tipo_fuente;
    private String debt_comentario_descripcion_fuente;
    TipoOrigenFuenteEntity tipo_origenFuente;
    private String id_fuente_antigua;
    private String nombre_fuente;
    private String descripcion_fuente;
    DescargaTipoDatioEntity descarga_tipo_datio;
    private float numero_campos;
    private String raw_func_map_id = null;
    private String master_func_map_id;
    TipoFrecuenciaEntity tipo_frecuenciaObject;
    private String descripcion_nivel_1;
    private String descripcion_nivel_2;
    ArrayList<Object> sourceStructure = new ArrayList < Object > ();
}

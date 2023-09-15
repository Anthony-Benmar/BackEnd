package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.dto.external.request.GobiernoDtoRequest;
import com.bbva.dto.external.response.GobiernoDtoResponse;
import com.bbva.entities.external.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class ExternalDao {
    public GobiernoDtoResponse gobierno(GobiernoDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            GobiernoDtoResponse response = new GobiernoDtoResponse();
            GobiernoEntity gobiernoEntity=new GobiernoEntity();
            gobiernoEntity.setCodigo_folio("SDATOOL-A1018");
            gobiernoEntity.setSdatoolId("MCDTCON / PEBP.MPFD.FIX.MPDQCON1.D1%%ODATE");

            gobiernoEntity.setUc_tipo_frecuencia("No Requiere");
            gobiernoEntity.setSdatoolId("SDATOOL-A1018");
            gobiernoEntity.setUc_nombre_fuente("MCDTCON / PEBP.MPFD.FIX.MPDQCON1.D1%%ODATE");
            gobiernoEntity.setUc_descripcion_fuente("Información Maestra de Tarjetas");
            gobiernoEntity.setDepth_numero_meses("No Requiere");
            gobiernoEntity.setCodigo_folio("CA000021");
            gobiernoEntity.setFecha_folio_registrado("12/01/2018");
            EstadoFolioEntity estadoFolioEntity=new EstadoFolioEntity();
            estadoFolioEntity.setId(3);
            estadoFolioEntity.setDescripcion("Finalizada");
            gobiernoEntity.setEstado_folio(estadoFolioEntity);
            TipoFolioEntity tipo_folio=new TipoFolioEntity();
            tipo_folio.setId(1);
            tipo_folio.setDescripcion("Entrada");
            gobiernoEntity.setInicio_sprint("Q1-2018-SP1");
            gobiernoEntity.setAnalyst_ca_id("Core Assurance");
            ResolutionSourceTypeEntity resolution_source_type=new ResolutionSourceTypeEntity();
            resolution_source_type.setId(1);
            resolution_source_type.setDescripcion("Se ingesta tabla dictaminada");
            gobiernoEntity.setResolution_source_date("01/12/2018");
            gobiernoEntity.setId_folio_reutilizado(null);
            gobiernoEntity.setResolution_comment_desc("Fuente Ingestada por Huki");
            gobiernoEntity.setDescripcion_historyingest("No Requiere");
            gobiernoEntity.setDescripcion_historycomment("");
            DebtStatusTipoFuenteEntity debtstatus_tipo_fuente=new DebtStatusTipoFuenteEntity();
            debtstatus_tipo_fuente.setId(3);
            debtstatus_tipo_fuente.setDescripcion("Con Deuda");
            gobiernoEntity.setDebt_comentario_descripcion_fuente("03-11-2020 [BGA]: No tiene dictamen\r");
            TipoOrigenFuenteEntity tipo_origenFuente=new TipoOrigenFuenteEntity();
            tipo_origenFuente.setId(1);
            tipo_origenFuente.setDescripcion("Host");
            gobiernoEntity.setId_fuente_antigua("179.1");
            gobiernoEntity.setNombre_fuente("PEBP.MPFD.FIX.MPDQCON1.D1%%ODATE");
            gobiernoEntity.setDescripcion_fuente("Tabla de Contratos de medios de Pago");
            DescargaTipoDatioEntity descarga_tipo_datio=new DescargaTipoDatioEntity();
            descarga_tipo_datio.setId(1);
            descarga_tipo_datio.setDescripcion("Maestra - Total");
            gobiernoEntity.setNumero_campos(103);
            gobiernoEntity.setRaw_func_map_id(null);
            gobiernoEntity.setMaster_func_map_id("PDCO");
            TipoFrecuenciaEntity tipo_frecuenciaObject=new TipoFrecuenciaEntity();
            tipo_frecuenciaObject.setId(1);
            tipo_frecuenciaObject.setDescripcion("Diaria");
            gobiernoEntity.setDescripcion_nivel_1("Básicos Comunes");
            gobiernoEntity.setDescripcion_nivel_2("Arquitectura");

            response.setSdatoolId(gobiernoEntity.getSdatoolId());
            response.setUc_nombre_fuente(gobiernoEntity.getUc_nombre_fuente());
            response.setUc_descripcion_fuente(gobiernoEntity.getUc_descripcion_fuente());
            response.setUc_tipo_frecuencia(gobiernoEntity.getUc_tipo_frecuencia());
            response.setSdatoolId(gobiernoEntity.getSdatoolId());
            response.setCodigo_folio(gobiernoEntity.getCodigo_folio());
            response.setFecha_folio_registrado(gobiernoEntity.getFecha_folio_registrado());
            response.setEstado_folio(gobiernoEntity.getEstado_folio());
            response.setTipo_folio(gobiernoEntity.getTipo_folio());
            response.setInicio_sprint(gobiernoEntity.getInicio_sprint());
            response.setAnalyst_ca_id(gobiernoEntity.getAnalyst_ca_id());
            response.setResolution_source_date(gobiernoEntity.getResolution_source_date());
            response.setId_folio_reutilizado(gobiernoEntity.getId_folio_reutilizado());
            response.setResolution_comment_desc(gobiernoEntity.getResolution_comment_desc());
            response.setDescripcion_historyingest(gobiernoEntity.getDescripcion_historyingest());
            response.setDescripcion_historycomment(gobiernoEntity.getDescripcion_historycomment());
            response.setDebtstatus_tipo_fuente(gobiernoEntity.getDebtstatus_tipo_fuente());
            response.setDebt_comentario_descripcion_fuente(gobiernoEntity.getDebt_comentario_descripcion_fuente());
            response.setTipo_origenFuente(gobiernoEntity.getTipo_origenFuente());
            response.setId_fuente_antigua(gobiernoEntity.getId_fuente_antigua());
            response.setNombre_fuente(gobiernoEntity.getNombre_fuente());
            response.setDescripcion_fuente(gobiernoEntity.getDescripcion_fuente());
            response.setDescarga_tipo_datio(gobiernoEntity.getDescarga_tipo_datio());
            response.setNumero_campos(gobiernoEntity.getNumero_campos());
            response.setRaw_func_map_id(gobiernoEntity.getRaw_func_map_id());
            response.setMaster_func_map_id(gobiernoEntity.getMaster_func_map_id());
            response.setTipo_frecuenciaObject(gobiernoEntity.getTipo_frecuenciaObject());
            return response;
        }
    }
}
package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceMapper;
import com.bbva.dto.source.request.ReadOnlyDtoRequest;
import com.bbva.dto.source.response.*;
import com.bbva.dto.source.request.PaginationDtoRequest;
import com.bbva.entities.coreassurance.SourceEntity;
import com.bbva.entities.source.*;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SourceDao {
    private static final Logger log = Logger.getLogger(SourceDao.class.getName());
    public PaginationResponse pagination(PaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceMapper mapper = session.getMapper(SourceMapper.class);
            Integer recordsCount = 0;
            Integer pagesAmount = 0;
            List<SourcePagedFilteredEntity> list =  mapper.pagination(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getId_fuente(),
                    dto.getNombre_fuente(),
                    dto.getUuaa_aplicativo(),
                    dto.getEstado(),
                    dto.getOrigen(),
                    dto.getEstado_deuda(),
                    dto.getUuaa_master(),
                    dto.getDescripcion_tds(),
                    dto.getTabla_master(),
                    dto.getPropietario_global()
            );

            PaginationResponse paginationResponse  =  new PaginationResponse();
            List<PaginationDataDtoResponse> listDataResponse   = new ArrayList<>();

            recordsCount = (list.size() > 0) ? Integer.parseInt(list.get(0).getRecords_count()) : 0;
            pagesAmount = (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue());

            list.forEach(object -> {
                var validation = object == null? new SourcePagedFilteredEntity():object;
                PaginationDataDtoResponse paginationDataDtoResponse=new PaginationDataDtoResponse();
                paginationDataDtoResponse.setId(object.getOld_source_id()==null?0:Float.parseFloat(object.getOld_source_id()));
                paginationDataDtoResponse.setSourceId(object.getSource_id()==null?0:Integer.parseInt(object.getSource_id()));
                paginationDataDtoResponse.setFuente(validation.getSource_name());
                paginationDataDtoResponse.setDescripcion(validation.getSource_desc());
                paginationDataDtoResponse.setEstado_id(object.getStatus_type()==null?0:Integer.parseInt(object.getStatus_type().equalsIgnoreCase("")?"0":object.getStatus_type()));
                paginationDataDtoResponse.setEstado(object.getEstatus()==null?"":object.getEstatus());
                paginationDataDtoResponse.setTipo_origen_id(object.getOrigin_source_type()==null?0:Integer.parseInt(object.getOrigin_source_type()));
                paginationDataDtoResponse.setTipo_origen(validation.getOrigin_source());
                paginationDataDtoResponse.setUuaa_master_id(object.getMaster_func_map_id()==null?0:Integer.parseInt(object.getMaster_func_map_id()));
                paginationDataDtoResponse.setUuaa_master(validation.getMaster_uuaa());
                paginationDataDtoResponse.setDeuda_id(object.getDebtstatus_source_type()==null?0:Integer.parseInt(object.getDebtstatus_source_type()));
                paginationDataDtoResponse.setDeuda(validation.getDebtstatus_source());
                listDataResponse.add(paginationDataDtoResponse);
            });
            paginationResponse.setCount(recordsCount);
            paginationResponse.setPages_amount(pagesAmount);
            paginationResponse.setData(listDataResponse);
            log.info(JSONUtils.convertFromObjectToJson(paginationResponse.getData()));
            return paginationResponse;
        }
    }

    public ReadOnlyDtoResponse readOnly(ReadOnlyDtoRequest dto) {
        DecimalFormat formatoDecimal = new DecimalFormat("#.#");
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceMapper mapper = session.getMapper(SourceMapper.class);

            SourceEntity objeto=mapper.getReadOnly(dto.getSourceId());

            var validation = objeto == null? new SourceEntity():objeto;

            InformacionGeneralEntity informacionGeneralEntity = new InformacionGeneralEntity();
            informacionGeneralEntity.setId(validation.getOld_source_id());
            informacionGeneralEntity.setRuta_master(validation.getMaster_path_desc());
            informacionGeneralEntity.setDescripcion_tds(validation.getSource_desc());
            informacionGeneralEntity.setFuente_tds(validation.getSource_name());
            informacionGeneralEntity.setEstado_tds(validation.getStatus());

            List<ReemplazoEntity> sourceIdReemplazoListDecimal=new ArrayList<>();

            ReemplazoEntity reemplazoEntity=new ReemplazoEntity();
            reemplazoEntity.setId(validation.getSource_id());
            reemplazoEntity.setId_reemplazado(objeto.getReplacement_source_desc().equalsIgnoreCase("")?"":formatoDecimal.format(Float.parseFloat(validation.getReplacement_source_desc())));
            sourceIdReemplazoListDecimal.add(reemplazoEntity);

            informacionGeneralEntity.setReemplazo(objeto.getReplacement_source_desc().equalsIgnoreCase("")?new ArrayList():sourceIdReemplazoListDecimal);
            informacionGeneralEntity.setDictamen_url(validation.getFiles_source_link());
            informacionGeneralEntity.setUuaa_master(validation.getUuaa_id());
            informacionGeneralEntity.setEstado_deuda(validation.getDebtstatus_source());
            informacionGeneralEntity.setTipologia(validation.getTipology());
            informacionGeneralEntity.setOwner_global_model(validation.getOwner_global());

            DetalleFuenteTdsEntity detalleFuenteTdsEntity=new DetalleFuenteTdsEntity();
            detalleFuenteTdsEntity.setTipo_origen(validation.getOrigin_source());
            detalleFuenteTdsEntity.setComentarios_tds(validation.getComment_source_desc());
            detalleFuenteTdsEntity.setPeriodicidad(validation.getFrequency());
            detalleFuenteTdsEntity.setDetalla_periodicidad(validation.getFreq_source_desc());
            detalleFuenteTdsEntity.setTipo_descarga_datio(validation.getDatio_downloand());
            detalleFuenteTdsEntity.setInformacion_master(validation.getDownloand_desc());
            detalleFuenteTdsEntity.setCampos(validation.getFields_number());
            detalleFuenteTdsEntity.setUuaa_raw(validation.getUuaa_raw());
            detalleFuenteTdsEntity.setNombre_origen(validation.getOrigin_source_name());

            ReadOnlyDtoResponse response = new ReadOnlyDtoResponse();

            InformacionGeneralResponse informacionGeneralResponse=new InformacionGeneralResponse();
            DetalleFuenteTdsResponse detalleFuenteResponse = new DetalleFuenteTdsResponse();
            MapaFuncionalResponse mapaFuncionalResponse=new MapaFuncionalResponse();
            DeudaDictamenResponse deudaDictamenResponse=new DeudaDictamenResponse();

            informacionGeneralResponse.setId(formatoDecimal.format(Float.parseFloat(informacionGeneralEntity.getId())));
            informacionGeneralResponse.setRuta_master(informacionGeneralEntity.getRuta_master());
            informacionGeneralResponse.setDescripcion_tds(informacionGeneralEntity.getDescripcion_tds());
            informacionGeneralResponse.setFuente_tds(informacionGeneralEntity.getFuente_tds());
            informacionGeneralResponse.setEstado_tds(informacionGeneralEntity.getEstado_tds());
            informacionGeneralResponse.setReemplazo(informacionGeneralEntity.getReemplazo());
            informacionGeneralResponse.setDictamen_url(informacionGeneralEntity.getDictamen_url());
            informacionGeneralResponse.setUuaa_master(informacionGeneralEntity.getUuaa_master());
            informacionGeneralResponse.setEstado_deuda(informacionGeneralEntity.getEstado_deuda());
            informacionGeneralResponse.setTipologia(informacionGeneralEntity.getTipologia());
            informacionGeneralResponse.setOwner_global_model(informacionGeneralEntity.getOwner_global_model());

            detalleFuenteResponse.setTipo_origen(detalleFuenteTdsEntity.getTipo_origen());
            detalleFuenteResponse.setComentarios_tds(detalleFuenteTdsEntity.getComentarios_tds());
            detalleFuenteResponse.setPeriodicidad(detalleFuenteTdsEntity.getPeriodicidad());
            detalleFuenteResponse.setDetalla_periodicidad(detalleFuenteTdsEntity.getDetalla_periodicidad());
            detalleFuenteResponse.setTipo_descarga_datio(detalleFuenteTdsEntity.getTipo_descarga_datio());
            detalleFuenteResponse.setInformacion_master(detalleFuenteTdsEntity.getInformacion_master());
            detalleFuenteResponse.setCampos(detalleFuenteTdsEntity.getCampos());
            detalleFuenteResponse.setUuaa_raw(detalleFuenteTdsEntity.getUuaa_raw());
            detalleFuenteResponse.setNombre_origen(detalleFuenteTdsEntity.getNombre_origen());

            MapaFuncionalEntity mapaFuncionalEntity=new MapaFuncionalEntity();
            mapaFuncionalEntity.setDescripcion(validation.getUuaa_desc());
            mapaFuncionalEntity.setUuaa_master(validation.getUuaa_master());
            mapaFuncionalEntity.setNivel_1(validation.getNivel1_desc());
            mapaFuncionalEntity.setNivel_2(validation.getNivel2_desc());
            mapaFuncionalEntity.setNivel_3(validation.getNivel3_desc());
            mapaFuncionalResponse.setUuaa_master(mapaFuncionalEntity.getUuaa_master());
            mapaFuncionalResponse.setDescripcion(mapaFuncionalEntity.getDescripcion());
            mapaFuncionalResponse.setNivel_1(mapaFuncionalEntity.getNivel_1());
            mapaFuncionalResponse.setNivel_2(mapaFuncionalEntity.getNivel_2());
            mapaFuncionalResponse.setNivel_3(mapaFuncionalEntity.getNivel_3());

            DeudaDictamenEntity deudaDictamenEntity=new DeudaDictamenEntity();
            deudaDictamenEntity.setNivelDeuda(validation.getDebtstatus_source());
            deudaDictamenEntity.setComentariosDeuda(validation.getDebt_comment_source_desc());
            deudaDictamenEntity.setEstadoFuente(validation.getDebtstatus_source());
            deudaDictamenResponse.setNivel_deuda(deudaDictamenEntity.getNivelDeuda());
            deudaDictamenResponse.setComentarios_deuda(deudaDictamenEntity.getComentariosDeuda());
            deudaDictamenResponse.setEstado_fuente(deudaDictamenEntity.getEstadoFuente());

            response.setInformacionGeneral(informacionGeneralResponse);
            response.setDetalleFuenteTds(detalleFuenteResponse);
            response.setMapaFuncional(mapaFuncionalResponse);
            response.setDeudaDictamen(deudaDictamenResponse);
            log.info(JSONUtils.convertFromObjectToJson(response));
            return response;
        }
    }
}
package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BuiMapper;
import com.bbva.dto.bui.request.PaginationDtoRequest;
import com.bbva.dto.bui.request.ReadOnlyDtoRequest;
import com.bbva.dto.bui.response.*;
import com.bbva.entities.bui.BuiEntity;
import com.bbva.entities.bui.BuiPagedFilteredEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class BuiDao {
    private static final Logger log = Logger.getLogger(BuiDao.class.getName());
    public PaginationResponse pagination(PaginationDtoRequest dto) {
        DecimalFormat formatoDecimal = new DecimalFormat("#.#");
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BuiMapper mapper = session.getMapper(BuiMapper.class);
            Integer recordsCount = 0;
            Integer pagesAmount = 0;

            List<BuiPagedFilteredEntity> buiList =  mapper.pagination(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getSdatool(),
                    dto.getProposed_table(),
                    dto.getAnalyst_in_charge(),
                    dto.getFolio_code(),
                    dto.getId_fuente(),
                    dto.getTipo(),
                    dto.getEstado()
            );

            recordsCount = (buiList.size() > 0) ? Integer.parseInt(buiList.get(0).getRecords_count()) : 0;
            pagesAmount = (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue());

            PaginationResponse paginationResponse  =  new PaginationResponse();
            List<PaginationDataResponse> listDataResponse   = new ArrayList<PaginationDataResponse>();

            buiList.forEach(objeto -> {
                var validation = objeto == null? new BuiPagedFilteredEntity():objeto;
                PaginationDataResponse objectPaginationData = new PaginationDataResponse();
                objectPaginationData.setId(Integer.parseInt(validation.getBui_id()));
                objectPaginationData.setSourceId(objeto.getSource_id() == null ? 0 : Integer.parseInt(objeto.getSource_id()));
                objectPaginationData.setFuenteAnteriorId(objeto.getOld_source_id() == null ? "" : formatoDecimal.format(Float.parseFloat(objeto.getOld_source_id())));
                objectPaginationData.setSourceName(validation.getUc_source_name());
                objectPaginationData.setFolioId(Integer.parseInt(validation.getFolio_id()));
                objectPaginationData.setFolioCode(validation.getFolio_code());
                objectPaginationData.setProjectId(Integer.parseInt(validation.getProject_id()));
                objectPaginationData.setSdatool(validation.getSdatool_id());
                objectPaginationData.setProjectName(validation.getProject_name());
                objectPaginationData.setResolutionSourceTypeId(objeto.getResolution_source_type() == null ? 0 : Integer.parseInt(objeto.getResolution_source_type()));
                objectPaginationData.setResolutionSourceType(validation.getResolution_source_type_desc());
                objectPaginationData.setIngestSourceTypeId(objeto.getIngest_source_type() == null ? 0 : Integer.parseInt(objeto.getIngest_source_type()));
                objectPaginationData.setIngestSourceType(validation.getIngest_source_type_desc());
                objectPaginationData.setStatusFolioTypeId(objeto.getStatus_folio_type() == null ? 0 : Integer.parseInt(objeto.getStatus_folio_type()));
                objectPaginationData.setStatusFolioType(validation.getStatus_folio_type_desc());
                listDataResponse.add(objectPaginationData);
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
            BuiMapper mapper = session.getMapper(BuiMapper.class);
            ReadOnlyDtoResponse response = new ReadOnlyDtoResponse();
            try {
                List<BuiEntity> buiListObjeto = mapper.getReadOnly(dto.getBuiId());

                BuiEntity object = buiListObjeto.stream()
                        .filter(distinctByKey(l-> l.getFolio_code()))
                        .findAny().orElse(null);

                ReadOnlyRoadmapDtoResponse roadmap = new ReadOnlyRoadmapDtoResponse();
                ReadOnlyDictamenResponse dictamenResponse = new ReadOnlyDictamenResponse();
                ReadOnlyTdsDictaminadaResponse tdsDictaminadaResponse = new ReadOnlyTdsDictaminadaResponse();
                ReadOnlyIngestaResponse ingestaResponse = new ReadOnlyIngestaResponse();

                var validation = object == null? new BuiEntity():object;

                roadmap.setFolio(validation.getFolio_code());
                roadmap.setProject(validation.getProject_name());
                roadmap.setSdatool(validation.getSdatool_id());
                roadmap.setTabla(validation.getUc_source_name());
                roadmap.setHistoria(validation.getDepth_month_number());
                roadmap.setPeriodicidad(validation.getUc_frequency_type());
                roadmap.setFechaRegistro(validation.getRegistered_folio_date());
                roadmap.setDescTabla(validation.getUc_source_desc());
                dictamenResponse.setEstatus(validation.getStatus_dictum_type_desc());
                dictamenResponse.setAnalista_proyecto(validation.getAnalyst_project_id());
                dictamenResponse.setAnalista_ca(validation.getAnalyst_ca_id());
                dictamenResponse.setResolucion(validation.getResolution_source_type_desc());
                dictamenResponse.setFolio_reuso(validation.getReused_folio_code());
                dictamenResponse.setFecha_sprint_cierre(validation.getEnd_date_sprint());
                dictamenResponse.setComentario_resolucion(validation.getResolution_comment_desc());
                dictamenResponse.setTipo(validation.getFolio_type_desc());
                tdsDictaminadaResponse.setId_tds(formatoDecimal.format(Float.parseFloat(validation.getOld_source_id())));
                tdsDictaminadaResponse.setFuenteId(validation.getSource_id());
                tdsDictaminadaResponse.setFuente_tds(validation.getSource_name());
                tdsDictaminadaResponse.setHistoria_ingestar(validation.getHistoryingest_desc());
                tdsDictaminadaResponse.setComentario(validation.getHistorycomment_desc());
                tdsDictaminadaResponse.setEstado_tds(validation.getStatus_type_desc());
                tdsDictaminadaResponse.setId_reemplazado(validation.getDebt_comment_source_desc());
                tdsDictaminadaResponse.setEstado_deuda(validation.getDebtstatus_source_type_desc());
                tdsDictaminadaResponse.setComentario_deuda(validation.getDebt_comment_source_desc());
                ingestaResponse.setPersistencia_destino(validation.getPersistence_folio_type_desc());
                ingestaResponse.setDashboard(validation.getIngest_source_type_desc());
                ingestaResponse.setFecha_dashboard(validation.getIngest_source_date());
                ingestaResponse.setComentario_general(validation.getIngest_comment_folio_desc());

                response.setRoadmap(roadmap);
                response.setDictamen(dictamenResponse);
                response.setTdsDictaminada(tdsDictaminadaResponse);
                response.setIngesta(ingestaResponse);
            }catch(Exception e){
            }
            log.info(JSONUtils.convertFromObjectToJson(response));
            return response;
        }
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

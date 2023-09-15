package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BucMapper;
import com.bbva.dto.buc.request.PaginationDtoRequest;
import com.bbva.dto.buc.request.ReadOnlyDtoRequest;
import com.bbva.dto.buc.response.*;
import com.bbva.entities.buc.BucPagedFilteredEntity;
import com.bbva.entities.buc.BucmapEntity;
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
import java.util.stream.Collectors;

public class BucDao {
    private static final Logger log = Logger.getLogger(BucDao.class.getName());

    public PaginationResponse pagination(PaginationDtoRequest dto) {
        DecimalFormat formatoDecimal = new DecimalFormat("#.#");
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BucMapper mapper = session.getMapper(BucMapper.class);
            Integer recordsCount = 0;
            Integer pagesAmount = 0;

            ArrayList<BucPagedFilteredEntity> list = mapper.getPagination(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getFolio_code(),
                    dto.getCodigo_campo(),
                    dto.getNombre_proyecto(),
                    dto.getId_fuente(),
                    dto.getPrioridad(),
                    dto.getEstado_resolucion(),
                    dto.getDescripcion_funcional()
            );

            recordsCount = (list.size() > 0) ? Integer.parseInt(list.get(0).getRecords_count()) : 0;
            pagesAmount = (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue());

            PaginationResponse paginationResponse=new PaginationResponse();
            List<PaginationDataResponse> listDataResponse   = new ArrayList<>();
            list.forEach(object -> {
                var validation = object == null? new BucPagedFilteredEntity():object;
                PaginationDataResponse paginationDataResponse = new PaginationDataResponse();
                paginationDataResponse.setId(Integer.parseInt(validation.getBuc_id()));
                paginationDataResponse.setFolioId(Integer.parseInt(validation.getFolio_id()));
                paginationDataResponse.setFolioCodigo(validation.getFolio_code());
                paginationDataResponse.setProyectoId(Integer.parseInt(validation.getProject_id()));
                paginationDataResponse.setSdatool(validation.getSdatool_id());
                paginationDataResponse.setProyectoNombre(validation.getProject_name());
                paginationDataResponse.setCasoUsoId(Integer.parseInt(validation.getUc_data_id()));
                paginationDataResponse.setCasoUsoCodigo(validation.getUc_data_code());
                paginationDataResponse.setCasoUsoNombre(validation.getUc_data_func_name());
                paginationDataResponse.setTipoEstadoId(Integer.parseInt(validation.getStatus_buc_type()));
                paginationDataResponse.setTipoEstadoNombre(validation.getElement_name());
                listDataResponse.add(paginationDataResponse);
            });

            var listPaginationDataDistinct = listDataResponse
                    .stream().distinct()
                    .collect(Collectors.toList());

            listPaginationDataDistinct.forEach(object -> {
                List<PaginationBucFuenteCampoResponse> listFields = new ArrayList<>();

                var filterById = list
                        .stream().filter(f -> Integer.parseInt(f.getBuc_id()) == object.getId());

                filterById.forEachOrdered(filteredObject -> {
                    var valid = filteredObject == null? new BucPagedFilteredEntity():filteredObject;
                    PaginationBucFuenteCampoResponse objectField = new PaginationBucFuenteCampoResponse();
                    objectField.setBucFieldId(filteredObject.getBuc_fields_id()==null?0:Integer.parseInt(filteredObject.getBuc_fields_id()));
                    objectField.setFuenteId(filteredObject.getOld_source_id()==null?"":formatoDecimal.format(Float.parseFloat(filteredObject.getOld_source_id())));
                    objectField.setFuenteNombre(valid.getSource_name());
                    objectField.setBucCampoNombre(valid.getDictamen_field_name());
                    listFields.add(objectField);
                });
                object.setBucFuenteCampo(listFields);
            });
            var listDataResponseFilter = listDataResponse.stream()
                            .filter(distinctByKey(l -> l.getId()))
                                    .collect(Collectors.toList());
            paginationResponse.setCount(recordsCount);
            paginationResponse.setPages_amount(pagesAmount);
            paginationResponse.setData(listDataResponseFilter);
            log.info(JSONUtils.convertFromObjectToJson(paginationResponse.getData()));
            return paginationResponse;
        }
    }



    public ReadOnlyDtoResponse readOnly(ReadOnlyDtoRequest dto) {
        DecimalFormat formatoDecimal = new DecimalFormat("#.#");
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BucMapper mapper = session.getMapper(BucMapper.class);

            ArrayList<BucmapEntity> listbucmapEntity=mapper.getReadOnly(dto.getId());

            BucmapEntity object = listbucmapEntity.stream()
                    .filter(distinctByKey(l-> l.getFolio_code()))
                    .findAny().orElse(null);

            ReadOnlyDtoResponse response = new ReadOnlyDtoResponse();
            ReadOnlyBucmapDtoResponse roadmapResponse = new ReadOnlyBucmapDtoResponse();
            ReadOnlyResolucionDtoResponse resolucionResponse = new ReadOnlyResolucionDtoResponse();

            var validation = object == null? new BucmapEntity():object;

            roadmapResponse.setFolio(validation.getFolio_code());
            roadmapResponse.setSdatool(validation.getSdatool());
            roadmapResponse.setPrioridad(validation.getPriority());
            roadmapResponse.setCodigo_campo(validation.getField_code());
            roadmapResponse.setDato_funcional(validation.getFunctional_data());
            roadmapResponse.setDescripcion_funcional(validation.getFunctional_description());
            resolucionResponse.setEstado_resolucion(validation.getResolution_state());
            resolucionResponse.setResolucion(validation.getResolution());
            resolucionResponse.setComentario_resolucion(validation.getResolution_comment());
            resolucionResponse.setLogica(validation.getLogic_description());
            List<ReadOnlyResolucionFieldsDtoResponse> listFields = new ArrayList<>();
            listbucmapEntity.forEach(obj ->{
                var valid = obj == null? new BucmapEntity():obj;
                ReadOnlyResolucionFieldsDtoResponse objectField = new ReadOnlyResolucionFieldsDtoResponse();
                objectField.setId(object.getSource_id()==null?"":object.getSource_id());
                objectField.setId_fuente(object.getOld_source_id()==null?"":formatoDecimal.format(Float.parseFloat(object.getOld_source_id())));
                objectField.setFuente_dictaminada(valid.getSource_name());
                objectField.setCampo_dictaminado(valid.getDictamen_field_name());
                listFields.add(objectField);
            });

            resolucionResponse.setFields(listFields);
            response.setRoadmap(roadmapResponse);
            response.setResolucion(resolucionResponse);
            return response;
        }
    }
    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

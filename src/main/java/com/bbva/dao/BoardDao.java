package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BoardMapper;
import com.bbva.dto.board.request.BoardPaginationDtoRequest;
import com.bbva.dto.board.request.ListDtoRequest;
import com.bbva.dto.board.response.BoardPaginationDataDtoResponse;
import com.bbva.dto.board.response.BoardPaginationDtoResponse;
import com.bbva.dto.board.response.ListDtoResponse;
import com.bbva.entities.board.BoardPaginationEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BoardDao {
    private static final Logger log = Logger.getLogger(BoardDao.class.getName());

    public BoardPaginationDtoResponse pagination(BoardPaginationDtoRequest dto) {
        DecimalFormat formatoDecimal = new DecimalFormat("#.#");
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoardMapper mapper = session.getMapper(BoardMapper.class);
            Integer recordsCount = 0;
            Integer pagesAmount = 0;

            List<BoardPaginationEntity> list = mapper.pagination(dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getId_fuente(),
                    dto.getFuente_origen(),
                    dto.getFuente_datio(),
                    dto.getFuncional_descripcion()
            );

            BoardPaginationDtoResponse response = new BoardPaginationDtoResponse();
            ArrayList<BoardPaginationDataDtoResponse> listaResponse=new ArrayList<>();

            recordsCount = (list.size() > 0) ? Integer.parseInt(list.get(0).getRecords_count()) : 0;
            pagesAmount = (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue());

            list.forEach(object ->{
                var validation = object == null? new BoardPaginationEntity():object;
                BoardPaginationDataDtoResponse entityResponse = new BoardPaginationDataDtoResponse();
                entityResponse.setFuente(Integer.parseInt(validation.getSource_id()));
                entityResponse.setDeuda_tecnica(Integer.parseInt(validation.getDebt_source_id()));
                entityResponse.setFuente_antigua(formatoDecimal.format(Float.parseFloat(validation.getOld_source_id())));
                entityResponse.setProyecto(Integer.parseInt(validation.getProject_id()));
                entityResponse.setProyecto_sdatool(validation.getSdatool_id());
                entityResponse.setProyecto_nombre(validation.getProject_name());
                entityResponse.setFuente_origen(validation.getOrigin_source_name());
                entityResponse.setEstado_fuente(object.getStatus_type()==null?0:Integer.parseInt(object.getStatus_type()));
                entityResponse.setEstado_fuente_descripcion(validation.getStatus());
                entityResponse.setEstado_deuda(Integer.parseInt(validation.getDebtstatus_source_type()));
                entityResponse.setEstado_deuda_descripcion(validation.getDebtstatus_source());
                entityResponse.setTipologia(Integer.parseInt(validation.getTipology_type()));
                entityResponse.setTipologia_descripcion(validation.getTipology());
                entityResponse.setTabla_raw(validation.getRaw_table_name());
                entityResponse.setEstado_deuda_tecnica(Integer.parseInt(validation.getUuaa_debt_type()));
                entityResponse.setEstado_deuda_tecnica_descripcion(validation.getSumm_debt_t());
                entityResponse.setTabla_master(validation.getMaster_path_desc());
                entityResponse.setComplejidad_deuda("");
                listaResponse.add(entityResponse);
            });
            response.setData(listaResponse);
            response.setCount(recordsCount);
            response.setPages_amount(pagesAmount);
            return response;
        }
    }

    public List<ListDtoResponse> list(ListDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BoardMapper mapper = session.getMapper(BoardMapper.class);
            List<ListDtoResponse> response = new ArrayList<>();
            try {
                var boardEntityList = mapper.lista(dto.getId());
                response = boardEntityList.stream().map(f-> {
                  return new ListDtoResponse(f.getBoard_id(),f.getBoard_code(),f.getName());
                }).collect(Collectors.toList());
            }catch(Exception e){
            }
            log.info(JSONUtils.convertFromObjectToJson(response));
            return response;
        }
    }

}
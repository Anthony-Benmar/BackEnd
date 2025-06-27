package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SingleBaseMapper;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Logger;

public class SingleBaseDao {
    private static final Logger log = Logger.getLogger(SingleBaseDao.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    public SingleBaseDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<SingleBaseDataDtoResponse> getBaseUnicaWithSource(SingleBasePaginationDtoRequest dto) {
        List<SingleBaseDataDtoResponse> result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            result = mapper.getBaseUnicaDataWithFilters(dto);
            if (result == null) {
                result = List.of();
            }
        } catch (Exception e) {
            log.info("Error en getBaseUnicaWithSource: "+ e.getMessage());
        }
        return result;
    }

    public int getBaseUnicaTotalCount(SingleBasePaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            totalCount = mapper.getBaseUnicaTotalCountWithFilters(
                    dto.getId(),
                    dto.getProjectName(),
                    dto.getTipoFolio(),
                    dto.getFolio(),
                    dto.getRegisteredFolioDate(),
                    dto.getOldSourceId() // <-- Agregado para filtrar por TDS (ID FUENTE)
            );
        } catch (Exception e) {
            log.info("Error en SingleBasePaginationDtoRequest: "+ e.getMessage());
        }
        return totalCount;
    }

    // ----------- AGREGADO: Método para detalle por ID -----------
    public SingleBaseDataDtoResponse getSingleBaseById(String singleBaseId) {
        SingleBaseDataDtoResponse result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            result = mapper.getSingleBaseById(singleBaseId);
        } catch (Exception e) {
            log.info("Error en getSingleBaseById: "+ e.getMessage());
            return null;
        }
        return result;
    }

    // Métodos para combos
    public List<String> getDistinctFolios() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SingleBaseMapper.class).getDistinctFolios();
        }
    }

    public List<String> getDistinctProjectNames() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SingleBaseMapper.class).getDistinctProjectNames();
        }
    }

    public List<java.sql.Date> getDistinctRegisteredFolioDates() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SingleBaseMapper.class).getDistinctRegisteredFolioDates();
        }
    }

    public List<String> getDistinctStatusFolioTypes() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SingleBaseMapper.class).getDistinctStatusFolioTypes();
        }
    }

    public List<String> getDistinctFolioTypes() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SingleBaseMapper.class).getDistinctFolioTypes();
        }
    }
}
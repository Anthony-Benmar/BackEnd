package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SingleBaseMapper;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleBaseDao {
    private static final Logger log = Logger.getLogger(SingleBaseDao.class.getName());

    public List<SingleBaseDataDtoResponse> getBaseUnicaWithSource(SingleBasePaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<SingleBaseDataDtoResponse> result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            result = mapper.getBaseUnicaDataWithFilters(
                    dto.getLimit(),
                    dto.getOffset(),
                    dto.getProjectName(),
                    dto.getTipoFolio(),
                    dto.getFolio()
            );
            if (result == null) {
                result = List.of();
            }
            log.info("SingleBaseDao - Registros obtenidos: " + result.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getBaseUnicaWithSource: " + e.getMessage(), e);
        }
        return result;
    }

    public int getBaseUnicaTotalCount(SingleBasePaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            totalCount = mapper.getBaseUnicaTotalCountWithFilters(
                    dto.getProjectName(),
                    dto.getTipoFolio(),
                    dto.getFolio()
            );
            log.info("SingleBaseDao - Total filtrado: " + totalCount);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getBaseUnicaTotalCount: " + e.getMessage(), e);
        }
        return totalCount;
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
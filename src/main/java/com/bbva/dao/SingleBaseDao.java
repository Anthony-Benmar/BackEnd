package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SingleBaseMapper;
import com.bbva.dto.single_base.response.SingleBaseResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleBaseDao {
    private static final Logger log = Logger.getLogger(SingleBaseDao.class.getName());

    // Método para obtener los datos paginados
    public List<SingleBaseResponseDTO> getBaseUnicaWithSource(int limit, int offset) {
        List<SingleBaseResponseDTO> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            result = mapper.getBaseUnicaData(limit, offset);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    // Método para obtener el total de registros (sin paginación)
    public int getBaseUnicaTotalCount() {
        int totalCount = 0;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            totalCount = mapper.getBaseUnicaTotalCount();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return totalCount;
    }
}
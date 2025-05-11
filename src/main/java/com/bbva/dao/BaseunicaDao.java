package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BaseunicaMapper;
import com.bbva.dto.baseunica.response.BaseunicaResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseunicaDao {
    private static final Logger log = Logger.getLogger(BaseunicaDao.class.getName());

    public List<BaseunicaResponseDTO> getBaseUnicaWithSource(String tableName) {
        List<BaseunicaResponseDTO> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // Obtener el mapper correspondiente para Baseunica
            BaseunicaMapper mapper = session.getMapper(BaseunicaMapper.class);
            // Llamar al método que ejecuta el procedimiento y pasa el parámetro 'tableName'
            result = mapper.getBaseUnicaData(tableName);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }
}


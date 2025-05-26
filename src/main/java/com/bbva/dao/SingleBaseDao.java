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

    public List<SingleBaseResponseDTO> getBaseUnicaWithSource() {
        List<SingleBaseResponseDTO> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // Obtener el mapper correspondiente para Baseunica
            SingleBaseMapper mapper = session.getMapper(SingleBaseMapper.class);
            // Llamar al método que ejecuta el procedimiento y pasa el parámetro 'tableName'
            result = mapper.getBaseUnicaData();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }
}


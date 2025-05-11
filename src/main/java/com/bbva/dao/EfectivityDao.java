package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.EfectivityMapper;
import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EfectivityDao {
    private static final Logger log = Logger.getLogger(EfectivityDao.class.getName());
    public List<EfectivityEntityResponseDTO> getEfectivityWithSource(String tableName){
        List<EfectivityEntityResponseDTO> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            EfectivityMapper mapper = session.getMapper(EfectivityMapper.class);
            result = mapper.getEfectivityWithSource(tableName);
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

}

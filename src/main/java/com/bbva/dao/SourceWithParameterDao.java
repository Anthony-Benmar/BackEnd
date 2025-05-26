package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.EfectivityMapper;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;
import com.bbva.dto.sourceWithParameter.response.SourceWithParameterDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceWithParameterDao {
    private static final Logger log = Logger.getLogger(SourceWithParameterDao.class.getName());

    public List<SourceWithParameterDTO> getSourceWithParameter(){
        List<SourceWithParameterDTO> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            result = mapper.getSourcesWithParameter();
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }
}

package com.bbva.dao;

import com.bbva.core.results.ErrorDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BatchMapper;
import com.bbva.database.mappers.ExceptionsMapper;
import com.bbva.dto.batch.request.BatchIssuesActionFilterDtoRequest;
import com.bbva.dto.batch.response.BatchIssuesActionFilterDtoResponse;
import com.bbva.dto.batch.response.BatchIssuesActionSelectDtoResponse;
import com.bbva.dto.batch.response.ExceptionEntityResponseDTO;
import com.bbva.dto.batch.response.StatusJobExecutionDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionDao {
    private static final Logger log = Logger.getLogger(ExceptionDao.class.getName());

    public List<ExceptionEntityResponseDTO> getExceptionsWithSource(){
        List<ExceptionEntityResponseDTO> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            ExceptionsMapper mapper = session.getMapper(ExceptionsMapper.class);
             result = mapper.getExceptionsWithSource();
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

}

package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UseCaseMapper;
import com.bbva.entities.common.PeriodEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.bbva.entities.usecase.UseCaseEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseReliabilityDao {

    private static final Logger LOGGER = Logger.getLogger(UseCaseReliabilityDao.class.getName());

    public List<UseCaseEntity> listAllUseCases() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseMapper mapper = session.getMapper(UseCaseMapper.class);
            return mapper.listAllUseCases();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }
}

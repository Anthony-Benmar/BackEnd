package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UseCaseDefinitionMapper;
import com.bbva.entities.use_case_definition.UseCaseDefinitionEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseDefinitionDao {

    private static final Logger log = Logger.getLogger(UseCaseDefinitionDao.class.getName());

    public List<UseCaseDefinitionEntity> listForProjectID(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseDefinitionMapper mapper = session.getMapper(UseCaseDefinitionMapper.class);
            List<UseCaseDefinitionEntity> useCaseList = mapper.listUseCaseDefinitionByProjectId(projectId);
            return useCaseList;
        }
    }

    public DataResult<UseCaseDefinitionEntity> insert(UseCaseDefinitionEntity item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UseCaseDefinitionMapper mapper = session.getMapper(UseCaseDefinitionMapper.class);
                mapper.insert(item);
                session.commit();
                return new SuccessDataResult(item);
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500",e.getMessage());
        }
    }


}

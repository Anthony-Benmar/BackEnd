package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JiraValidatorLogMapper;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DmJiraValidatorLogDao {

    private static final Logger LOGGER = Logger.getLogger(DmJiraValidatorLogDao.class.getName());

    private static DmJiraValidatorLogDao instance = null;

    public static synchronized DmJiraValidatorLogDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DmJiraValidatorLogDao();
        }
        return instance;
    }

    public boolean insertDmJiraValidatorLog(JiraValidatorLogEntity entity) {
        try {
            LOGGER.info("Insertando log en DmJiraValidator");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JiraValidatorLogMapper mapper = session.getMapper(JiraValidatorLogMapper.class);
                mapper.insertJiraValidatorLog(entity);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
}
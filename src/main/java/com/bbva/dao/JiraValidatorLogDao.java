package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JiraValidatorLogMapper;
import com.bbva.database.mappers.LogMapper;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import com.bbva.entities.secu.LogEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JiraValidatorLogDao {

    private static final Logger LOGGER = Logger.getLogger(JiraValidatorLogDao.class.getName());

    private static JiraValidatorLogDao instance = null;

    public static synchronized JiraValidatorLogDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new JiraValidatorLogDao();
        }
        return instance;
    }

    public boolean insertJiraValidatorLog(JiraValidatorLogEntity entity) {
        try {
            LOGGER.info("Insertar Log en Mapper");
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

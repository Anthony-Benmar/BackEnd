package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JiraValidatorLogMapper;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import org.apache.ibatis.session.SqlSession;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DmJiraValidatorLogDao {

    private static final Logger LOGGER = Logger.getLogger(DmJiraValidatorLogDao.class.getName());

    public boolean insertDmJiraValidatorLog(JiraValidatorLogEntity entity) {
        SqlSession session = null;
        try {
            session = MyBatisConnectionFactory.getInstance().openSession();
            JiraValidatorLogMapper mapper = session.getMapper(JiraValidatorLogMapper.class);
            mapper.insertJiraValidatorLog(entity);
            session.commit();
            return true;
        } catch (Exception e) {
            if (session != null) {
                session.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error al insertar log de validación DM: " + e.getMessage(), e);
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

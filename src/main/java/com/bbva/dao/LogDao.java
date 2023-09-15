package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.LogMapper;
import com.bbva.entities.secu.LogEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogDao {

    private static final Logger LOGGER = Logger.getLogger(LogDao.class.getName());

    private static LogDao instance = null;

    public static synchronized LogDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new LogDao();
        }
        return instance;
    }

    public List<LogEntity> list() {
        List<LogEntity> logList = null;
        try {
            LOGGER.info("Listar Log en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                LogMapper mapper = session.getMapper(LogMapper.class);
                logList = mapper.list();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return logList;
    }

    public boolean insertLog(LogEntity entity) {

        try {
            LOGGER.info("Insertar Log en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                LogMapper mapper = session.getMapper(LogMapper.class);
                mapper.insertLog(entity);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
}

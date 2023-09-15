package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.AccionMapper;
import com.bbva.entities.secu.Accion;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class AccionDao {
    
    private static final Logger LOGGER = Logger.getLogger(AccionDao.class.getName());

    private static AccionDao instance = null;

    public static synchronized AccionDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AccionDao();
        }

        return instance;
    } 

    public List<Accion> list() {
        
        List<Accion> accionList = null;

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccionMapper mapper = session.getMapper(AccionMapper.class);
            accionList = mapper.list();
        }    
            
        return accionList;

    }

}

package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.AccionMapper;
import com.bbva.database.mappers.InfoJiraProjectMapper;
import com.bbva.entities.jiravalidator.InfoJiraProject;
import com.bbva.entities.secu.Accion;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class InfoJiraProjectDao {
    
    private static final Logger LOGGER = Logger.getLogger(InfoJiraProjectDao.class.getName());

    private static InfoJiraProjectDao instance = null;

    public static synchronized InfoJiraProjectDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new InfoJiraProjectDao();
        }

        return instance;
    } 

    public List<InfoJiraProject> list() {
        
        List<InfoJiraProject> infoJiraProjectList = null;

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            InfoJiraProjectMapper mapper = session.getMapper(InfoJiraProjectMapper.class);
            infoJiraProjectList = mapper.list();
        }    
            
        return infoJiraProjectList;

    }

    public String currentQ(){
        String currentQ = "";
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            InfoJiraProjectMapper mapper = session.getMapper(InfoJiraProjectMapper.class);
            currentQ = mapper.currentQ();
        }
        return currentQ;
    }

}

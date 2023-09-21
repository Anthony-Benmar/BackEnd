package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.MapDependencyMapper;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.entities.map_dependecy.MapDependencyEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapDependencyDao {
    private static final Logger log = Logger.getLogger(MapDependencyDao.class.getName());


    public List<MapDependencyListByProjectResponse> listMapDependencyByProjectId(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<MapDependencyListByProjectResponse> list;

        try (SqlSession session = sqlSessionFactory.openSession()) {
            MapDependencyMapper mapper = session.getMapper(MapDependencyMapper.class);
            list = mapper.listMapDependencyByProjectById(projectId);
        }
        return list;
    }

    public boolean insert(MapDependencyEntity item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                MapDependencyMapper mapper = session.getMapper(MapDependencyMapper.class);
                mapper.insert(item);
                session.commit();
                return true;
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean update(MapDependencyEntity item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                MapDependencyMapper mapper = session.getMapper(MapDependencyMapper.class);
                mapper.update(item);
                session.commit();
                return true;
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
}
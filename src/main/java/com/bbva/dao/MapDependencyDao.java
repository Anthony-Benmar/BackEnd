package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
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

    public DataResult<MapDependencyEntity> insert(MapDependencyEntity item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                MapDependencyMapper mapper = session.getMapper(MapDependencyMapper.class);
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

    public DataResult<MapDependencyEntity> update(MapDependencyEntity item) {
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                MapDependencyMapper mapper = session.getMapper(MapDependencyMapper.class);
                mapper.update(item);
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
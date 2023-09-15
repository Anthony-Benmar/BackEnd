package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.RolMapper;
import com.bbva.entities.secu.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class RolDao {
    
    private static final Logger LOGGER = Logger.getLogger(RolDao.class.getName());

    private static RolDao instance = null;

    public static synchronized RolDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RolDao();
        }

        return instance;
    }    

    public List<Rol> list() {
        
        List<Rol> userList = null;

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            RolMapper mapper = session.getMapper(RolMapper.class);
            userList = mapper.list();
        }    
            
        return userList;

    }

    public List<Menu> listIdsMenu(int role_id) {
        
        List<Menu> menuList = null;

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            RolMapper mapper = session.getMapper(RolMapper.class);
            menuList = mapper.listIdsMenu(role_id);
        }
            
        return menuList;

    }

    public List<Accion> listIdsMenuAccion(int role_id) {
        
        List<Accion> accionList = null;

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            RolMapper mapper = session.getMapper(RolMapper.class);
            accionList = mapper.listIdsMenuAccion(role_id);
        }
            
        return accionList;

    }

    public void insertRolMenu(RolMenu rolMenu) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RolMapper mapper = session.getMapper(RolMapper.class);
                mapper.insertMenu(rolMenu);
                session.commit();
            }
    }

    public void insertRolMenuAccion(RolMenuAction rolMenuAction) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RolMapper mapper = session.getMapper(RolMapper.class);
                mapper.insertAccion(rolMenuAction);
                session.commit();
            }
    }

    public void insertRol(Rol rol) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RolMapper mapper = session.getMapper(RolMapper.class);
                mapper.insertRol(rol);
                session.commit();
            }
    }

    public void updateRol(Rol rol) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RolMapper mapper = session.getMapper(RolMapper.class);
                mapper.updateRol(rol);
                session.commit();
            }
    }

    public void deleteMenus(Rol rol) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RolMapper mapper = session.getMapper(RolMapper.class);
                mapper.deleteMenus(rol.getIdRole());
                session.commit();
            }
    }

    public void deleteAcciones(Rol rol) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RolMapper mapper = session.getMapper(RolMapper.class);
                mapper.deleteAcciones(rol.getIdRole());
                session.commit();
            }
    }

}

package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.MenuMapper;
import com.bbva.entities.secu.Menu;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class MenuDao {
    
    private static final Logger LOGGER = Logger.getLogger(MenuDao.class.getName());

    private static MenuDao instance = null;

    public static synchronized MenuDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MenuDao();
        }

        return instance;
    } 

    public List<Menu> list() {
        
        List<Menu> menuList = null;

        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            MenuMapper mapper = session.getMapper(MenuMapper.class);
            menuList = mapper.list();
        }    
            
        return menuList;

    }

}

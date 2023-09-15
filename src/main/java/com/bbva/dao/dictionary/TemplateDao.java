package com.bbva.dao.dictionary;

import java.util.Objects;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.dictionary.TemplateMapper;
import com.bbva.entities.dictionary.TemplateEntity;

public class TemplateDao {
    
    private static TemplateDao instance = null;
    
    public static synchronized TemplateDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TemplateDao();
        }
        return instance;
    }

    public TemplateEntity obtenerVigente(String type){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
			return templateMapper.obtenerVigente(type);
		}
    }

}

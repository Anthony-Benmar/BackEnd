package com.bbva.dao.dictionary;

import java.util.List;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.dictionary.GenerationFieldMapper;
import com.bbva.entities.dictionary.GenerationFieldEntity;

public final class GenerationFieldDao {
    
    private static GenerationFieldDao instance = null;
    
    public static synchronized GenerationFieldDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GenerationFieldDao();
        }
        return instance;
    }

    public void insertar(GenerationFieldEntity entity, SqlSession session) {
		final GenerationFieldMapper generationFieldMapper = session.getMapper(GenerationFieldMapper.class);
		generationFieldMapper.insertar(entity);
	}

    public List<GenerationFieldEntity> buscar(Integer generationId){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationFieldMapper generationFieldMapper = session.getMapper(GenerationFieldMapper.class);
			return generationFieldMapper.buscar(generationId);
		}
    }

    public GenerationFieldEntity buscarPorId(Integer generationFieldId){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationFieldMapper generationFieldMapper = session.getMapper(GenerationFieldMapper.class);
			return generationFieldMapper.buscarPorId(generationFieldId);
		}
    }

    public void actualizarFieldDatum(GenerationFieldEntity entity){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationFieldMapper generationFieldMapper = session.getMapper(GenerationFieldMapper.class);
			generationFieldMapper.actualizarFieldDatum(entity);
            session.commit();
		}
    }

}

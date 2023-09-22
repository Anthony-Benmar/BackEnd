package com.bbva.dao.dictionary;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.bbva.entities.dictionary.GenerationEntity;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.dictionary.GenerationMapper;
import com.bbva.dto.dictionary.parameter.GenerationSearchParameter;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public final class GenerationDao {
    
    private static GenerationDao instance = null;
    
    public static synchronized GenerationDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GenerationDao();
        }
        return instance;
    } 

    public void insertar(GenerationEntity entity, SqlSession session) {
		final GenerationMapper generationMapper = session.getMapper(GenerationMapper.class);
		generationMapper.insertar(entity);
	}

    public GenerationEntity buscarPorId(Integer generationId){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationMapper generationMapper = session.getMapper(GenerationMapper.class);
			return generationMapper.buscarPorId(generationId);
		}
    }

    public List<GenerationEntity> buscar(GenerationSearchParameter parameter){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationMapper generationMapper = session.getMapper(GenerationMapper.class);
			return generationMapper.buscar(parameter);
		}
    }

    public void finalizar(GenerationEntity entity){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationMapper generationMapper = session.getMapper(GenerationMapper.class);
			generationMapper.finalizar(entity);
            session.commit();
		}
    }

    public void desactivarAntiguos(Date fechaCorte){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final GenerationMapper generationMapper = session.getMapper(GenerationMapper.class);
			generationMapper.desactivarAntiguos(fechaCorte);
            session.commit();
		}
    }

}

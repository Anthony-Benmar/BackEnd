package com.bbva.dao.dictionary;

import java.util.List;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.dictionary.FieldDatumMapper;
import com.bbva.entities.dictionary.FieldDatumEntity;

public final class FieldDatumDao {
    
    private static FieldDatumDao instance = null;
    
    public static synchronized FieldDatumDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new FieldDatumDao();
        }
        return instance;
    } 

    public List<FieldDatumEntity> filtrar(String physicalFieldName, SqlSession session){
        final FieldDatumMapper fieldDatumMapper = session.getMapper(FieldDatumMapper.class);
        return fieldDatumMapper.filtrar(physicalFieldName);
    }
    
    public List<FieldDatumEntity> filtrar(String physicalFieldName){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			return this.filtrar(physicalFieldName, session);
		}
    }

    public FieldDatumEntity buscarPorId(Integer fieldDatumId){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
		try (SqlSession session = sqlSessionFactory.openSession()) {
			final FieldDatumMapper fieldDatumMapper = session.getMapper(FieldDatumMapper.class);
            return fieldDatumMapper.buscarPorId(fieldDatumId);
		}
    }

}

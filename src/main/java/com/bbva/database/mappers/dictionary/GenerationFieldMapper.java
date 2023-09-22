package com.bbva.database.mappers.dictionary;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.bbva.database.mappers.dictionary.sql.GenerationFieldSqlUtil;
import com.bbva.entities.dictionary.GenerationFieldEntity;

public interface GenerationFieldMapper {
    
    @InsertProvider(type = GenerationFieldSqlUtil.class, method = "insertar")
	@Options(useGeneratedKeys = true, keyProperty = "generationFieldId", keyColumn = "generation_field_id")
	void insertar(GenerationFieldEntity entity);

	@SelectProvider(type = GenerationFieldSqlUtil.class, method = "buscar")
    List<GenerationFieldEntity> buscar(Integer generationId);

	@SelectProvider(type = GenerationFieldSqlUtil.class, method = "buscarPorId")
    GenerationFieldEntity buscarPorId(Integer generationFieldId);

	@UpdateProvider(type = GenerationFieldSqlUtil.class, method = "actualizarFieldDatum")
	void actualizarFieldDatum(GenerationFieldEntity entity);

}

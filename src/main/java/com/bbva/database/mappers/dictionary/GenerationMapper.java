package com.bbva.database.mappers.dictionary;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.bbva.database.mappers.dictionary.sql.GenerationSqlUtil;
import com.bbva.dto.dictionary.parameter.GenerationSearchParameter;
import com.bbva.entities.dictionary.GenerationEntity;

public interface GenerationMapper {
    
    @InsertProvider(type = GenerationSqlUtil.class, method = "insertar")
	@Options(useGeneratedKeys = true, keyProperty = "generationId", keyColumn = "generation_id")
	void insertar(GenerationEntity entity);

	@SelectProvider(type = GenerationSqlUtil.class, method = "buscarPorId")
    GenerationEntity buscarPorId(Integer generationId);
	
	@SelectProvider(type = GenerationSqlUtil.class, method = "buscar")
    List<GenerationEntity> buscar(GenerationSearchParameter parameter);

	@UpdateProvider(type = GenerationSqlUtil.class, method = "finalizar")
	void finalizar(GenerationEntity entity);

	@UpdateProvider(type = GenerationSqlUtil.class, method = "desactivarAntiguos")
	void desactivarAntiguos(Date fechaCorte);

}

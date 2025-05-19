package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UseCaseMapper;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UseCaseReliabilityDao {

    private static final Logger LOGGER = Logger.getLogger(UseCaseReliabilityDao.class.getName());

    public List<UseCaseEntity> listAllUseCases() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseMapper mapper = session.getMapper(UseCaseMapper.class);
            return mapper.listAllUseCases();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public DataResult<UpdateOrInsertDtoResponse> updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UseCaseMapper mapper = session.getMapper(UseCaseMapper.class);
                UpdateOrInsertDtoResponse result = mapper.updateOrInsertUseCase(dto);
                session.commit();
                return new SuccessDataResult(result);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public UseCaseInputsFilterDtoResponse getFilteredUseCases(UseCaseInputsFilterDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<UseCaseInputsDtoResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;
        var response = new UseCaseInputsFilterDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseMapper mapper = session.getMapper(UseCaseMapper.class);
            lista = mapper.getFilteredUseCases(
                    dto.getDomainName(),
                    dto.getProjectName()
            );
        }
        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        if (dto.getRecords_amount() > 0) {
            lista = lista.stream()
                    .skip(dto.getRecords_amount() * (dto.getPage() - 1))
                    .limit(dto.getRecords_amount())
                    .collect(Collectors.toList());
        }

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        LOGGER.info(JSONUtils.convertFromObjectToJson(response.getData()));
        return response;
    }
}

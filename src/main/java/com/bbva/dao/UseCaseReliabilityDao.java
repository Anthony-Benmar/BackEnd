package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UseCaseMapper;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseReliabilityDao {

    private static final Logger LOGGER = Logger.getLogger(UseCaseReliabilityDao.class.getName());

    public List<UseCaseEntity> listAllUseCases() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseMapper mapper = session.getMapper(UseCaseMapper.class);
            return mapper.listAllUseCases();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public UpdateOrInsertDtoResponse updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UseCaseMapper mapper = session.getMapper(UseCaseMapper.class);
                UpdateOrInsertDtoResponse result = mapper.updateOrInsertUseCase(dto);
                session.commit();
                return result;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new UpdateOrInsertDtoResponse();
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
                    dto.getCritical(),
                    dto.getProjectName()
            );
        }
        recordsCount = (lista.isEmpty()) ? 0 : (int) lista.size();
        pagesAmount = dto.getRecordsAmount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecordsAmount().floatValue()) : 1;

        if (dto.getRecordsAmount() > 0) {
            lista = lista.stream()
                    .skip((long) dto.getRecordsAmount() * (dto.getPage() - 1))
                    .limit(dto.getRecordsAmount())
                    .toList();
        }

        response.setCount(recordsCount);
        response.setPagesAmount(pagesAmount);
        response.setData(lista);
        return response;
    }
}

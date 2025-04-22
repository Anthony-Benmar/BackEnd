package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.response.InventoryInputsDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReliabilityDao {
    private static final Logger LOGGER = Logger.getLogger(ReliabilityDao.class.getName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ReliabilityDao.class);
    private static ReliabilityDao instance = null;

    public static synchronized ReliabilityDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ReliabilityDao();
        }
        return instance;
    }

    public InventoryInputsFilterDtoResponse inventoryInputsFilter(InventoryInputsFilterDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<InventoryInputsDtoResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        var response = new InventoryInputsFilterDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            lista = reliabilityMapper.inventoryInputsFilter(
                    dto.getDomainName(),
                    dto.getUseCase(),
                    dto.getJobType(),
                    dto.getFrequency(),
                    dto.getIsCritical(),
                    dto.getSearchByInputOutputTable()
            );

        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        if (dto.records_amount > 0) {
            lista = lista.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

//        for (ProjectInfoSelectResponse item : lista) {
//            if (item.getCreateAuditDate() != null) {
//                item.setCreateAuditDate_S(convertDateToString(item.getCreateAuditDate(), "dd/MM/yyyy HH:mm:ss"));
//            }
//            if (item.getUpdateAuditDate() != null) {
//                item.setUpdateAuditDate_S(convertDateToString(item.getUpdateAuditDate(), "dd/MM/yyyy HH:mm:ss"));
//            }
//
//        }
        for (InventoryInputsDtoResponse item : lista) {
            if (item.getInputPaths() != null) {
                item.setInputPathsArray(item.getInputPaths().split("\n"));
            }
        }
        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));
        return response;
    }

    public void updateInventoryJobStock(InventoryJobUpdateDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            reliabilityMapper.updateInventoryJobStock(
                    dto.getJobName(),
                    dto.getComponentName(),
                    dto.getFrequencyId(),
                    dto.getInputPaths(),
                    dto.getOutputPath(),
                    dto.getJobTypeId(),
                    dto.getUseCaseId(),
                    dto.getIsCritical(),
                    dto.getDomainId()
            );
            session.commit();
        } catch (Exception e) {
            throw e;
        }
    }

}

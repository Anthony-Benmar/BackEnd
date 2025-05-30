package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.request.TransferInputDtoRequest;
import com.bbva.dto.reliability.response.*;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        recordsCount = (!lista.isEmpty()) ? lista.size() : 0;
        pagesAmount = dto.getRecordsAmount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecordsAmount().floatValue()) : 1;

        if (dto.getRecordsAmount() > 0) {
            lista = lista.stream()
                    .skip((long) dto.getRecordsAmount() * (dto.getPage() - 1))
                    .limit(dto.getRecordsAmount())
                    .toList();
        }

        for (InventoryInputsDtoResponse item : lista) {
            if (item.getInputPaths() != null) {
                item.setInputPathsArray(item.getInputPaths().split("\n"));
            }
        }
        response.setCount(recordsCount);
        response.setPagesAmount(pagesAmount);
        response.setData(lista);
        if (log.isInfoEnabled()) {
            try {
                log.info(JSONUtils.convertFromObjectToJson(response.getData()));
            } catch (Exception e) {
                log.warn("Error converting response to JSON", e);
            }
        }
        return response;
    }
    public List<PendingCustodyJobsDtoResponse> getPendingCustodyJobs(String sdatoolId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<PendingCustodyJobsDtoResponse> pendingCustodyJobsList;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper mapper = session.getMapper(ReliabilityMapper.class);
            pendingCustodyJobsList = mapper.getPendingCustodyJobs(sdatoolId);

            // Validar si la lista es nula o está vacía
            if (pendingCustodyJobsList == null || pendingCustodyJobsList.isEmpty()) {
                return Collections.emptyList(); // Retorna una lista vacía en lugar de null
            }

            for (PendingCustodyJobsDtoResponse item : pendingCustodyJobsList) {
                if (item.getJobName() != null) {
                    item.setJobName(item.getJobName().replaceAll("\\s+", ""));
                }
            }
            return pendingCustodyJobsList;
        }
    }
    public void updateInventoryJobStock(InventoryJobUpdateDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            reliabilityMapper.updateInventoryJobStock(dto);
            session.commit();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    public List<ProjectCustodyInfoDtoResponse> getProjectCustodyInfo(String sdatoolId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectCustodyInfoDtoResponse> projectCustodyInfoList;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper mapper = session.getMapper(ReliabilityMapper.class);
            projectCustodyInfoList = mapper.getProjectCustodyInfo(sdatoolId);
            return projectCustodyInfoList;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }

    public ExecutionValidationDtoResponse getExecutionValidation(String jobName) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        ExecutionValidationDtoResponse executionValidation;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper mapper = session.getMapper(ReliabilityMapper.class);
            executionValidation = mapper.getExecutionValidation(jobName);
            return executionValidation;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public List<ExecutionValidationAllDtoResponse> getExecutionValidationAll(List<String> jobsNames) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ExecutionValidationAllDtoResponse> executionValidationAll = new ArrayList<>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper mapper = session.getMapper(ReliabilityMapper.class);
            jobsNames.forEach(jobName -> {
                ExecutionValidationDtoResponse executionValidation = mapper.getExecutionValidation(jobName);
                executionValidationAll.add(ExecutionValidationAllDtoResponse.builder()
                                .jobName(jobName)
                                .validation(executionValidation.getValidation())
                        .build());
            });


            return executionValidationAll;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }

    public void insertTransfer(TransferInputDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            reliabilityMapper.insertTranfer(dto);
            dto.getTransferInputDtoRequests().forEach(reliabilityMapper::insertJobStock);
            session.commit();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}

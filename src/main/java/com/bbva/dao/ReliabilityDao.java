package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
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
        List<InventoryInputsDtoResponse> lista = listinventory(dto);
        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        var response = new InventoryInputsFilterDtoResponse();

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

            if (pendingCustodyJobsList == null || pendingCustodyJobsList.isEmpty()) {
                return Collections.emptyList();
            }

            for (PendingCustodyJobsDtoResponse item : pendingCustodyJobsList) {
                if (item.getJobName() != null) {
                    item.setJobName(item.getJobName().replaceAll("\\s+", ""));
                }
            }

            return pendingCustodyJobsList;
        }catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage(), e);
        return Collections.emptyList();
        }
    }

    public List<JobExecutionHistoryDtoResponse> getJobExecutionHistory(String jobName) {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session
                    .getMapper(ReliabilityMapper.class)
                    .getJobExecutionHistory(jobName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching job history", e);
            return Collections.emptyList();
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

    public static class PersistenceException extends RuntimeException {
        public PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void insertTransfer(TransferInputDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            reliabilityMapper.insertTranfer(dto);
            dto.getTransferInputDtoRequests().forEach(reliabilityMapper::insertJobStock);
            session.commit();
        } catch (Exception e) {
            String errorMessage = "Error al guardar los datos de la transferencia en la base de datos.";
            throw new PersistenceException(errorMessage, e);
        }
    }

    public List<InventoryInputsDtoResponse> listinventory(InventoryInputsFilterDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<InventoryInputsDtoResponse> lista;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            lista = reliabilityMapper.inventoryInputsFilter(dto);
            return lista;
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return List.of();
        }
    }

    public List<DropDownDto> getOriginTypes() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper mapper = session.getMapper(ReliabilityMapper.class);
            return mapper.getOriginTypes();
        }
    }

    public List<RawSn2DtoResponse> fetchRawSn2BySn1(Integer sn1) {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session
                    .getMapper(ReliabilityMapper.class)
                    .fetchRawSn2BySn1(sn1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching raw SN2 by SN1", e);
            return Collections.emptyList();
        }
    }

    public PaginationReliabilityPackResponse getReliabilityPacks(ReliabilityPackInputFilterRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ReliabilityPacksDtoResponse> lista;
        Integer recordsCount = 0;
        Integer pagesAmount = 0;
        var response = new PaginationReliabilityPackResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper mapper = session.getMapper(ReliabilityMapper.class);
            lista = mapper.getReliabilityPacks(
                    dto.getDomainName(),
                    dto.getUseCase()
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

    public void updateStatusReliabilityPacksJobStock(List<String> packs) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ReliabilityMapper reliabilityMapper = session.getMapper(ReliabilityMapper.class);
            packs.forEach(pack -> {
                reliabilityMapper.updateReliabilityStatus(pack, 1);
                reliabilityMapper.updateProjectInfoStatus(pack, 1);
            });
            session.commit();
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public List<ReliabilityPacksDtoResponse> listTransfersByStatus(
            String domainCsv, String useCaseCsv, String statusCsv) {

        LOGGER.info(() -> String.format(
                "CALL sidedb.SP_LIST_TRANSFERS_BY_STATUS('%s','%s','%s')",
                domainCsv, useCaseCsv, statusCsv
        ));

        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(ReliabilityMapper.class)
                    .listTransfersByStatus(domainCsv, useCaseCsv, statusCsv);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Integer getPackCurrentStatus(String pack) {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(ReliabilityMapper.class).getPackStatus(pack);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public void changeTransferStatus(String pack, int newStatus) {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            ReliabilityMapper m = session.getMapper(ReliabilityMapper.class);
            m.updateReliabilityStatus(pack, newStatus);
            m.updateProjectInfoStatus(pack, newStatus);
            session.commit();
        } catch (Exception e) {
            throw new PersistenceException("No se pudo actualizar el estado del pack " + pack, e);
        }
    }

    public void updateJobByPackAndName(UpdateJobDtoRequest dto) {
        try (SqlSession s = MyBatisConnectionFactory.getInstance().openSession()) {
            int rows = s.getMapper(ReliabilityMapper.class).updateJobByPackAndName(dto);
            if (rows == 0) {
                throw new PersistenceException("No se encontró el job en ese pack", null);
            }
            s.commit();
        }
    }

    public void updatePackComments(String pack, String comments) {
        try (SqlSession s = MyBatisConnectionFactory.getInstance().openSession()) {
            int rows = s.getMapper(ReliabilityMapper.class).updatePackComments(pack, comments);
            if (rows == 0) throw new PersistenceException("Pack no encontrado para comentar: " + pack, null);
            s.commit();
        }
    }

    public TransferDetailResponse getTransferDetail(String pack) {
        try (SqlSession s = MyBatisConnectionFactory.getInstance().openSession()) {
            var m = s.getMapper(ReliabilityMapper.class);
            var header = m.getTransferHeader(pack);
            if (header == null) return null;
            var jobs = m.getTransferJobs(pack);
            return TransferDetailResponse.builder().header(header).jobs(jobs).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getTransferDetail", e);
            return null;
        }
    }

    public void updateJobComment(String pack, String jobName, String comments){
        try (SqlSession s = MyBatisConnectionFactory.getInstance().openSession()) {
            int rows = s.getMapper(ReliabilityMapper.class).updateJobComment(pack, jobName, comments);
            if (rows == 0) throw new PersistenceException("No se encontró el job en ese pack", null);
            s.commit();
        }
    }
}

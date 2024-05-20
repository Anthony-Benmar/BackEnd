package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BatchMapper;
import com.bbva.database.mappers.JobMapper;
import com.bbva.dto.batch.request.*;
import com.bbva.dto.batch.response.*;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoSelectDtoResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.bbva.util.types.FechaUtil.convertStringToDate;

public class BatchDao {

    private static final Logger log = Logger.getLogger(BatchDao.class.getName());

    public JobExecutionFilterResponseDTO filter(JobExecutionFilterRequestDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<JobExecutionFilterData> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        JobExecutionFilterResponseDTO response = new JobExecutionFilterResponseDTO();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchMapper mapper = session.getMapper(BatchMapper.class);
            lista = mapper.filter(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getJobName(),
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getFolder(),
                    dto.getDataproc(),
                    dto.getOrderId(),
                    dto.getProjectName(),
                    dto.getSdatoolId(),
                    dto.getDomain(),
                    dto.getIsTypified());
        }
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));
        recordsCount = (lista.size() > 0) ? lista.get(0).getRecordsCount() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        Integer notTypified = (lista.size() > 0) ? lista.get(0).getWithoutTypified() : 0;
        Integer typified = (lista.size() > 0) ? lista.get(0).getTypified() : 0;

        StatisticsData statistics = new StatisticsData();
        statistics.setNotTypified(notTypified);
        statistics.setTypified(typified);

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        response.setStatistics(statistics);
        return response;
    }

    public InsertEntity insertReliabilityIncidence(InsertReliabilityIncidenceDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        InsertBatchIssueActionsDtoRequest request = new InsertBatchIssueActionsDtoRequest();
        if (dto.getDataIssueActions() == null) {
            try (SqlSession session = sqlSessionFactory.openSession()) {
                BatchMapper batchMapper = session.getMapper(BatchMapper.class);
                InsertEntity result = batchMapper.insertReliabilityIncidence(
                        dto.getJobName(),
                        dto.getOrderDate(),
                        dto.getOrderId(),
                        dto.getErrorType(),
                        dto.getErrorReason(),
                        dto.getSolutionDetail(),
                        dto.getEmployeeId(),
                        dto.getLogArgos(),
                        dto.getRunCounter(),
                        dto.getTicketJira(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                session.commit();
                //return dto;
                return result;
            }
        }
        request.setIssueActionsId(dto.getDataIssueActions().getIssueActionsId());
        request.setJobId(dto.getDataIssueActions().getJobId());
        request.setFolderName(dto.getDataIssueActions().getFolderName());
        request.setDevEmail(dto.getDataIssueActions().getDevEmail());
        request.setEndDate(dto.getDataIssueActions().getEndDate());
        request.setStatusType(dto.getDataIssueActions().getStatusType());
        request.setCommentActionsDesc(dto.getDataIssueActions().getCommentActionsDesc());
        request.setCreateAuditUser(dto.getDataIssueActions().getCreateAuditUser());
        request.setUpdateAuditUser(dto.getDataIssueActions().getUpdateAuditUser());
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchMapper batchMapper = session.getMapper(BatchMapper.class);
            InsertEntity result = batchMapper.insertReliabilityIncidence(
                    dto.getJobName(),
                    dto.getOrderDate(),
                    dto.getOrderId(),
                    dto.getErrorType(),
                    dto.getErrorReason(),
                    dto.getSolutionDetail(),
                    dto.getEmployeeId(),
                    dto.getLogArgos(),
                    dto.getRunCounter(),
                    dto.getTicketJira(),
                    request.getIssueActionsId(),
                    request.getJobId(),
                    request.getFolderName(),
                    request.getDevEmail(),
                    convertStringToDate(request.getEndDate(), "yyyy-MM-dd"),
                    request.getStatusType(),
                    request.getCommentActionsDesc(),
                    request.getCreateAuditUser(),
                    request.getUpdateAuditUser()
            );
            session.commit();
            //return dto;
            return result;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public DataResult<InsertCSATJobExecutionResponseDTO>insertCSATJobExecutionRequest(List<InsertCSATJobExecutionRequest> dto) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                BatchMapper batchMapper = session.getMapper(BatchMapper.class);
                InsertEntity result = null;
                for (InsertCSATJobExecutionRequest request : dto) {
                    result = batchMapper.insertCSATJobExecution(request);
                }
                session.commit();
                return new SuccessDataResult(result);
            }
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500",e.getMessage());
        }
    }
    public DataResult<InsertAJIFJobExecutionResponseDTO>insertAJIFJobExecutionRequest(List<InsertAJIFJobExecutionRequest> dto) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                BatchMapper batchMapper = session.getMapper(BatchMapper.class);
                InsertEntity result = null;
                for (InsertAJIFJobExecutionRequest request : dto) {
                    result = batchMapper.insertAJIFJobExecution(request);
                }
                session.commit();
                return new SuccessDataResult(result);
            }
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500",e.getMessage());
        }
    }

    public List<StatusJobExecutionDTO> getStatusJobExecution(String jobName, Integer quantity) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchMapper batchMapper = session.getMapper(BatchMapper.class);
            List<StatusJobExecutionDTO> result = batchMapper.getStatusJobExecution(jobName, quantity);
            return result;
        }
    }

    public JobExecutionByIdDTO getJobExecutionById(String folder, String orderId, String jobName) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchMapper batchMapper = session.getMapper(BatchMapper.class);
            JobExecutionByIdDTO result = batchMapper.getJobExecutionById(folder, orderId, jobName);
            return result;
        }
    }

    public BatchIssuesActionFilterDtoResponse filterIssueAction(BatchIssuesActionFilterDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<BatchIssuesActionSelectDtoResponse> lista;
        int recordsCount = 0;
        int pagesAmount = 0;
        BatchIssuesActionFilterDtoResponse response = new BatchIssuesActionFilterDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchMapper mapper = session.getMapper(BatchMapper.class);
            lista = mapper.filterIssueAction(
                    dto.getDomainId(),
                    dto.getProjectId(),
                    dto.getJobName(),
                    dto.getFolderName(),
                    dto.getStatusType());
        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecordsAmount() > 0 ? (int) Math.ceil((float) recordsCount / dto.getRecordsAmount().floatValue()) : 1;

        if (dto.getRecordsAmount() > 0) {
            lista = lista.stream()
                    .skip(dto.getRecordsAmount() * (dto.getPage() - 1))
                    .limit(dto.getRecordsAmount())
                    .collect(Collectors.toList());
        }

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);

        return response;
    }
}

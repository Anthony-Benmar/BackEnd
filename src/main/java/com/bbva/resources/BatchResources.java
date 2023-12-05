package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BatchDao;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BatchMapper;
import com.bbva.dto.batch.request.InsertCSATJobExecutionRequest;
import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.InsertCSATJobExecutionResponseDTO;
import com.bbva.dto.batch.response.JobExecutionFilterResponseDTO;
import com.bbva.entities.InsertEntity;
import com.bbva.service.BatchService;
import com.bbva.util.Helper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/batch")
@Produces(MediaType.APPLICATION_JSON)
public class BatchResources {

    private BatchService batchService = new BatchService();
    private Helper helper = new Helper();
    private static final Logger log = Logger.getLogger(BatchResources.class.getName());

    @POST
    @Path("/job_execution_cstat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertCSATJobExecutionResponseDTO> createCSAUTJobExecution(List<InsertCSATJobExecutionRequest> requests){
        IDataResult<InsertCSATJobExecutionResponseDTO>  result = batchService.insertCSATJobExecution(requests);
        return result;
    }

    @GET
    @Path("/job_execution/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobExecutionFilterResponseDTO> filter(
            @QueryParam("recordsAmount") String recordsAmount,
            @QueryParam("page") String page,
            @QueryParam("jobName") String jobName,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("folder") String folder,
            @QueryParam("dataproc") String dataproc,
            @QueryParam("orderId") String orderId,
            @QueryParam("projectName") String projectName,
            @QueryParam("sdatoolId") String sdatoolId,
            @QueryParam("domain") String domain,
            @QueryParam("isTypified") Boolean isTypified
    ){
        JobExecutionFilterRequestDTO dto = new JobExecutionFilterRequestDTO();
        Integer recordsAmountFinal = helper.parseIntegerOrDefault(recordsAmount, 10);
        Integer pageFinal = helper.parseIntegerOrDefault(page, 1);

        dto.setRecords_amount(recordsAmountFinal);
        dto.setPage(pageFinal);
        dto.setJobName(jobName);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setFolder(folder);
        dto.setDataproc(dataproc);
        dto.setOrderId(orderId);
        dto.setProjectName(projectName);
        dto.setSdatoolId(sdatoolId);
        dto.setDomain(domain);
        dto.setIsTypified(isTypified);
        IDataResult<JobExecutionFilterResponseDTO>  result = batchService.filter(dto);
        return result;
    }

    @POST
    @Path("/reliability_incidence")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertReliabilityIncidenceDTO> insertReliabilityIncidence(InsertReliabilityIncidenceDTO request){
        IDataResult<InsertReliabilityIncidenceDTO>  result = batchService.insertReliabilityIncidence(request);
        return result;
    }
}

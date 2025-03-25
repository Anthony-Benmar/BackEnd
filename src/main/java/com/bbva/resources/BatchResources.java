package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.batch.request.*;
import com.bbva.dto.batch.response.*;
import com.bbva.service.BatchService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/batch")
@Produces(MediaType.APPLICATION_JSON)
public class BatchResources {
    private BatchService batchService = new BatchService();
    private Helper helper = new Helper();

    @GET
    @Path("/lastJobExecutionStatusDate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<String> getLastJobExecutionStatusDate() {
        return batchService.getLastJobExecutionStatusDate();
    }

    @GET
    @Path("/{jobName}/job/{quantity}/executions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<StatusJobExecutionDTO>> getStatusJobExecution(
            @PathParam("jobName") String jobName,
            @PathParam("quantity") Integer quantity
    ){
        return batchService.getStatusJobExecution(jobName, quantity);
    }

    @POST
    @Path("/job_execution/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult saveJobExecutionStatus(List<InsertJobExecutionStatusRequest> request){
        return batchService.saveJobExecutionStatus(request);
    }

    @POST
    @Path("/job_execution/active")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult saveJobExecutionActive(List<InsertJobExecutionActiveRequest> request){
        return batchService.saveJobExecutionActive(request);
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
        return batchService.filter(dto);
    }

    @POST
    @Path("/reliability_incidence")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertReliabilityIncidenceDTO> insertReliabilityIncidence(InsertReliabilityIncidenceDTO request){
        return batchService.insertReliabilityIncidence(request);
    }

    @GET
    @Path("/Typified_job/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertAJIFJobExecutionResponseDTO> createAJIFJobExecution(List<InsertJobExecutionStatusRequest> requests) {
        return batchService.insertAJIFJobExecution(requests);
    }

    @POST
    @Path("/job_execution_by_id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobExecutionByIdDTO> getJobExecutionById(JobExecutionByIdRequest request){
        return batchService.getJobExecutionById(request.getFolder(),
                request.getOrderId(), request.getJobName(), request.getRunCounter());
    }

    @GET
    @Path("/issue_action/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<BatchIssuesActionFilterDtoResponse> filterIssueAction(BatchIssuesActionFilterDtoRequest dto){
        return batchService.filterIssueAction(dto);
    }


}

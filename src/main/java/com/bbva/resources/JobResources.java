package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.job.request.*;
import com.bbva.dto.job.response.JobAdditionalDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoByIdDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.dto.job.response.JobMonitoringDtoResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.service.JobService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/job")
@Produces(MediaType.APPLICATION_JSON)
public class JobResources {
    private final JobService jobService = new JobService();

    @POST
    @Path("/job-basic-info/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobBasicInfoFilterDtoResponse> jobBasicInfoFilter(JobBasicInfoFilterDtoRequest dto)
            throws ExecutionException, InterruptedException {
        return jobService.jobBasicInfoFilter(dto);
    }

    @GET
    @Path("/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<JobDTO>> getJobById(@PathParam("jobId") int jobId) {
        return jobService.getJobById(jobId);
    }

    @PUT
    @Path("/additional/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobAdditionalDtoResponse> updateAdditional(JobAdditionalDtoRequest dto)
            throws ExecutionException, InterruptedException
    {
        return jobService.updateAdditional(dto);
    }

    @GET
    @Path("/monitoring/request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<JobMonitoringDtoResponse>> getAllMonitoringRequest() {
        return jobService.getAllMonitoringRequest();
    }

    @POST
    @Path("/monitoring/request/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobMonitoringDtoResponse> insertMonitoringRequest(JobMonitoringRequestInsertDtoRequest dto)
            throws ExecutionException, InterruptedException {
        return jobService.insertMonitoringRequest(dto);
    }
    @PUT
    @Path("/monitoring/request/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobMonitoringDtoResponse> updateMonitoringRequest(JobMonitoringDtoRequest dto)
            throws ExecutionException, InterruptedException {
        return jobService.updateMonitoringRequest(dto);
    }
    @DELETE
    @Path("/monitoring/request/delete/{monitoringRequestId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobMonitoringDtoResponse> deleteMonitoringRequest(@PathParam("monitoringRequestId") Integer monitoringRequestId)
            throws ExecutionException, InterruptedException {
        return jobService.deleteMonitoringRequest(monitoringRequestId);
    }

}

package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.job.request.*;
import com.bbva.dto.job.response.JobAdditionalDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.dto.job.response.JobMonitoringUpdateDtoResponse;
import com.bbva.dto.job.response.*;
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
    public IDataResult<List<JobMonitoringUpdateDtoResponse>> getAllMonitoringRequest() {
        return jobService.getAllMonitoringRequest();
    }
    @POST
    @Path("/monitoring/request/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobMonitoringRequestFilterDtoResponse> filterMonitoringRequest(JobMonitoringRequestFilterDtoRequest dto)
            throws ExecutionException, InterruptedException {
        return jobService.filterMonitoringRequest(dto);
    }

    @POST
    @Path("/monitoring/request/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobMonitoringUpdateDtoResponse> insertMonitoringRequest(JobMonitoringRequestInsertDtoRequest dto)
            throws ExecutionException, InterruptedException {
        return jobService.insertMonitoringRequest(dto);
    }
    @PUT
    @Path("/monitoring/request/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobMonitoringUpdateDtoResponse> updateMonitoringRequest(JobMonitoringUpdateDtoRequest dto)
            throws ExecutionException, InterruptedException {
        System.out.println("updateMonitoringRequest");
        return jobService.updateMonitoringRequest(dto);
    }
}

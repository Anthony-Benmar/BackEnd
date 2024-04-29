package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.job.request.JobAdditionalDtoRequest;
import com.bbva.dto.job.request.JobBasicInfoFilterDtoRequest;
import com.bbva.dto.job.response.JobAdditionalDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoByIdDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.service.JobBasicInfoService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

@Path("/job")
@Produces(MediaType.APPLICATION_JSON)
public class JobResources {
    private final JobBasicInfoService jobBasicInfoService = new JobBasicInfoService();

    @POST
    @Path("/job-basic-info/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobBasicInfoFilterDtoResponse> jobBasicInfoFilter(JobBasicInfoFilterDtoRequest dto)
            throws ExecutionException, InterruptedException {
        return jobBasicInfoService.jobBasicInfoFilter(dto);
    }
    @GET
    @Path("/job-basic-info/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobBasicInfoByIdDtoResponse> jobBasicDetail(@PathParam("jobId") Integer jobId)
            throws ExecutionException, InterruptedException {
        return jobBasicInfoService.jobBasicDetail(jobId);
    }

    @GET
    @Path("/job-basic-info/additional/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobAdditionalDtoResponse> getAdditional(@PathParam("jobId") Integer jobId)
            throws ExecutionException, InterruptedException {
        return jobBasicInfoService.getAdditional(jobId);
    }

    @PUT
    @Path("/job-basic-info/additional/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobAdditionalDtoResponse> updateAdditional(JobAdditionalDtoRequest dto)
            throws ExecutionException, InterruptedException
    {
        return jobBasicInfoService.updateAdditional(dto);
    }

}

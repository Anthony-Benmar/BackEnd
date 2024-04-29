package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.job.request.JobBasicInfoFilterDtoRequest;
import com.bbva.dto.job.request.JobDTO;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
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
}

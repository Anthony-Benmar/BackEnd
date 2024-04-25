package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.job.request.JobBasicInfoFilterDtoRequest;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.service.JobBasicInfoService;
import com.bbva.service.MeshService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}

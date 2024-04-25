package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.job.response.JobBasicInfoDtoResponse;
import com.bbva.dto.mesh.request.MeshDtoRequest;
import com.bbva.dto.mesh.response.MeshRelationalDtoResponse;
import com.bbva.service.JobBasicInfoService;
import com.bbva.service.MeshService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/mesh")
public class MeshResources {
    private final MeshService meshService = new MeshService();

    @POST
    @Path("/jobs-dependencies")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<MeshRelationalDtoResponse>> jobsdependencies(MeshDtoRequest dto)
    {
        return meshService.jobsdependencies(dto);
    }

    private final JobBasicInfoService jobBasicInfoService = new JobBasicInfoService();

    @POST
    @Path("/job-basic-info/listAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<JobBasicInfoDtoResponse>> listAllJobs()
            throws ExecutionException, InterruptedException {
        return jobBasicInfoService.listAllJobs();
    }
}

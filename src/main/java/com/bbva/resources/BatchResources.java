package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.JobExecutionFilterResponseDTO;
import com.bbva.service.BatchService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/batch")
@Produces(MediaType.APPLICATION_JSON)
public class BatchResources {

    private BatchService batchService = new BatchService();

    @POST
    @Path("/job_execution/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<JobExecutionFilterResponseDTO> filter(JobExecutionFilterRequestDTO request){
            IDataResult<JobExecutionFilterResponseDTO>  result = batchService.filter(request);
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

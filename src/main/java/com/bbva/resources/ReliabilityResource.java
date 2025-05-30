package com.bbva.resources;


import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.reliability.request.ExecutionValidationInputsDtoRequest;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.request.TransferInputDtoRequest;
import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;
import com.bbva.service.ReliabilityService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/reliability")
@Produces(MediaType.APPLICATION_JSON)
public class ReliabilityResource {
    private final ReliabilityService reliabilityService = new ReliabilityService();

    @POST
    @Path("/info/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InventoryInputsFilterDtoResponse> inventoryInputsFilter(InventoryInputsFilterDtoRequest dto) {
        return reliabilityService.inventoryInputsFilter(dto);
    }
    @GET
    @Path("/pending_custody_jobs/{sdatoolId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<PendingCustodyJobsDtoResponse>> getPendingCustodyJobs(@Context HttpStatusCodes request,
                                                                                  @PathParam("sdatoolId") String sdatoolId)
    {
        return reliabilityService.getPendingCustodyJobs(sdatoolId);
    }
    @GET
    @Path("/project_custody_info/{sdatoolId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectCustodyInfoDtoResponse>> getProjectCustodyInfo(@Context HttpServletRequest request,
                                                                                  @PathParam("sdatoolId") String sdatoolId)
    {
        return reliabilityService.getProjectCustodyInfo(sdatoolId);
    }
    @GET
    @Path("/execution_validation/{jobName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ExecutionValidationDtoResponse> getExecutionValidation(@Context HttpServletRequest request,
                                                                              @PathParam("jobName") String jobName)
    {
        return reliabilityService.getExecutionValidation(jobName);
    }

    @POST
    @Path("/job/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> updateInventoryJobStock(InventoryJobUpdateDtoRequest dto) {
        return reliabilityService.updateInventoryJobStock(dto);
    }

    @POST
    @Path("/transfer/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> insertTransfer(TransferInputDtoRequest dto) {
        return reliabilityService.insertTransfer(dto);
    }

}

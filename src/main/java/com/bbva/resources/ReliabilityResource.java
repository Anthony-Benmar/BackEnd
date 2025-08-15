package com.bbva.resources;


import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import com.bbva.service.ReliabilityService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/reliability")
@Produces(MediaType.APPLICATION_JSON)
public class ReliabilityResource {
    private final ReliabilityService reliabilityService = new ReliabilityService();
    private static final String CONTENTDISPOSITION = "Content-Disposition";

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
    @Path("/execution_history/{jobName}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<JobExecutionHistoryDtoResponse>> getExecutionHistory(
            @PathParam("jobName") String jobName
    ) {
        return reliabilityService.getJobExecutionHistory(jobName);
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
    @Path("/execution_validation_all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ExecutionValidationAllDtoResponse>> getExecutionValidationAll(@Context HttpServletRequest request,
                                                                                          List<String> jobsNames)
    {
        return reliabilityService.getExecutionValidationAll(jobsNames);
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

    @GET
    @Path("/sn2-options/{sn1}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<DropDownDto>> sn2Options(@PathParam("sn1") Integer sn1) {
        return reliabilityService.getSn2Options(sn1);
    }

    @POST
    @Path("/documentGenerator/inventory")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response generateDocumentMeshTracking(InventoryInputsFilterDtoRequest dto) {
        byte[] documentoModificado = reliabilityService.generateDocumentInventory(dto);
        String nombreDocumento = "job_control";
        return Response.ok(documentoModificado)
                .header(CONTENTDISPOSITION, "attachment; filename=\"Inventario_" + nombreDocumento + "_v1.xlsx\"")
                .header("Access-Control-Expose-Headers", CONTENTDISPOSITION)
                .build();
    }
    @POST
    @Path("/info/reliability_packs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationReliabilityPackResponse> getReliabilityPacks(ReliabilityPackInputFilterRequest dto)
    {
        return reliabilityService.getReliabilityPacks(dto);
    }

    @PUT
    @Path("/update_status/reliability_packs_job_stock")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> updateStatusReliabilityPacksJobStock(@Context HttpServletRequest request,
                                                 List<String> packs)
    {
        return reliabilityService.updateStatusReliabilityPacksJobStock(packs);
    }

    @GET
    @Path("/origin-types")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<DropDownDto>> getOriginTypes() {
        return reliabilityService.getOriginTypes();
    }

    @POST
    @Path("/info/reliability_packs_v2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationReliabilityPackResponse> getReliabilityPacksV2(
            ReliabilityPackAdvancedFilterRequest dto,
            @HeaderParam("X-USER-ROLE") String roleFromHeader   // <-- nuevo
    ) {
        if (dto.getRole() == null || dto.getRole().isBlank()) {
            dto.setRole(roleFromHeader);
        }
        return reliabilityService.getReliabilityPacksAdvanced(dto);
    }
}

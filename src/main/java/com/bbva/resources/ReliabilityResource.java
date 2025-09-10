package com.bbva.resources;


import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
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
import java.util.Map;

@Path("/reliability")
@Produces(MediaType.APPLICATION_JSON)
public class ReliabilityResource {
    private final ReliabilityService reliabilityService = new ReliabilityService();
    private static final String CONTENTDISPOSITION = "Content-Disposition";
    private static final String ACTOR_ROLE_KEY = "actorRole";

    @POST
    @Path("/info/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InventoryInputsFilterDtoResponse> inventoryInputsFilter(InventoryInputsFilterDtoRequest dto) {
        return reliabilityService.inventoryInputsFilter(dto);
    }

    @GET
    @Path("/projects/sdatools/active")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<String>> listActiveSdatools() {
        return reliabilityService.listActiveSdatools();
    }

    @PUT
    @Path("/jobs/{jobName}/sdatool/{newSdatoolId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> updateJobSdatool(
            @PathParam("jobName") String jobName,
            @PathParam("newSdatoolId") String newSdatoolId
    ) { return reliabilityService.updateJobSdatool(jobName, newSdatoolId); }

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
            ReliabilityPackInputFilterRequest dto,
            @HeaderParam("X-USER-ROLE")  String roleFromHeader,
            @HeaderParam("X-USER-EMAIL") String emailFromHeader
    ) {
        if (dto.getRole()==null || dto.getRole().isBlank()) dto.setRole(roleFromHeader);
        return reliabilityService.getReliabilityPacksAdvanced(dto, emailFromHeader);
    }

    @PUT
    @Path("/transfers/{pack}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<TransferStatusChangeResponse> changeTransferStatus(
            @PathParam("pack") String pack,
            TransferStatusChangeRequest request
    ) {
        return reliabilityService.changeTransferStatus(pack, request);
    }

    @PUT
    @Path("/transfers/{pack}/jobs/{jobName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> updateJobBySm(
            @PathParam("pack") String pack,
            @PathParam("jobName") String jobName,
            UpdateJobDtoRequest body,
            @HeaderParam("X-USER-ROLE") String roleFromHeader
    ) {
        body.setPack(pack);
        body.setJobName(jobName);
        if (body.getActorRole()==null || body.getActorRole().isBlank()) {
            body.setActorRole(roleFromHeader);
        }
        return reliabilityService.updateJobBySm(body);
    }

    @PUT
    @Path("/transfers/{pack}/comments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> updateCommentsForPack(
            @PathParam("pack") String pack,
            Map<String, String> body,
            @HeaderParam("X-USER-ROLE") String roleFromHeader
    ) {
        String fallbackRole = (roleFromHeader == null) ? "" : roleFromHeader;
        String roleInBody = (body != null) ? body.get(ACTOR_ROLE_KEY) : null;
        String actor = (roleInBody != null && !roleInBody.isBlank())
                ? roleInBody
                : fallbackRole;
        String comments = (body != null) ? body.get("comments") : null;
        return reliabilityService.updateCommentsForPack(pack, actor, comments);
    }

    @GET
    @Path("/transfers/{pack}/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<TransferDetailResponse> getTransferDetail(@PathParam("pack") String pack) {
        return reliabilityService.getTransferDetail(pack);
    }

    @PUT
    @Path("/transfers/{pack}/detail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<TransferDetailResponse> updateTransferDetail(
            @PathParam("pack") String pack,
            TransferDetailUpdateRequest body,
            @HeaderParam("X-USER-ROLE") String roleFromHeader) {
        return reliabilityService.updateTransferDetail(pack, roleFromHeader, body);
    }

    @GET
    @Path("/services/{service}/can_delete")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Boolean> canDelete(@PathParam("service") String service) {
        ServicePermissionResponse dto = reliabilityService.getServicePermissionByName(service);
        boolean flag = dto != null && Boolean.TRUE.equals(dto.getCanDeleteJobs());
        return new SuccessDataResult<>(flag);
    }
}

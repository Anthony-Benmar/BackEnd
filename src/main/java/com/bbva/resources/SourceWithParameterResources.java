package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import com.bbva.service.SourceWithParameterService;
import com.bbva.util.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;

@Path("/sourcesWithParameter")
@Produces(MediaType.APPLICATION_JSON)
public class SourceWithParameterResources {
    private static final Logger LOGGER = Logger.getLogger(SourceWithParameterResources.class.getName());
    private static final String INTERNAL_SERVER_ERROR_JSON = "{\"error\":\"Internal Server Error\"}";
    private SourceWithParameterService sourceWithParameterService = new SourceWithParameterService();
    private final Helper helper = new Helper();
    private static final String MESSAGE_KEY = "message";
    private Response buildJsonResponse(Map<String, Object> data) {
        try {
            String json = new ObjectMapper().writeValueAsString(data);
            return Response.ok(json).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(INTERNAL_SERVER_ERROR_JSON).build();
        }
    }
    private Response validateDto(SourceWithParameterDataDtoResponse dto) {
        if (dto == null || dto.getId() == null || dto.getId().isEmpty() ||
                dto.getUserId() == null || dto.getUserId().isEmpty() ||
                dto.getUserName() == null || dto.getUserName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"id, userId y userName son obligatorios\"}")
                    .build();
        }
        return null;
    }

    @GET
    @Path("/getSourceWithParameter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceWithParameterPaginatedResponseDTO> getSourceWithParameter(
            @QueryParam("limit") String limit,
            @QueryParam("offset") String offset,
            @QueryParam("id") String id,
            @QueryParam("tdsSource") String tdsSource,
            @QueryParam("uuaaMaster") String uuaaMaster,
            @QueryParam("modelOwner") String modelOwner,
            @QueryParam("status") String status,
            @QueryParam("originType") String originType,
            @QueryParam("tdsOpinionDebt") String tdsOpinionDebt,
            @QueryParam("effectivenessDebt") String effectivenessDebt
    ) {
        LOGGER.info("getSourceWithParameter paginated request");
        Integer limitFinal = helper.parseIntegerOrDefault(limit, 30);
        Integer offsetFinal = helper.parseIntegerOrDefault(offset, 0);
        SourceWithParameterPaginationDtoRequest dto = new SourceWithParameterPaginationDtoRequest();
        dto.setLimit(limitFinal);
        dto.setOffset(offsetFinal);
        dto.setId(id);
        dto.setTdsSource(tdsSource);
        dto.setUuaaMaster(uuaaMaster);
        dto.setModelOwner(modelOwner);
        dto.setStatus(status);
        dto.setOriginType(originType);
        dto.setTdsOpinionDebt(tdsOpinionDebt);
        dto.setEffectivenessDebt(effectivenessDebt);

        return sourceWithParameterService.getSourceWithParameter(dto);
    }
    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceWithParameterReadOnlyDtoResponse> readOnly(SourceWithReadyOnlyDtoRequest request) {
        LOGGER.info("SingleBase readonly request");
        return sourceWithParameterService.readOnly(request);
    }
    @GET
    @Path("/getDistinctStatuses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctStatuses() {
        LOGGER.info("getDistinctStatuses request");
        return sourceWithParameterService.getDistinctStatuses();
    }

    @GET
    @Path("/getDistinctOriginTypes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctOriginTypes() {
        LOGGER.info("getDistinctOriginTypes request");
        return sourceWithParameterService.getDistinctOriginTypes();
    }
    @GET
    @Path("/getDistinctTdsOpinionDebts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctTdsOpinionDebts() {
        LOGGER.info("getDistinctTdsOpinionDebts request");
        return sourceWithParameterService.getDistinctTdsOpinionDebts();
    }

    @GET
    @Path("/getDistinctEffectivenessDebts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctEffectivenessDebts() {
        LOGGER.info("getDistinctEffectivenessDebts request");
        return sourceWithParameterService.getDistinctEffectivenessDebts();
    }
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Boolean> update(SourceWithParameterDataDtoResponse dto) {
        return sourceWithParameterService.updateSourceWithParameter(dto);
    }


    @GET
    @Path("/exportComment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportComment(@QueryParam("sourceId") String sourceId,
                                  @QueryParam("type") String commentType) {
        if (sourceId == null || sourceId.isEmpty() || commentType == null || commentType.isEmpty()) {
            return Response.status(400)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }

        List<String> comments = sourceWithParameterService.exportCommentsBySourceId(sourceId, commentType);

        if (comments == null || comments.isEmpty()) {
            return Response.status(404)
                    .entity("{\"error\":\"No comment found for the given sourceId and type\"}")
                    .build();
        }

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("comments", comments);

            String json = new ObjectMapper().writeValueAsString(response);
            return Response.ok(json).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }
    }
    @POST
    @Path("/saveComment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response saveComment(@FormParam("sourceId") String sourceId,
                                @FormParam("type") String commentType,
                                @FormParam("comment") String comment) {
        if (sourceId == null || sourceId.isEmpty() ||
                commentType == null || commentType.isEmpty() ||
                comment == null || comment.isEmpty()) {
            return Response.status(400)
                    .entity("{\"error\":\"sourceId, commentType or comment is missing\"}")
                    .build();
        }

        try {
            sourceWithParameterService.saveComment(sourceId, commentType, comment);
            return buildJsonResponse(Map.of(MESSAGE_KEY, "Comment saved successfully"));
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }

    }
    @POST
    @Path("/saveModifyHistory")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveModifyHistory(SourceWithParameterDataDtoResponse dto) {

        Response validation = validateDto(dto);
        if (validation != null) return validation;

        try {
            sourceWithParameterService.saveModifyHistory(dto);
            return buildJsonResponse(Map.of(MESSAGE_KEY, "Histórico guardado correctamente"));
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }
    }
    @POST
    @Path("/insertSource")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertSource(SourceWithParameterDataDtoResponse dto) {
        Response validation = validateDto(dto);
        if (validation != null) return validation;

        boolean inserted = sourceWithParameterService.insertSource(dto);
        if (inserted) {
            return buildJsonResponse(Map.of(MESSAGE_KEY, "Registro insertado correctamente"));
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"El ID ya existe o no se pudo insertar\"}")
                    .build();
        }

    }
    @GET
    @Path("/getMaxSourceId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMaxSourceId() {
        LOGGER.info("getMaxSourceId request");
        try {
            String maxSourceId = sourceWithParameterService.getMaxSourceId();
            Map<String, Object> response = new HashMap<>();
            response.put("maxSourceId", maxSourceId);
            return Response.ok(new ObjectMapper().writeValueAsString(response)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }
    }

    @GET
    @Path("/existsReplacementId/{replacementId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response existsReplacementId(@PathParam("replacementId") String replacementId) {
        try {
            boolean exists = sourceWithParameterService.existsReplacementId(replacementId);
            Map<String, Object> response = Map.of(
                    "replacementId", replacementId,
                    "exists", exists
            );
            return Response.ok(new ObjectMapper().writeValueAsString(response)).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }
    }

    @GET
    @Path("/status/{sourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatusById(@PathParam("sourceId") String sourceId) {
        try {
            String status = sourceWithParameterService.getStatusById(sourceId);
            if (status == null) {
                return Response.status(404)
                        .entity("{\"error\":\"No se encontró el ID\"}")
                        .build();
            }
            Map<String, Object> response = Map.of(
                    "sourceId", sourceId,
                    "status", status
            );
            return Response.ok(new ObjectMapper().writeValueAsString(response)).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(INTERNAL_SERVER_ERROR_JSON)
                    .build();
        }
    }


}

package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import com.bbva.service.SourceWithParameterService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/sourcesWithParameter")
@Produces(MediaType.APPLICATION_JSON)
public class SourceWithParameterResources {
    private static final Logger LOGGER = Logger.getLogger(SourceWithParameterResources.class.getName());

    private SourceWithParameterService sourceWithParameterService = new SourceWithParameterService();
    private final Helper helper = new Helper();

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
}

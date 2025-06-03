package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
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
            @QueryParam("tdsDescription") String tdsDescription,
            @QueryParam("tdsSource") String tdsSource,
            @QueryParam("replacementId") String replacementId,
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
        dto.setTdsDescription(tdsDescription);
        dto.setTdsSource(tdsSource);
        dto.setReplacementId(replacementId);
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
    @Path("/getDistinctTdsDescriptions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctTdsDescriptions() {
        LOGGER.info("getDistinctTdsDescriptions request");
        return sourceWithParameterService.getDistinctTdsDescriptions();
    }
    @GET
    @Path("/getDistinctTdsSources")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctTdsSources() {
        LOGGER.info("getDistinctTdsSources request");
        return sourceWithParameterService.getDistinctTdsSources();
    }
    @GET
    @Path("/getDistinctReplacementIds")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDistinctReplacementIds() {
        LOGGER.info("getDistinctReplacementIds request");
        return sourceWithParameterService.getDistinctReplacementIds();
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

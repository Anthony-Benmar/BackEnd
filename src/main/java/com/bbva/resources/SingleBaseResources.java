package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.dto.single_base.response.SingleBaseReadOnlyDtoResponse;
import com.bbva.service.SingleBaseService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/singleBase")
@Produces(MediaType.APPLICATION_JSON)
public class SingleBaseResources {
    private static final Logger LOGGER = Logger.getLogger(SingleBaseResources.class.getName());
    private final SingleBaseService singleBaseService = new SingleBaseService();
    private final Helper helper = new Helper();

    @GET
    @Path("/getSingleBase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SingleBasePaginatedResponseDTO> getBaseUnicaWithSource(
            @QueryParam("limit") String limit,
            @QueryParam("offset") String offset,
            @QueryParam("projectName") String projectName,
            @QueryParam("tipoFolio") String tipoFolio,
            @QueryParam("folio") String folio
    ) {
        LOGGER.info("SingleBase paginated request");
        Integer limitFinal = helper.parseIntegerOrDefault(limit, 30);
        Integer offsetFinal = helper.parseIntegerOrDefault(offset, 0);

        SingleBasePaginationDtoRequest dto = new SingleBasePaginationDtoRequest();
        dto.setLimit(limitFinal);
        dto.setOffset(offsetFinal);
        dto.setProjectName(projectName);
        dto.setTipoFolio(tipoFolio);
        dto.setFolio(folio);

        return singleBaseService.getBaseUnicaWithSource(dto);
    }

    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SingleBaseReadOnlyDtoResponse> readOnly(SingleBaseReadOnlyDtoRequest request){
        LOGGER.info("SingleBase readonly request");
        return singleBaseService.readOnly(request);
    }

    // Endpoints para combos
    @GET
    @Path("/distinct/folios")
    public List<String> getDistinctFolios() {
        return singleBaseService.getDistinctFolios();
    }

    @GET
    @Path("/distinct/projectNames")
    public List<String> getDistinctProjectNames() {
        return singleBaseService.getDistinctProjectNames();
    }

    @GET
    @Path("/distinct/registeredFolioDates")
    public List<java.sql.Date> getDistinctRegisteredFolioDates() {
        return singleBaseService.getDistinctRegisteredFolioDates();
    }

    @GET
    @Path("/distinct/statusFolioTypes")
    public List<String> getDistinctStatusFolioTypes() {
        return singleBaseService.getDistinctStatusFolioTypes();
    }

    @GET
    @Path("/distinct/folioTypes")
    public List<String> getDistinctFolioTypes() {
        return singleBaseService.getDistinctFolioTypes();
    }
}
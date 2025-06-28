package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.request.EfectivityBaseReadOnlyDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataReadOnlyDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;
import com.bbva.service.EfectivityBaseService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/efectivityBase")
@Produces(MediaType.APPLICATION_JSON)
public class EfectivityResources {
    private static final Logger LOGGER = Logger.getLogger(EfectivityResources.class.getName());
    private final EfectivityBaseService efectivityBaseService = new EfectivityBaseService();
    private final Helper helper = new Helper();

    @GET
    @Path("/getEfectivityBase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<EfectivityBasePaginatedResponseDTO> getBaseEfectivityWithSource(
            @QueryParam("limit") String limit,
            @QueryParam("offset") String offset,
            @QueryParam("sdatoolProject") String sdatoolProject,
            @QueryParam("sprintDate") String sprintDate,
            @QueryParam("registerDate") String registerDate,
            @QueryParam("efficiency") String efficiency
    ) {
        LOGGER.info("SingleBase paginated request");
        Integer limitFinal = helper.parseIntegerOrDefault(limit, 30);
        Integer offsetFinal = helper.parseIntegerOrDefault(offset, 0);
        EfectivityBasePaginationDtoRequest dto = new EfectivityBasePaginationDtoRequest();
        dto.setLimit(limitFinal);
        dto.setOffset(offsetFinal);
        dto.setSdatoolProject(sdatoolProject);
        dto.setSprintDate(sprintDate);
        dto.setRegisterDate(registerDate);
        dto.setEfficiency(efficiency);
        return efectivityBaseService.getBaseEfectivityWithSource(dto);
    }
    // Endpoints para combos
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/distinct/sdatoolProjects")
    public List<String> getDistinctSdatoolProjects() {
        return efectivityBaseService.getDistinctSdatoolProjects();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/distinct/sprintDates")
    public List<String> getDistinctSprintDates() {
        return efectivityBaseService.getDistinctSprintDates();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/distinct/registerDates")
    public List<java.sql.Date> getDistinctRegisterDates() {
        return efectivityBaseService.getDistinctRegisterDates();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/distinct/efficiencies")
    public List<String> getDistinctEfficiencies() {
        return efectivityBaseService.getDistinctEfficiencies();
    }

    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<EfectivityBaseDataReadOnlyDtoResponse> readOnly(EfectivityBaseReadOnlyDtoRequest request) {
        return efectivityBaseService.readOnly(request);
    }
}
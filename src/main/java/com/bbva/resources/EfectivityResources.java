package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;
import com.bbva.service.EfectivityBaseService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/efectivityBase")
@Produces(MediaType.APPLICATION_JSON)
public class EfectivityResources {
    private final EfectivityBaseService efectivityBaseService = new EfectivityBaseService();

    @GET
    @Path("/getEfectivityBase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<EfectivityBasePaginatedResponseDTO> getBaseEfectivityWithSource(
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset,
            @QueryParam("sdatoolProject") String sdatoolProject,
            @QueryParam("sprintDate") String sprintDate,
            @QueryParam("registerDate") String registerDate,
            @QueryParam("efficiency") String efficiency
    ) {
        EfectivityBasePaginationDtoRequest dto = new EfectivityBasePaginationDtoRequest();
        dto.setLimit(limit);
        dto.setOffset(offset);
        dto.setSdatoolProject(sdatoolProject);
        dto.setSprintDate(sprintDate);
        dto.setRegisterDate(registerDate);
        dto.setEfficiency(efficiency);

        return efectivityBaseService.getBaseEfectivityWithSource(dto);
    }

    // Endpoints para combos
    @GET
    @Path("/distinct/sdatoolProjects")
    public List<String> getDistinctSdatoolProjects() {
        return efectivityBaseService.getDistinctSdatoolProjects();
    }

    @GET
    @Path("/distinct/sprintDates")
    public List<String> getDistinctSprintDates() {
        return efectivityBaseService.getDistinctSprintDates();
    }

    @GET
    @Path("/distinct/registerDates")
    public List<java.sql.Date> getDistinctRegisterDates() {
        return efectivityBaseService.getDistinctRegisterDates();
    }

    @GET
    @Path("/distinct/efficiencies")
    public List<String> getDistinctEfficiencies() {
        return efectivityBaseService.getDistinctEfficiencies();
    }
}
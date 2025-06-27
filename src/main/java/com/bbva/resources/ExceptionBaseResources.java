package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.request.ExceptionBaseReadOnlyDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBasePaginatedResponseDTO;
import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;
import com.bbva.service.ExceptionBaseService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/exceptionBase")
@Produces(MediaType.APPLICATION_JSON)
public class ExceptionBaseResources {
    private static final Logger LOGGER = Logger.getLogger(ExceptionBaseResources.class.getName());
    private final ExceptionBaseService exceptionBaseService = new ExceptionBaseService();
    private final Helper helper = new Helper();

    @GET
    @Path("/getExceptions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ExceptionBasePaginatedResponseDTO> getExceptionsWithSource(
            @QueryParam("limit") String limit,
            @QueryParam("offset") String offset,
            @QueryParam("requestingProject") String requestingProject,
            @QueryParam("approvalResponsible") String approvalResponsible,
            @QueryParam("registrationDate") String registrationDate,
            @QueryParam("quarterYearSprint") String quarterYearSprint
    ) {
        LOGGER.info("ExceptionBase paginated request");
        Integer limitFinal = helper.parseIntegerOrDefault(limit, 30);
        Integer offsetFinal = helper.parseIntegerOrDefault(offset, 0);

        ExceptionBasePaginationDtoRequest dto = new ExceptionBasePaginationDtoRequest();
        dto.setLimit(limitFinal);
        dto.setOffset(offsetFinal);
        dto.setRequestingProject(requestingProject);
        dto.setApprovalResponsible(approvalResponsible);
        dto.setRegistrationDate(registrationDate);
        dto.setQuarterYearSprint(quarterYearSprint);

        return exceptionBaseService.getExceptionsWithSource(dto);
    }

    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ExceptionBaseReadOnlyDtoResponse> readOnly(ExceptionBaseReadOnlyDtoRequest request){
        LOGGER.info("ExceptionBase readonly request");
        return exceptionBaseService.readOnly(request);
    }

    // Endpoints para combos
    @GET
    @Path("/distinct/requestingProjects")
    public List<String> getDistinctRequestingProjects() {
        return exceptionBaseService.getDistinctRequestingProjects();
    }

    @GET
    @Path("/distinct/approvalResponsibles")
    public List<String> getDistinctApprovalResponsibles() {
        return exceptionBaseService.getDistinctApprovalResponsibles();
    }

    @GET
    @Path("/distinct/registrationDates")
    public List<String> getDistinctRegistrationDates() {
        return exceptionBaseService.getDistinctRegistrationDates();
    }

    @GET
    @Path("/distinct/quarterYearSprints")
    public List<String> getDistinctQuarterYearSprints() {
        return exceptionBaseService.getDistinctQuarterYearSprints();
    }
}
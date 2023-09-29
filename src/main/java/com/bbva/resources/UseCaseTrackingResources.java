package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.usecasetracking.GetUseCaseTrackingJobsRequestDto;
import com.bbva.dto.usecasetracking.GetUseCaseTrackingJobsResponseDto;
import com.bbva.dto.usecasetracking.UseCaseDto;
import com.bbva.service.UseCaseTrackingService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/use_case_tracking")
@Produces(MediaType.APPLICATION_JSON)
public class UseCaseTrackingResources {
    private UseCaseTrackingService useCaseTrackingService = new UseCaseTrackingService();

    private Helper helper = new Helper();

    @GET
    @Path("/domain/{domainId}/use_case")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<UseCaseDto>> getUseCaseByDomainId(@PathParam("domainId") int domainId) {
        IDataResult<List<UseCaseDto>> result = useCaseTrackingService.getUseCaseByDomainId(domainId);
        return result;
    }

    @GET
    @Path("/use_case/{useCaseId}/date/{orderDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<GetUseCaseTrackingJobsResponseDto> getUseCaseTrackingJobs(
            @PathParam("useCaseId") int useCaseId,
            @PathParam("orderDate") String orderDate,
            @QueryParam("recordsAmount") String recordsAmount,
            @QueryParam("page") String page
    ) {
        GetUseCaseTrackingJobsRequestDto dto = new GetUseCaseTrackingJobsRequestDto();
        Integer recordsAmountFinal = helper.parseIntegerOrDefault(recordsAmount, 10);
        Integer pageFinal = helper.parseIntegerOrDefault(page, 1);

        dto.setRecords_amount(recordsAmountFinal);
        dto.setPage(pageFinal);
        dto.setUseCaseId(useCaseId);
        dto.setOrderDate(orderDate);

        IDataResult<GetUseCaseTrackingJobsResponseDto> result = useCaseTrackingService.getUseCaseTrackingJobs(dto);
        return result;
    }
}

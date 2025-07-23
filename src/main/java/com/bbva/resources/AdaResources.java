package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import com.bbva.service.AdaService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/ada")
@Produces(MediaType.APPLICATION_JSON)
public class AdaResources {
    private final AdaService adaService = new AdaService();
    private final Helper helper = new Helper();

    @GET
    @Path("/job_execution/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<AdaJobExecutionFilterResponseDTO> filter(
            @QueryParam("recordsAmount") String recordsAmount,
            @QueryParam("page") String page,
            @QueryParam("jobName") String jobName,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("frequency") String frequency,
            @QueryParam("isTransferred") String isTransferred,
            @QueryParam("jobType") String jobType,
            @QueryParam("serverExecution") String serverExecution,
            @QueryParam("domain") String domain
    ){
        AdaJobExecutionFilterRequestDTO dto = new AdaJobExecutionFilterRequestDTO();
        Integer recordsAmountFinal = helper.parseIntegerOrDefault(recordsAmount, 10);
        Integer pageFinal = helper.parseIntegerOrDefault(page, 1);

        dto.setRecords_amount(recordsAmountFinal);
        dto.setPage(pageFinal);
        dto.setJobName(jobName);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setFrequency(frequency);
        dto.setIsTransferred(isTransferred);
        dto.setJobType(jobType);
        dto.setServerExecution(serverExecution);
        dto.setDomain(domain);
        return adaService.filter(dto);
    }
}

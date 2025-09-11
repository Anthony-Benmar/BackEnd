package com.bbva.resources;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.visa_sources.request.ApproveVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.RegisterVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.UpdateStatusVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.VisaSourcePaginationDtoRequest;
import com.bbva.dto.visa_sources.response.VisaSourceApproveDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourceValidateExistDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourcesPaginationDtoResponse;
import com.bbva.service.VisaSourcesService;
import com.bbva.util.Helper;
import com.bbva.util.JSONUtils;

@Path("/visaSources")
@Produces(MediaType.APPLICATION_JSON)
public class VisaSourcesResources {
    private static final Logger LOGGER = Logger.getLogger(VisaSourcesResources.class.getName());

    private VisaSourcesService visaSourcesService = new VisaSourcesService();

    @GET
    @Path("/getVisaSources")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<VisaSourcesPaginationDtoResponse> getVisaSources(
        @QueryParam("limit") Integer limit,
        @QueryParam("offset") Integer offset,
        @QueryParam("id") Integer id,
        @QueryParam("quarter") String quarter,
        @QueryParam("registerDate") String registerDate,
        @QueryParam("domain") String domain,
        @QueryParam("userStory") String userStory
    ) {
        LOGGER.info("getVisaSources paginated request");
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();
        dto.setLimit(limit);
        dto.setOffset(offset);
        dto.setId(id);
        dto.setQuarter(quarter);
        dto.setDomain(domain);
        dto.setRegisterDate(registerDate);
        dto.setUserStory(userStory);
        return visaSourcesService.getVisaSources(dto);
    }

    @POST
    @Path("/registerVisaSource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Boolean> registerVisaSource(
        RegisterVisaSourceDtoRequest dto
    ) {
        LOGGER.info("registerVisaSource request");
        return visaSourcesService.registerVisaSource(dto);
    }

    @POST
    @Path("/updateVisaSource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Boolean> updateVisaSource(
        RegisterVisaSourceDtoRequest dto
    ) {
        LOGGER.info("updateVisaSource request");
        return visaSourcesService.updateVisaSource(dto);
    }

    @POST
    @Path("/approveVisaSource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<VisaSourceApproveDtoResponse> approveVisaSource(
        ApproveVisaSourceDtoRequest dto
    ) {
        LOGGER.info("approveVisaSource request");
        return visaSourcesService.approveVisaSource(dto);
    }

    @GET
    @Path("/validateSourceIds")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<VisaSourceValidateExistDtoResponse> validateSourceIds(
        @QueryParam("ids") String ids
    ) {
        LOGGER.info("validateSourceId request");
        return visaSourcesService.validateSourceIds(ids);
    }
}

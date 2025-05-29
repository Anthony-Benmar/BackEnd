package com.bbva.resources;

import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.service.SingleBaseService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/singleBase")
@Produces(MediaType.APPLICATION_JSON)
public class SingleBaseResources {
    private final SingleBaseService singleBaseService = new SingleBaseService();

    @GET
    @Path("/getSingleBase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SingleBasePaginatedResponseDTO getBaseUnicaWithSource(
            @QueryParam("limit") @DefaultValue("30") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset
    ) {
        return singleBaseService.getBaseUnicaWithSource(limit, offset);
    }
}
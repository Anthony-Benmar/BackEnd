package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.single_base.response.SingleBaseResponseDTO;
import com.bbva.service.SingleBaseService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/singleBase")
@Produces(MediaType.APPLICATION_JSON)
public class SingleBaseResources {
    private final SingleBaseService singleBaseService = new SingleBaseService();

    @GET
    @Path("/getSingleBase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<SingleBaseResponseDTO>> getBaseUnicaWithSource() {
        return singleBaseService.getBaseUnicaWithSource();
    }
}

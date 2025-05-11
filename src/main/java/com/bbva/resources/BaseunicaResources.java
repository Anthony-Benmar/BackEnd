package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.baseunica.response.BaseunicaResponseDTO;
import com.bbva.service.BaseunicaService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/baseunica")
@Produces(MediaType.APPLICATION_JSON)
public class BaseunicaResources {
    private final BaseunicaService baseunicaService = new BaseunicaService();

    @GET
    @Path("/getBaseUnicaWithSource/{tableName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<BaseunicaResponseDTO>> getBaseUnicaWithSource(@PathParam("tableName") String tableName) {
        return baseunicaService.getBaseUnicaWithSource(tableName);
    }
}

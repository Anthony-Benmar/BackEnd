package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;
import com.bbva.service.EfectivityService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/efectivity")
@Produces(MediaType.APPLICATION_JSON)
public class EfectivityResources {
    private EfectivityService efectivityService = new EfectivityService();

    @GET
    @Path("/getEfectivityWithSource/{tableName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<EfectivityEntityResponseDTO>> exceptionService(@PathParam("tableName") String tableName) {
        IDataResult<List<EfectivityEntityResponseDTO>> result = efectivityService.getEfectivityWithSource(tableName);
        return result;
    }
}

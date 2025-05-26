package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDTO;
import com.bbva.service.SourceWithParameterService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sourcesWithParameter")
@Produces(MediaType.APPLICATION_JSON)
public class SourceWithParameterResources {
    private SourceWithParameterService sourceWithParameterService = new SourceWithParameterService();
    @GET
    @Path("/getSourceWithParameter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<SourceWithParameterDTO>> getSourceWithParameter() {
        return sourceWithParameterService.getSourceWithParameter();
    }
}

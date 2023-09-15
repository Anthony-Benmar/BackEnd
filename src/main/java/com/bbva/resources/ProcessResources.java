package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.map_dependency.request.MapDependencyDTORequest;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.service.ProcessService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/process")
@Produces(MediaType.APPLICATION_JSON)
public class ProcessResources {
    private ProcessService processService = new ProcessService();

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<MapDependencyListByProjectResponse> insert(MapDependencyDTORequest dto) {
        return processService.insert(dto);
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<MapDependencyListByProjectResponse> update(MapDependencyDTORequest dto) {
        return processService.update(dto);
    }
}

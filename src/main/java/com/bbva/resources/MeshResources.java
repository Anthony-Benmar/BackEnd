package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.mesh.request.MeshDtoRequest;
import com.bbva.dto.mesh.response.MeshRelationalDtoResponse;
import com.bbva.service.MeshService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/mesh")
public class MeshResources {
    private MeshService meshService = new MeshService();

    @POST
    @Path("/jobs-dependencies")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<MeshRelationalDtoResponse>> jobsdependencies(MeshDtoRequest dto)
    {
        return meshService.jobsdependencies(dto);
    }
}

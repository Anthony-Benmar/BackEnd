package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.rol.request.RolDataDetailPermissionsRequest;
import com.bbva.dto.rol.request.RolDataRequest;
import com.bbva.dto.rol.response.RolDataDetailPermissionsResponse;
import com.bbva.dto.rol.response.RolDataResponse;
import com.bbva.service.RolService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/rol")
@Produces(MediaType.APPLICATION_JSON)
public class RolResource {
    
    private RolService rolService = new RolService();

    @POST
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<RolDataResponse>> list() {
        return rolService.list();
    }
    
    @POST
    @Path("/detail/permissions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<RolDataDetailPermissionsResponse> listDetailPermissions(RolDataDetailPermissionsRequest request) {
        return rolService.listDetailPermissions(request);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<RolDataResponse> registrar(RolDataRequest request){        
        return rolService.registrar(request);
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<RolDataResponse> update(RolDataRequest request){
        return rolService.actualizar(request);
    }

}

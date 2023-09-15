package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.User.Request.ValidateDtoRequest;
import com.bbva.dto.User.Response.ValidateDtoResponse;
import com.bbva.service.AuthenticationService;
import com.bbva.service.LogService;
import net.minidev.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/authentication")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResources {
    private static final Logger LOGGER = Logger.getLogger(CatalogResources.class.getName());
    private AuthenticationService authenticationService = new AuthenticationService();
    private LogService logService = new LogService();

    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ValidateDtoResponse> Validate(ValidateDtoRequest request){
        LOGGER.info("Validar usuario");
        IDataResult<ValidateDtoResponse>  result = authenticationService.Validate(request);
        return result;
    }

    @POST
    @Path("/validate/permissions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject ValidatePermissions(ValidateDtoRequest request){
        LOGGER.info("Validar permisos");
        return authenticationService.permissions(request);
    }
}

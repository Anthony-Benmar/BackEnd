package com.bbva.resources;

import com.bbva.fga.session.UserSessionHelper;
import com.bbva.jetty.MainApp;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    @GET
    public Response hello(@Context HttpServletRequest httpServletRequest) {
        var userSession = UserSessionHelper.getInstance().getUserSession(httpServletRequest, false);
        MainApp.ROOT_LOOGER.log(Level.INFO, "User session: {0}", userSession);
        return Response.status(200).entity("{\"message\":\"Hello world!\"}").build();
    }
}
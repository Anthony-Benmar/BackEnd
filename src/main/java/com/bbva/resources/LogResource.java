package com.bbva.resources;

import com.bbva.dao.LogDao;
import com.bbva.entities.secu.LogEntity;
import com.bbva.util.JSONUtils;
import com.google.api.client.http.HttpStatusCodes;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/log")
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {

    private static final Logger LOGGER = Logger.getLogger(LogResource.class.getName());
    private final LogDao logDao = new LogDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response allLog(@Context HttpServletRequest httpServletRequest) {
        try{
            LOGGER.info("Obtener todos los logs");
            List<LogEntity> logList =  logDao.list();
            var data = JSONUtils.convertFromObjectToJson(logList);
            return Response
                    .status(Response.Status.OK)
                    .entity(data)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch(Exception e){
            JSONObject resp = new JSONObject();
            resp.put("success",false);
            resp.put("msg","error");
            resp.put("data",e.getMessage());
            LOGGER.log(Level.SEVERE, MessageFormat.format("Error al obtener logs. {0}", e.getMessage()), e);
            return Response.status(HttpStatusCodes.STATUS_CODE_BAD_REQUEST).entity(resp).build();
        }

    }
}

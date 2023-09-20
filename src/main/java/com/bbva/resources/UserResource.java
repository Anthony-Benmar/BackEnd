package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UserDao;
import com.bbva.dto.User.Request.PaginationDtoRequest;
import com.bbva.dto.User.Request.UpdateRolesRequest;
import com.bbva.dto.User.Response.PaginationResponse;
import com.bbva.dto.User.Response.UpdateRolesResponse;
import com.bbva.entities.User;
import com.bbva.jetty.MainApp;
import com.bbva.service.UserService;
import com.bbva.util.JSONUtils;
import com.google.api.client.http.HttpStatusCodes;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());
    private final UserDao userDao = new UserDao();
    private final UserService userService = new UserService();

    @POST
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest request) {         
        return userService.pagination(request);
    }

    @POST
    @Path("/update/roles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<UpdateRolesResponse> updateRoles(UpdateRolesRequest request) {         
        userService.updateRoles(request);
        return new SuccessDataResult(UpdateRolesResponse.builder().roles(request.getRoles()).idUser(request.getIdUser()).build(), "Succesfull");
    }

    @GET
    public Response userCounts(@Context HttpServletRequest httpServletRequest) {

        try{
            LOGGER.info("Consultar cantidad de usuarios");
            var userCount =  userDao.list().size();
            var message = "Existen "+ userCount + " usuarios.";
            return Response
                    .status(Response.Status.OK)
                    .entity(message)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }catch(Exception e){
            JSONObject resp = new JSONObject();
            resp.put("success",false);
            resp.put("msg","error");
            resp.put("data",e.getMessage());
            LOGGER.log(Level.SEVERE, MessageFormat.format("Error al consultar cantidad de usuarios. {0}", e.getMessage()), e);
            return Response.status(HttpStatusCodes.STATUS_CODE_BAD_REQUEST).entity(resp).build();
        }
    }

    @GET
    @Path("/name/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showUserName(@Context HttpServletRequest request, @PathParam("userName") String userName) {
        return Response.status(200).entity("{\"message\":\"Hola: "+userName+"!\"}").build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allUserList(@Context HttpServletRequest httpServletRequest) {
        try{
            MainApp.ROOT_LOOGER.log(Level.INFO,"Obtener todos los usuarios con Response");
            List<User> userList =  userDao.list();
            var data = JSONUtils.convertFromObjectToJson(userList);
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
            MainApp.ROOT_LOOGER.log(Level.INFO,MessageFormat.format("Error al obtener todos los usuarios. {0}", e.getMessage()), e);
            return Response.status(HttpStatusCodes.STATUS_CODE_BAD_REQUEST).entity(resp).build();
        }

    }

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertUser(User usuario){

        try{
            LOGGER.info("Insertar Usuario");
            boolean success = userDao.insertUser(usuario);

            JSONObject resp = new JSONObject();
            resp.put("success",success);
            if(success){
                resp.put("msg","Datos insertados");
            }else{
                resp.put("msg","no se pudo insertar");
            }
            return Response.status(HttpStatusCodes.STATUS_CODE_OK)
                    .entity(resp)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            JSONObject resp = new JSONObject();
            resp.put("success",false);
            resp.put("msg","error");
            resp.put("data",e.getMessage());
            return Response.status(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                    .entity(resp)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFactory(@Context HttpServletRequest request,
                                  @PathParam("id") int id){
        try{
            LOGGER.info("Eliminar Usuario");
            boolean success = userDao.deleteUser(id);
            JSONObject resp = new JSONObject();
            resp.put("success",success);
            if(success){
                resp.put("msg","Se eliminó correctamente");
            }else{
                resp.put("msg","No se pudo eliminar");
            }
            return Response.status(HttpStatusCodes.STATUS_CODE_OK)
                    .entity(resp)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            JSONObject resp = new JSONObject();
            resp.put("success",false);
            resp.put("msg","error");
            resp.put("data",e.getMessage());
            return Response.status(HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
                    .entity(resp)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    @Path("/code/{employeeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult findByEmployeeId(@Context HttpServletRequest request, @PathParam("employeeId") String employeeId) {
        return userService.findByEmployeeId(employeeId);
    }
}

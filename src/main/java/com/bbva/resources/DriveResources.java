//package com.bbva.resources;
//
//import com.bbva.dto.drive.FolderDto;
//import com.bbva.service.DriveService;
//import org.apache.ibatis.session.SqlSessionFactory;
//
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.util.Map;
//import java.util.logging.Logger;
//
//@Path("/drive")
//public class DriveResources {
//    private static final Logger LOGGER = Logger.getLogger(DriveResources.class.getName());
//    private final DriveService driveService;
//
//    public DriveResources(SqlSessionFactory sqlSessionFactory) {
//        this.driveService = new DriveService(sqlSessionFactory);
//    }
//
//    @POST
//    @Path("/crear-carpeta")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response crearCarpeta(Map<String, String> input) {
//        String nombre = input.get("nombre");
//
//        if (nombre == null || nombre.trim().isEmpty()) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("El campo 'nombre' es obligatorio.").build();
//        }
//
//        try {
//            FolderDto carpeta = driveService.crearCarpeta(nombre);
//            return Response.ok(carpeta).build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Response.serverError().entity("Error: " + e.getMessage()).build();
//        }
//    }
//}
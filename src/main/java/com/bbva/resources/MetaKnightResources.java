package com.bbva.resources;

import com.bbva.core.results.ErrorDataResult;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.service.metaknight.IngestaService;

import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/metaknight")
@Produces(MediaType.APPLICATION_JSON)
public class MetaKnightResources {

    private IngestaService ingestaService = new IngestaService();

    @POST
    @Path("/ingesta/download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/octet-stream")
    public Response procesarIngestaDownload(IngestaRequestDto request) {
        try {
            byte[] zipBytes = ingestaService.procesarIngesta(request);

            return Response.ok(zipBytes)
                    .header("Content-Type", "application/zip")
                    .header("Content-Disposition", "attachment; filename=\"ingesta_output.zip\"")
                    .header("Content-Length", zipBytes.length)
                    .build();

        } catch (Exception e) {
            ErrorDataResult<Void> errorResult = new ErrorDataResult<>(
                    null, "500", "Error procesando la ingesta: " + e.getMessage()
            );

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(errorResult)
                    .build();
        }
    }
}

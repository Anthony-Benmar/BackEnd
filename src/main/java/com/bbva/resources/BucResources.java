package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.buc.request.ReadOnlyDtoRequest;
import com.bbva.dto.buc.response.ReadOnlyDtoResponse;
import com.bbva.dto.buc.request.PaginationDtoRequest;
import com.bbva.dto.buc.response.PaginationResponse;
import com.bbva.service.BucService;
import com.bbva.service.LogService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/buc")
@Produces(MediaType.APPLICATION_JSON)
public class BucResources {
    private static final Logger LOGGER = Logger.getLogger(BucResources.class.getName());
    private BucService bucService = new BucService();
    private LogService logService = new LogService();
    private Helper helper = new Helper();

    @GET
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(
            @QueryParam("recordsAmount") String recordsAmount,
            @QueryParam("page") String page,
            @QueryParam("folioCode") String folio_code,
            @QueryParam("codigoCampo") String codigo_campo,
            @QueryParam("nombreProyecto") String nombre_proyecto,
            @QueryParam("fuenteId") String id_fuente,
            @QueryParam("prioridad") String prioridad,
            @QueryParam("estadoResolucion") String estado_resolucion,
            @QueryParam("descripcionFuncional") String descripcion_funcional)
    {
        Integer recordsAmountFinal = helper.parseIntegerOrDefault(recordsAmount, 10);
        Integer pageFinal = helper.parseIntegerOrDefault(page, 1);

        PaginationDtoRequest dto = new PaginationDtoRequest();
        dto.setRecords_amount(recordsAmountFinal);
        dto.setPage(pageFinal);
        dto.setFolio_code(folio_code);
        dto.setCodigo_campo(codigo_campo);
        dto.setId_fuente(helper.parseInteger(id_fuente));
        dto.setPrioridad(helper.parseInteger(prioridad));
        dto.setEstado_resolucion(helper.parseInteger(estado_resolucion));
        dto.setDescripcion_funcional(descripcion_funcional);

        IDataResult<PaginationResponse>  result = bucService.pagination(dto);
        return result;
    }

    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ReadOnlyDtoResponse> readOnly(ReadOnlyDtoRequest request){
        LOGGER.info("buc readonly");
        IDataResult<ReadOnlyDtoResponse>  result = bucService.readOnly(request);
        return result;
    }

}

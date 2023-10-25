package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source.request.PaginationDtoRequest;
import com.bbva.dto.source.request.ReadOnlyDtoRequest;
import com.bbva.dto.source.response.PaginationResponse;
import com.bbva.dto.source.response.ReadOnlyDtoResponse;
import com.bbva.service.LogService;
import com.bbva.service.SourceService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/source")
@Produces(MediaType.APPLICATION_JSON)
public class SourceResources {
    private static final Logger LOGGER = Logger.getLogger(SourceResources.class.getName());
    private SourceService sourceService = new SourceService();
    private LogService logService = new LogService();
    private Helper helper = new Helper();

    @GET
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(
            @QueryParam("recordsAmount") String recordsAmount,
            @QueryParam("page") String page,
            @QueryParam("fuenteId") String id_fuente,
            @QueryParam("nombreFuente") String nombre_fuente,
            @QueryParam("uuaaAplicativo") String uuaa_aplicativo,
            @QueryParam("estado") String estado,
            @QueryParam("origen") String origen,
            @QueryParam("estadoDeuda") String estado_deuda,
            @QueryParam("uuaaMaster") String uuaa_master,
            @QueryParam("descripcionTds") String descripcion_tds,
            @QueryParam("tablaMaster") String tabla_master,
            @QueryParam("propietarioGlobal") String propietario_global)
    {
        LOGGER.info("board PaginationDtoResponse");
        Integer recordsAmountFinal = helper.parseIntegerOrDefault(recordsAmount, 10);
        Integer pageFinal = helper.parseIntegerOrDefault(page, 1);

        PaginationDtoRequest dto = new PaginationDtoRequest();
        dto.setRecords_amount(recordsAmountFinal);
        dto.setPage(pageFinal);
        dto.setId_fuente(helper.parseInteger(id_fuente));
        dto.setNombre_fuente(nombre_fuente);
        dto.setUuaa_aplicativo(uuaa_aplicativo);
        dto.setEstado(helper.parseInteger(estado));
        dto.setOrigen(helper.parseInteger(origen));
        dto.setEstado_deuda(helper.parseInteger(estado_deuda));
        dto.setUuaa_master(uuaa_master);
        dto.setDescripcion_tds(descripcion_tds);
        dto.setTabla_master(tabla_master);
        dto.setPropietario_global(helper.parseInteger(propietario_global));

        IDataResult<PaginationResponse>  result = sourceService.pagination(dto);
        return result;
    }

    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ReadOnlyDtoResponse> readOnly(ReadOnlyDtoRequest request){
        LOGGER.info("board readonly");
        IDataResult<ReadOnlyDtoResponse>  result = sourceService.readOnly(request);
        return result;
    }

}
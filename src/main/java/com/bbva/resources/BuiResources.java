package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.bui.request.PaginationDtoRequest;
import com.bbva.dto.bui.request.ReadOnlyDtoRequest;
import com.bbva.dto.bui.response.PaginationResponse;
import com.bbva.dto.bui.response.ReadOnlyDtoResponse;
import com.bbva.service.BuiService;
import com.bbva.service.LogService;
import com.bbva.util.Helper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/bui")
@Produces(MediaType.APPLICATION_JSON)
public class BuiResources {
    private static final Logger LOGGER = Logger.getLogger(BuiResources.class.getName());
    private BuiService buiService = new BuiService();
    private LogService logService = new LogService();
    private Helper helper = new Helper();

    @GET
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(
            @QueryParam("recordsAmount") String recordsAmount,
            @QueryParam("page") String page,
            @QueryParam("sdatool") String sdatool,
            @QueryParam("proposedTable") String proposed_table,
            @QueryParam("analystInCharge") String analyst_in_charge,
            @QueryParam("folioCode") String folio_code,
            @QueryParam("fuenteId") String id_fuente,
            @QueryParam("tipo") String tipo,
            @QueryParam("estado") String estado)
    {
        LOGGER.info("bui PaginationDtoResponse");

        Integer recordsAmountFinal = helper.parseIntegerOrDefault(recordsAmount, 10);
        Integer pageFinal = helper.parseIntegerOrDefault(page, 1);
        Integer idFuenteFinal = helper.parseInteger(id_fuente);
        Integer tipoFinal = helper.parseInteger(tipo);
        Integer estadoFinal = helper.parseInteger(estado);

        PaginationDtoRequest dto = new PaginationDtoRequest();
        dto.setRecords_amount(recordsAmountFinal);
        dto.setPage(pageFinal);
        dto.setSdatool(sdatool);
        dto.setProposed_table(proposed_table);
        dto.setAnalyst_in_charge(analyst_in_charge);
        dto.setFolio_code(folio_code);
        dto.setId_fuente(idFuenteFinal);
        dto.setTipo(tipoFinal);
        dto.setEstado(estadoFinal);

        IDataResult<PaginationResponse> result = buiService.pagination(dto);
        return result;
    }

    @POST
    @Path("/readonly")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ReadOnlyDtoResponse> readOnly(ReadOnlyDtoRequest request){
        LOGGER.info("bui readonly");
        IDataResult<ReadOnlyDtoResponse>  result = buiService.readOnly(request);
        return result;
    }
}
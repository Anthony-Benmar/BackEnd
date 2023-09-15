package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.spp.Period;
import com.bbva.service.CatalogService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResources {
    private CatalogService catalogService = new CatalogService();

    @POST
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ListByCatalogIdDtoResponse> catalogsByCatalogId(ListByCatalogIdDtoRequest dto) {
        return catalogService.catalogosByCatalogoId(dto);
    }

    @GET
    @Path("periods")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Period> listPeriods() throws IOException, InterruptedException {
        return catalogService.listPeriods();
    }

    @GET
    @Path("listperiods")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PeriodEntity> listAllPeriods() {
        return catalogService.listAllPeriods();
    }
}

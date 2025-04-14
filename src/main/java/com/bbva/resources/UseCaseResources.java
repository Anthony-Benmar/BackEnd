package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.CatalogResponseDto;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.spp.Period;
import com.bbva.entities.usecase.UseCaseEntity;
import com.bbva.service.UseCaseService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/use-cases")
@Produces(MediaType.APPLICATION_JSON)
public class UseCaseResources {
    private UseCaseService useCaseService = new UseCaseService();

    @GET
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<UseCaseEntity> listUseCases() throws IOException, InterruptedException {
        return useCaseService.listUseCases();
    }
}

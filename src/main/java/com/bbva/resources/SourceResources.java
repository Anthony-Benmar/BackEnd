package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source.request.PaginationDtoRequest;
import com.bbva.dto.source.request.ReadOnlyDtoRequest;
import com.bbva.dto.source.response.PaginationResponse;
import com.bbva.dto.source.response.ReadOnlyDtoResponse;
import com.bbva.service.LogService;
import com.bbva.service.SourceService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/source")
@Produces(MediaType.APPLICATION_JSON)
public class SourceResources {
    private static final Logger LOGGER = Logger.getLogger(SourceResources.class.getName());
    private SourceService sourceService = new SourceService();
    private LogService logService = new LogService();

    @POST
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest request){
        LOGGER.info("board PaginationDtoResponse");
        IDataResult<PaginationResponse>  result = sourceService.pagination(request);
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
package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.bui.request.PaginationDtoRequest;
import com.bbva.dto.bui.request.ReadOnlyDtoRequest;
import com.bbva.dto.bui.response.PaginationResponse;
import com.bbva.dto.bui.response.ReadOnlyDtoResponse;
import com.bbva.service.BuiService;
import com.bbva.service.LogService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/bui")
@Produces(MediaType.APPLICATION_JSON)
public class BuiResources {
    private static final Logger LOGGER = Logger.getLogger(BuiResources.class.getName());
    private BuiService buiService = new BuiService();
    private LogService logService = new LogService();

    @POST
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest request){
        LOGGER.info("bui PaginationDtoResponse");
        IDataResult<PaginationResponse>  result = buiService.pagination(request);
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
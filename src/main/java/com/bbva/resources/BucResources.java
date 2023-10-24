package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.buc.request.ReadOnlyDtoRequest;
import com.bbva.dto.buc.response.ReadOnlyDtoResponse;
import com.bbva.dto.buc.request.PaginationDtoRequest;
import com.bbva.dto.buc.response.PaginationResponse;
import com.bbva.service.BucService;
import com.bbva.service.LogService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/buc")
@Produces(MediaType.APPLICATION_JSON)
public class BucResources {
    private static final Logger LOGGER = Logger.getLogger(BucResources.class.getName());
    private BucService bucService = new BucService();
    private LogService logService = new LogService();

    @POST
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest request) {
        IDataResult<PaginationResponse>  result = bucService.pagination(request);
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

package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.external.request.GobiernoDtoRequest;
import com.bbva.dto.external.response.GobiernoDtoResponse;
import com.bbva.service.ExternalService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/external")
@Produces(MediaType.APPLICATION_JSON)
public class ExternoResources {
    private static final Logger LOGGER = Logger.getLogger(ExternoResources.class.getName());
    private ExternalService externalService = new ExternalService();
    @POST
    @Path("/gobierno")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<GobiernoDtoResponse> readOnly(GobiernoDtoRequest request){
        LOGGER.info("board readonly");
        IDataResult<GobiernoDtoResponse>  result = externalService.gobierno(request);
        return result;
    }
}

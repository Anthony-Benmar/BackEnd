package com.bbva.resources;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.service.ReliabilityService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;
@Path("/reliability")
@Produces(MediaType.APPLICATION_JSON)
public class ReliabilityResource {
    private static final Logger LOGGER = Logger.getLogger(ReliabilityResource.class.getName());
    private final ReliabilityService reliabilityService = new ReliabilityService();

    @POST
    @Path("/info/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InventoryInputsFilterDtoResponse> inventoryInputsFilter(InventoryInputsFilterDtoRequest dto) {
        return reliabilityService.inventoryInputsFilter(dto);
    }

}

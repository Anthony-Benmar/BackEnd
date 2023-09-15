package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.template.request.PaginationDtoRequest;
import com.bbva.dto.template.response.TemplatePaginationDtoResponse;
import com.bbva.service.TemplateService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/template")
@Produces(MediaType.APPLICATION_JSON)
public class TemplateResources {
    private static final Logger LOGGER = Logger.getLogger(TemplateResources.class.getName());
    private TemplateService templateService = new TemplateService();

    @POST
    @Path("pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<TemplatePaginationDtoResponse> pagination(PaginationDtoRequest dto) {
        return templateService.pagination(dto);
    }

}
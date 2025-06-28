package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.service.IssueTicketService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/issue_ticket")
@Produces(MediaType.APPLICATION_JSON)
public class IssueTicketResources {
    private IssueTicketService issueTicketService = new IssueTicketService();

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Void> insert(WorkOrderDtoRequest dto)
            throws Exception
    {
        return issueTicketService.insert(dto);
    }

    @POST
    @Path("/insert2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Map<String, Object>> insertFeatures(List<WorkOrderDtoRequest2> dtoList)
            throws Exception {
        return issueTicketService.insert2(dtoList);
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult update(WorkOrderDtoRequest dto)
            throws Exception
    {
        return issueTicketService.update(dto);
    }

    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult generate(WorkOrderDtoRequest dto)
            throws Exception
    {
        return issueTicketService.generate(dto);
    }

    @POST
    @Path("/listSourcesGenerated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<sourceTicketDtoResponse> listSourcesGenerated(sourceTicketDtoRequest dto)
    {
        return issueTicketService.listSourcesGenerated(dto);
    }

    @POST
    @Path("/listIssuesGenerated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<issueTicketDtoResponse> listIssuesGenerated(sourceTicketDtoRequest dto)
    {
        return issueTicketService.listIssuesGenerated(dto);
    }
}

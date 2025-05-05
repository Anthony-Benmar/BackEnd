package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.board.request.BoardPaginationDtoRequest;
import com.bbva.dto.board.request.ListDtoRequest;
import com.bbva.dto.board.response.ListDtoResponse;
import com.bbva.dto.board.response.BoardPaginationDtoResponse;
import com.bbva.entities.board.JiraTeamBacklogEntity;
import com.bbva.service.BoardService;
import com.bbva.service.LogService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/board")
@Produces(MediaType.APPLICATION_JSON)
public class BoardResources {
    private static final Logger LOGGER = Logger.getLogger(BoardResources.class.getName());
    private BoardService boardManager = new BoardService();
    private LogService logService = new LogService();

    @POST
    @Path("/pagination")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<BoardPaginationDtoResponse> pagination(BoardPaginationDtoRequest request){
        LOGGER.info("board PaginationDtoResponse");
        IDataResult<BoardPaginationDtoResponse>  result = boardManager.pagination(request);
        return result;
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ListDtoResponse>> list(ListDtoRequest dto){
        LOGGER.info("board readonly");
        IDataResult<List<ListDtoResponse>>  result = boardManager.list(dto);
        return result;
    }

    @GET
    @Path("/{projectId}/jirateambacklog")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<JiraTeamBacklogEntity>> getProjectParticipants(@PathParam("projectId") Long projectId)
    {
        return boardManager.getJiraTeamBackLog(projectId);
    }
}

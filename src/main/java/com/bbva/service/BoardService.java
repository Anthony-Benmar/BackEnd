package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BoardDao;
import com.bbva.dto.board.request.BoardPaginationDtoRequest;
import com.bbva.dto.board.request.ListDtoRequest;
import com.bbva.dto.board.response.ListDtoResponse;
import com.bbva.dto.board.response.BoardPaginationDtoResponse;
import com.bbva.entities.board.JiraTeamBacklogEntity;

import java.util.List;

public class BoardService {

    private final BoardDao _boardRepository = new BoardDao();

    public IDataResult<BoardPaginationDtoResponse> pagination(BoardPaginationDtoRequest dto) {
        var result = _boardRepository.pagination(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<List<ListDtoResponse>> list(ListDtoRequest dto) {
        var modelo = _boardRepository.list(dto);
        return new SuccessDataResult(modelo);
    }

    public IDataResult<List<JiraTeamBacklogEntity>> getJiraTeamBackLog(Long projectId) {
        try {
            List<JiraTeamBacklogEntity> result = _boardRepository.listJiraTeamBackLog(projectId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
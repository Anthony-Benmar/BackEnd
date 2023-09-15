package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BoardDao;
import com.bbva.dto.board.request.BoardPaginationDtoRequest;
import com.bbva.dto.board.request.ListDtoRequest;
import com.bbva.dto.board.response.ListDtoResponse;
import com.bbva.dto.board.response.BoardPaginationDtoResponse;

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

}
package com.bbva.service;

import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.dto.single_base.response.SingleBaseResponseDTO;

import java.util.List;

public class SingleBaseService {
    private final SingleBaseDao singleBaseDao = new SingleBaseDao();

    public SingleBasePaginatedResponseDTO getBaseUnicaWithSource(int limit, int offset) {
        List<SingleBaseResponseDTO> data = singleBaseDao.getBaseUnicaWithSource(limit, offset);
        int totalCount = singleBaseDao.getBaseUnicaTotalCount();
        SingleBasePaginatedResponseDTO response = new SingleBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);
        return response;
    }
}
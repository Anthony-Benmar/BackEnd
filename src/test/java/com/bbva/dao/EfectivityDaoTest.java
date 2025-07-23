package com.bbva.dao;


import com.bbva.database.mappers.EfectivityBaseMapper;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EfectivityDaoTest {
    private EfectivityBaseDao efectivityBaseDao;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private EfectivityBaseMapper efectivityBaseMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        efectivityBaseDao = new EfectivityBaseDao(sqlSessionFactory);
        when(sqlSessionFactory.openSession()).thenReturn(sqlSession);
        when(sqlSession.getMapper(EfectivityBaseMapper.class)).thenReturn(efectivityBaseMapper);
    }

    @Test
    void testGetBaseEfectivityWithSource() {
        EfectivityBasePaginationDtoRequest dto = new EfectivityBasePaginationDtoRequest();
        dto.setLimit(10);
        dto.setOffset(0);

        List<EfectivityBaseDataDtoResponse> mockResponse = List.of(new EfectivityBaseDataDtoResponse());
        when(efectivityBaseMapper.getBaseEfectivityDataWithFilters(
                dto.getLimit(),
                dto.getOffset(),
                dto.getSdatoolProject(),
                dto.getSprintDate(),
                dto.getRegisterDate(),
                dto.getEfficiency()
        )).thenReturn(mockResponse);

        List<EfectivityBaseDataDtoResponse> result = efectivityBaseDao.getBaseEfectivityWithSource(dto);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSession).close();
    }

    @Test
    void testGetBaseEfectivityTotalCount() {
        EfectivityBasePaginationDtoRequest dto = new EfectivityBasePaginationDtoRequest();
        when(efectivityBaseMapper.getBaseEfectivityTotalCountWithFilters(
                dto.getSdatoolProject(),
                dto.getSprintDate(),
                dto.getRegisterDate(),
                dto.getEfficiency()
        )).thenReturn(5);

        int totalCount = efectivityBaseDao.getBaseEfectivityTotalCount(dto);

        assertEquals(5, totalCount);
        verify(sqlSession).close();
    }

    @Test
    void testGetBaseEfectivityById() {
        String singleId = "123";
        EfectivityBaseDataDtoResponse mockResponse = new EfectivityBaseDataDtoResponse();
        mockResponse.setId(singleId);

        when(efectivityBaseMapper.getBaseEfectivityById(singleId)).thenReturn(mockResponse);

        EfectivityBaseDataDtoResponse result = efectivityBaseDao.getBaseEfectivityById(singleId);

        assertNotNull(result);
        assertEquals(singleId, result.getId());
        verify(sqlSession).close();
    }
}

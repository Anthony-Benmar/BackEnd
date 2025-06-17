package com.bbva.dao;


import com.bbva.database.mappers.SingleBaseMapper;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
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

class SingleBaseDaoTest {
    private SingleBaseDao singleBaseDao;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private SingleBaseMapper singleBaseMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        singleBaseDao = new SingleBaseDao(sqlSessionFactory);
        when(sqlSessionFactory.openSession()).thenReturn(sqlSession);
        when(sqlSession.getMapper(SingleBaseMapper.class)).thenReturn(singleBaseMapper);
    }
    @Test
    void testGetSingleBaseData() {
        SingleBasePaginationDtoRequest dto = new SingleBasePaginationDtoRequest();
        dto.setLimit(10);
        dto.setOffset(0);

        List<SingleBaseDataDtoResponse> mockResponse = List.of(new SingleBaseDataDtoResponse());
        when(singleBaseMapper.getBaseUnicaDataWithFilters(
                dto.getLimit(),
                dto.getOffset(),
                dto.getId(),
                dto.getProjectName(),
                dto.getTipoFolio(),
                dto.getFolio(),
                dto.getRegisteredFolioDate(),
                dto.getOldSourceId()
        )).thenReturn(mockResponse);

        List<SingleBaseDataDtoResponse> result = singleBaseDao.getBaseUnicaWithSource(dto);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSession).close();
    }
    @Test
    void testGetSingleBaseTotalCount() {
        SingleBasePaginationDtoRequest dto = new SingleBasePaginationDtoRequest();
        when(singleBaseMapper.getBaseUnicaTotalCountWithFilters(
                dto.getId(),
                dto.getProjectName(),
                dto.getTipoFolio(),
                dto.getFolio(),
                dto.getRegisteredFolioDate(),
                dto.getOldSourceId()
        )).thenReturn(5);

        int totalCount = singleBaseDao.getBaseUnicaTotalCount(dto);
        assertEquals(5, totalCount);
        verify(sqlSession).close();
    }

    @Test
    void testGetSingleBaseById() {
        String singleBaseId = "12345";
        SingleBaseDataDtoResponse mockResponse = new SingleBaseDataDtoResponse();
        when(singleBaseMapper.getSingleBaseById(singleBaseId)).thenReturn(mockResponse);

        SingleBaseDataDtoResponse result = singleBaseDao.getSingleBaseById(singleBaseId);
        assertNotNull(result);
        verify(sqlSession).close();
    }
}

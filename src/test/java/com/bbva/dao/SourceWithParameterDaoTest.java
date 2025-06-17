package com.bbva.dao;


import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
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

class SourceWithParameterDaoTest {
    private SourceWithParameterDao sourceWithParameterDao;
    @Mock
    private SqlSessionFactory sqlSessionFactory;
    @Mock
    private SqlSession sqlSession;
    @Mock
    private SourceWithParameterMapper sourceWithParameterMapper;
    @BeforeEach
    void setUp() {
     MockitoAnnotations.openMocks(this);
     sourceWithParameterDao = new SourceWithParameterDao(sqlSessionFactory);
     when(sqlSessionFactory.openSession()).thenReturn(sqlSession);
     when(sqlSession.getMapper(SourceWithParameterMapper.class)).thenReturn(sourceWithParameterMapper);
    }
    @Test
    void testGetSourceWithParameter() {
     SourceWithParameterPaginationDtoRequest dto = new SourceWithParameterPaginationDtoRequest();
     dto.setLimit(10);
     dto.setOffset(0);

     List<SourceWithParameterDataDtoResponse> mockResponse = List.of(new SourceWithParameterDataDtoResponse());
     when(sourceWithParameterMapper.getSourcesWithParameterWithFilters(
             dto.getLimit(),
             dto.getOffset(),
             dto.getId(),
             dto.getTdsSource(),
             dto.getUuaaMaster(),
             dto.getModelOwner(),
             dto.getStatus(),
             dto.getOriginType(),
             dto.getTdsOpinionDebt(),
             dto.getEffectivenessDebt()
     )).thenReturn(mockResponse);
     List<SourceWithParameterDataDtoResponse> result = sourceWithParameterDao.getSourceWithParameter(dto);
     assertNotNull(result);
     assertEquals(1, result.size());
     verify(sqlSession).close();
    }
    @Test
    void testGetSourceWithParameterCount() {
        SourceWithParameterPaginationDtoRequest dto = new SourceWithParameterPaginationDtoRequest();
        dto.setId("testId");
        dto.setTdsSource("testSource");

        when(sourceWithParameterMapper.getSourcesWithParameterTotalCountWithFilters(
                dto.getId(),
                dto.getTdsSource(),
                dto.getUuaaMaster(),
                dto.getModelOwner(),
                dto.getStatus(),
                dto.getOriginType(),
                dto.getTdsOpinionDebt(),
                dto.getEffectivenessDebt()
        )).thenReturn(5);

        int count = sourceWithParameterDao.getSourceWithParameterTotalCount(dto);
        assertEquals(5, count);
        verify(sqlSession).close();
    }
    @Test
    void testGetSourceWithParameterById() {
        String id = "testId";
        SourceWithParameterDataDtoResponse mockResponse = new SourceWithParameterDataDtoResponse();
        when(sourceWithParameterMapper.getSourceWithParameterById(id)).thenReturn(mockResponse);

        SourceWithParameterDataDtoResponse result = sourceWithParameterDao.getSourceWithParameterById(id);
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(sqlSession).close();
    }
}

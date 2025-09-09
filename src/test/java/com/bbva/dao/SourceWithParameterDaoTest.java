package com.bbva.dao;

import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SourceWithParameterDaoTest {
    @InjectMocks
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
        when(sourceWithParameterMapper.getSourcesWithParameterWithFilters(dto)).thenReturn(mockResponse);
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

        when(sourceWithParameterMapper.getSourcesWithParameterTotalCountWithFilters(dto)).thenReturn(5);

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

    @Test
    void testUpdate() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        boolean result = sourceWithParameterDao.update(dto);
        assertTrue(result);
        verify(sourceWithParameterMapper).updateSource(dto);
        verify(sqlSession).commit();
    }

    @Test
    void testGetCommentsBySourceIdAndType() {
        List<String> comments = Arrays.asList("comment1", "comment2");
        when(sourceWithParameterMapper.getCommentsBySourceIdAndType("1", "typeA")).thenReturn(comments);

        List<String> result = sourceWithParameterDao.getCommentsBySourceIdAndType("1", "typeA");
        assertEquals(2, result.size());
        assertEquals("comment1", result.get(0));
    }

    @Test
    void testSaveCommentBySourceIdAndType() {
        sourceWithParameterDao.saveCommentBySourceIdAndType("1", "typeA", "new comment");
        verify(sourceWithParameterMapper).saveCommentBySourceIdAndType("1", "typeA", "new comment");
        verify(sqlSession).commit();
    }

    @Test
    void testInsertModifyHistory() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        sourceWithParameterDao.insertModifyHistory(dto);
        verify(sourceWithParameterMapper).insertModifyHistory(dto);
        verify(sqlSession).commit();
    }

    @Test
    void testInsertWithoutReplacement() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("123");

        boolean result = sourceWithParameterDao.insert(dto);
        assertTrue(result);
        verify(sourceWithParameterMapper).insertSource(dto);
        verify(sqlSession).commit();
    }

    @Test
    void testInsertWithReplacement() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("123");
        dto.setReplacementId("456");

        when(sourceWithParameterMapper.getReplacementIds("456")).thenReturn("");
        sourceWithParameterDao.insert(dto);

        verify(sourceWithParameterMapper).updateReplacementId("123", "456");
        verify(sqlSession).commit();
    }

    @Test
    void testGetMaxSourceId() {
        when(sourceWithParameterMapper.getMaxSourceId()).thenReturn("999");
        String result = sourceWithParameterDao.getMaxSourceId();
        assertEquals("999", result);
    }

    @Test
    void testExistsReplacementIdTrue() {
        when(sourceWithParameterMapper.countById("123")).thenReturn(1);
        assertTrue(sourceWithParameterDao.existsReplacementId("123"));
    }

    @Test
    void testExistsReplacementIdFalse() {
        when(sourceWithParameterMapper.countById("123")).thenReturn(0);
        assertFalse(sourceWithParameterDao.existsReplacementId("123"));
    }

    @Test
    void testGetStatusById() {
        when(sourceWithParameterMapper.getStatusById("123")).thenReturn("ACTIVE");
        String result = sourceWithParameterDao.getStatusById("123");
        assertEquals("ACTIVE", result);
    }
    @Test
    void testUpdateThrowsException() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        doThrow(new RuntimeException("DB error")).when(sourceWithParameterMapper).updateSource(dto);

        boolean result = sourceWithParameterDao.update(dto);

        assertFalse(result);
        verify(sqlSession).close();
    }

    @Test
    void testSaveCommentBySourceIdAndTypeThrowsException() {
        doThrow(new RuntimeException("DB comment error")).when(sourceWithParameterMapper)
                .saveCommentBySourceIdAndType(anyString(), anyString(), anyString());

        assertThrows(RuntimeException.class, () ->
                sourceWithParameterDao.saveCommentBySourceIdAndType("1", "typeA", "comment")
        );
        verify(sqlSession).close();
    }

    @Test
    void testInsertModifyHistoryThrowsException() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        doThrow(new RuntimeException("DB history error")).when(sourceWithParameterMapper)
                .insertModifyHistory(dto);

        assertThrows(RuntimeException.class, () ->
                sourceWithParameterDao.insertModifyHistory(dto)
        );
        verify(sqlSession).close();
    }

}

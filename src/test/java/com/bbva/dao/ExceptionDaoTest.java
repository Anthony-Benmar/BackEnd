package com.bbva.dao;


import com.bbva.database.mappers.ExceptionBaseMapper;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class ExceptionDaoTest {
    private ExceptionBaseDao exceptionBaseDao;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private ExceptionBaseMapper exceptionBaseMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionBaseDao = new ExceptionBaseDao(sqlSessionFactory);
        when(sqlSessionFactory.openSession()).thenReturn(sqlSession);
        when(sqlSession.getMapper(ExceptionBaseMapper.class)).thenReturn(exceptionBaseMapper);
    }
    @Test
    void testGetExceptionsWithSource() {
        // Arrange
        ExceptionBasePaginationDtoRequest dto = new ExceptionBasePaginationDtoRequest();
        dto.setLimit(10);
        dto.setOffset(0);

        // Mocking the mapper response
        List<ExceptionBaseDataDtoResponse> mockResponse = List.of(new ExceptionBaseDataDtoResponse());
        when(exceptionBaseMapper.getExceptionsDataWithFilters(
                dto.getLimit(),
                dto.getOffset(),
                dto.getRequestingProject(),
                dto.getApprovalResponsible(),
                dto.getRegistrationDate(),
                dto.getQuarterYearSprint()
        )).thenReturn(mockResponse);

        // Act
        List<ExceptionBaseDataDtoResponse> result = exceptionBaseDao.getExceptionsWithSource(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    void testGetExceptionsTotalCount() {
        // Arrange
        ExceptionBasePaginationDtoRequest dto = new ExceptionBasePaginationDtoRequest();
        dto.setRequestingProject("TestProject");
        dto.setApprovalResponsible("TestResponsible");

        // Mocking the mapper response
        int mockCount = 5;
        when(exceptionBaseMapper.getExceptionsTotalCountWithFilters(
                dto.getRequestingProject(),
                dto.getApprovalResponsible(),
                dto.getRegistrationDate(),
                dto.getQuarterYearSprint()
        )).thenReturn(mockCount);

        // Act
        int totalCount = exceptionBaseDao.getExceptionsTotalCount(dto);

        // Assert
        assertEquals(mockCount, totalCount);
    }
    @Test
    void testGetExceptionById() {
    // Arrange
        String exceptionId = "12345";
        ExceptionBaseDataDtoResponse mockResponse = new ExceptionBaseDataDtoResponse();
        when(exceptionBaseMapper.getExceptionById(exceptionId)).thenReturn(mockResponse);

        // Act
        ExceptionBaseDataDtoResponse result = exceptionBaseDao.getExceptionById(exceptionId);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
    }
}

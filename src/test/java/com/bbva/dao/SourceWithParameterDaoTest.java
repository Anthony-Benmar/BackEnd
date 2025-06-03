package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

 class SourceWithParameterDaoTest {
    @Test
    void testGetSourceWithParameter() {
        SqlSessionFactory mockSqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession mockSqlSession = mock(SqlSession.class);
        SourceWithParameterMapper mockSourceWithParameterMapper = mock(SourceWithParameterMapper.class);

        // Mock the behavior of the dependencies
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(SourceWithParameterMapper.class)).thenReturn(mockSourceWithParameterMapper);

        List<SourceWithParameterDataDtoResponse> expectedList = List.of(new SourceWithParameterDataDtoResponse());
        when(mockSourceWithParameterMapper.getSourcesWithParameterWithFilters(
                anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(expectedList);

        try (MockedStatic<MyBatisConnectionFactory> mockedFactory = mockStatic(MyBatisConnectionFactory.class)) {
            mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);
            SourceWithParameterDao sourceWithParameterDao = new SourceWithParameterDao();
            List<SourceWithParameterDataDtoResponse> actualList = sourceWithParameterDao.getSourceWithParameter(new SourceWithParameterPaginationDtoRequest());
            // Assertions
            assertNotNull(actualList);
            assertEquals(expectedList, actualList);
        }
    }
}

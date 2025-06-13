package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.dto.exception.response.ExceptionEntityResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ExceptionDaoTest {
    @Test
    void testGetExceptionsWithSource() {
        // Mock dependencies
        SqlSessionFactory mockSqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession mockSqlSession = mock(SqlSession.class);
        ExceptionsMapper mockExceptionsMapper = mock(ExceptionsMapper.class);

        // Mock the behavior of the dependencies
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(ExceptionsMapper.class)).thenReturn(mockExceptionsMapper);

        // Prepare expected data
        List<ExceptionEntityResponseDTO> expectedList = List.of(new ExceptionEntityResponseDTO());
        when(mockExceptionsMapper.getExceptionsWithSource()).thenReturn(expectedList);

        // Use MockedStatic to mock MyBatisConnectionFactory
        try (MockedStatic<MyBatisConnectionFactory> mockedFactory = mockStatic(MyBatisConnectionFactory.class)) {
            mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);

            // Create an instance of ExceptionDao and call the method
            ExceptionDao exceptionDao = new ExceptionDao();
            List<ExceptionEntityResponseDTO> actualList = exceptionDao.getExceptionsWithSource();

            // Assertions
            assertNotNull(actualList);
            assertEquals(expectedList, actualList);
        }
    }
}

package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.EfectivityMapper;
import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class EfectivityDaoTest {
    @Test
     void testGetEfectivityWithSource() {
        // Mock dependencies
        SqlSessionFactory mockSqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession mockSqlSession = mock(SqlSession.class);
        EfectivityMapper mockMapper = mock(EfectivityMapper.class);

        // Mock static method
        try (MockedStatic<MyBatisConnectionFactory> mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class)) {
            mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);
            when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
            when(mockSqlSession.getMapper(EfectivityMapper.class)).thenReturn(mockMapper);

            // Define behavior
            List<EfectivityEntityResponseDTO> mockResponse = Arrays.asList(new EfectivityEntityResponseDTO());
            when(mockMapper.getEfectivityWithSource("test_table")).thenReturn(mockResponse);

            // Call the method under test
            EfectivityDao efectivityDao = new EfectivityDao();
            List<EfectivityEntityResponseDTO> result = efectivityDao.getEfectivityWithSource("test_table");

            // Verify results
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(mockMapper).getEfectivityWithSource("test_table");
        }
    }
}

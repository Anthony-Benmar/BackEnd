package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BaseunicaMapper;
import com.bbva.dto.baseunica.response.BaseunicaResponseDTO;
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

class BaseunicaDaoTest {

    @Test
    void testGetBaseUnicaWithSource() {
        // Mock dependencies
        SqlSessionFactory mockSqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession mockSqlSession = mock(SqlSession.class);
        BaseunicaMapper mockMapper = mock(BaseunicaMapper.class);

        // Mock static method
        try (MockedStatic<MyBatisConnectionFactory> mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class)) {
            mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);
            when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
            when(mockSqlSession.getMapper(BaseunicaMapper.class)).thenReturn(mockMapper);

            // Define behavior
            List<BaseunicaResponseDTO> mockResponse = Arrays.asList(new BaseunicaResponseDTO());
            when(mockMapper.getBaseUnicaData("test_table")).thenReturn(mockResponse);

            // Call the method under test
            BaseunicaDao baseunicaDao = new BaseunicaDao();
            List<BaseunicaResponseDTO> result = baseunicaDao.getBaseUnicaWithSource("test_table");

            // Verify results
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(mockMapper).getBaseUnicaData("test_table");
        }
    }
}

package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SingleBaseMapper;
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

class SingleBaseDaoTest {

    @Test
    void testGetBaseUnicaWithSource() {
        // Mock dependencies
        SqlSessionFactory mockSqlSessionFactory = mock(SqlSessionFactory.class);
        SqlSession mockSqlSession = mock(SqlSession.class);
        SingleBaseMapper mockMapper = mock(SingleBaseMapper.class);

        // Mock static method
        try (MockedStatic<MyBatisConnectionFactory> mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class)) {
            mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);
            when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
            when(mockSqlSession.getMapper(SingleBaseMapper.class)).thenReturn(mockMapper);

            // Define behavior
            List<SingleBaseResponseDTO> mockResponse = Arrays.asList(new SingleBaseResponseDTO());
            when(mockMapper.getBaseUnicaData()).thenReturn(mockResponse);

            // Call the method under test
            SingleBaseDao singleBaseDao = new SingleBaseDao();
            List<SingleBaseResponseDTO> result = singleBaseDao.getBaseUnicaWithSource();

            // Verify results
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(mockMapper).getBaseUnicaData();
        }
    }
}

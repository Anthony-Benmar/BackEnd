package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDTO;
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

        List<SourceWithParameterDTO> expectedList = List.of(new SourceWithParameterDTO());
        when(mockSourceWithParameterMapper.getSourcesWithParameter()).thenReturn(expectedList);

        try (MockedStatic<MyBatisConnectionFactory> mockedFactory = mockStatic(MyBatisConnectionFactory.class)) {
            mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);
            SourceWithParameterDao sourceWithParameterDao = new SourceWithParameterDao();
            List<SourceWithParameterDTO> actualList = sourceWithParameterDao.getSourceWithParameter();
            // Assertions
            assertNotNull(actualList);
            assertEquals(expectedList, actualList);
        }
    }
}

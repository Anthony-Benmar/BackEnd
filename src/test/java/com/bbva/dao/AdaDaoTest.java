package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.AdaMapper;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterData;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdaDaoTest {

    private AdaDao adaDao;
    private SqlSessionFactory mockSqlSessionFactory;
    private SqlSession mockSqlSession;
    private AdaMapper mockAdaMapper;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSqlSessionFactory = mock(SqlSessionFactory.class);
        mockSqlSession = mock(SqlSession.class);
        mockAdaMapper = mock(AdaMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);

        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(AdaMapper.class)).thenReturn(mockAdaMapper);

        adaDao = new AdaDao();
    }

    @AfterEach
    public void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testGetFilteredAda() {
        AdaJobExecutionFilterRequestDTO request = new AdaJobExecutionFilterRequestDTO();
        request.setDomain("CS");
        request.setRecords_amount(3);
        request.setPage(1);

        AdaJobExecutionFilterData adaJobExecutionFilterData = new AdaJobExecutionFilterData();
        adaJobExecutionFilterData.setRecordsCount(3);

        List<AdaJobExecutionFilterData> mockList = List.of(
                adaJobExecutionFilterData,
                adaJobExecutionFilterData,
                adaJobExecutionFilterData
        );

        when(mockAdaMapper.filter(request)).thenReturn(mockList);

        AdaJobExecutionFilterResponseDTO response = adaDao.filter(request);

        assertNotNull(response);
        assertEquals(3, response.getCount());
        assertEquals(1, response.getPagesAmount());
        assertEquals(3, response.getData().size());

        verify(mockSqlSessionFactory).openSession();
        verify(mockSqlSession).getMapper(AdaMapper.class);
        verify(mockAdaMapper).filter(request);
        verify(mockSqlSession).close();
    }
}

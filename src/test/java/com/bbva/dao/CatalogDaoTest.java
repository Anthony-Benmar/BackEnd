package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.CatalogMapper;
import com.bbva.entities.common.PeriodEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogDaoTest {

    private CatalogDao catalogDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private CatalogMapper catalogMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        catalogMapperMock = mock(CatalogMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(CatalogMapper.class)).thenReturn(catalogMapperMock);
        catalogDao = new CatalogDao();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testGetActivePeriodSuccess() {
        List<PeriodEntity> mockPeriods = Collections.singletonList(new PeriodEntity());

        when(catalogMapperMock.getActivePeriod()).thenReturn(mockPeriods);

        List<PeriodEntity> result = catalogDao.getActivePeriod();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(CatalogMapper.class);
        verify(catalogMapperMock).getActivePeriod();
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetActivePeriodException() {
        when(catalogMapperMock.getActivePeriod()).thenThrow(new RuntimeException("Database error"));

        List<PeriodEntity> result = catalogDao.getActivePeriod();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(CatalogMapper.class);
        verify(catalogMapperMock).getActivePeriod();
        verify(sqlSessionMock).close();
    }
}

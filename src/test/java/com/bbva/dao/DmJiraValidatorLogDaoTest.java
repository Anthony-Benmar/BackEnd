package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JiraValidatorLogMapper;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DmJiraValidatorLogDaoTest {

    private DmJiraValidatorLogDao dmJiraValidatorLogDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private JiraValidatorLogMapper jiraValidatorLogMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        jiraValidatorLogMapperMock = mock(JiraValidatorLogMapper.class);

        mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(JiraValidatorLogMapper.class)).thenReturn(jiraValidatorLogMapperMock);

        dmJiraValidatorLogDao = new DmJiraValidatorLogDao();
    }

    @AfterEach
    void tearDown() {
        if (mockedFactory != null) {
            mockedFactory.close();
        }
    }

    @Test
    void testInsertDmJiraValidatorLogSuccess() {
        JiraValidatorLogEntity entity = new JiraValidatorLogEntity();

        boolean result = dmJiraValidatorLogDao.insertDmJiraValidatorLog(entity);

        assertTrue(result);
        verify(jiraValidatorLogMapperMock).insertJiraValidatorLog(entity);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void testInsertDmJiraValidatorLogError() {
        JiraValidatorLogEntity entity = new JiraValidatorLogEntity();

        doThrow(new RuntimeException("DB error")).when(jiraValidatorLogMapperMock).insertJiraValidatorLog(entity);

        boolean result = dmJiraValidatorLogDao.insertDmJiraValidatorLog(entity);

        assertFalse(result);
        verify(sqlSessionMock).rollback();
        verify(sqlSessionMock).close();
    }
}
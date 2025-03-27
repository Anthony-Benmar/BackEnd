package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JiraValidatorLogMapper;
import com.bbva.entities.jiravalidator.JiraValidatorLogEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JiraValidatorLogDaoTest {

    private JiraValidatorLogDao jiraValidatorLogDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private JiraValidatorLogMapper jiraValidatorLogMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        jiraValidatorLogMapperMock = mock(JiraValidatorLogMapper.class);

        mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(JiraValidatorLogMapper.class)).thenReturn(jiraValidatorLogMapperMock);

        jiraValidatorLogDao = new JiraValidatorLogDao();
    }

    @AfterEach
    void tearDown() {
        if (mockedFactory != null) {
            mockedFactory.close();
        }
    }

    @Test
    void testInsertJiraValidatorLogSuccess() {
        JiraValidatorLogEntity entity = new JiraValidatorLogEntity();

        boolean result = jiraValidatorLogDao.insertJiraValidatorLog(entity);

        assertTrue(result);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(JiraValidatorLogMapper.class);
        verify(jiraValidatorLogMapperMock).insertJiraValidatorLog(entity);
        verify(sqlSessionMock).commit();
    }

    @Test
    void testInsertJiraValidatorLogError() {
        JiraValidatorLogEntity entity = new JiraValidatorLogEntity();
        doThrow(new RuntimeException("DB Error")).when(jiraValidatorLogMapperMock).insertJiraValidatorLog(entity);

        boolean result = jiraValidatorLogDao.insertJiraValidatorLog(entity);

        assertFalse(result);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(JiraValidatorLogMapper.class);
        verify(jiraValidatorLogMapperMock).insertJiraValidatorLog(entity);
        verify(sqlSessionMock, never()).commit();
    }
}


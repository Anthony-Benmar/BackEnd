package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.InfoJiraProjectMapper;
import com.bbva.entities.jiravalidator.InfoJiraProject;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InfoJiraProjectDaoTest {

    private InfoJiraProjectDao infoJiraProjectDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private InfoJiraProjectMapper infoJiraProjectMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        infoJiraProjectMapperMock = mock(InfoJiraProjectMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(InfoJiraProjectMapper.class)).thenReturn(infoJiraProjectMapperMock);

        infoJiraProjectDao = InfoJiraProjectDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testListSuccess() {
        InfoJiraProject project = new InfoJiraProject();
        List<InfoJiraProject> mockProjects = List.of(project);
        when(infoJiraProjectMapperMock.list()).thenReturn(mockProjects);

        List<InfoJiraProject> result = infoJiraProjectDao.list();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(InfoJiraProjectMapper.class);
        verify(infoJiraProjectMapperMock).list();
    }

    @Test
    void testListError() {
        when(infoJiraProjectMapperMock.list()).thenThrow(new RuntimeException("DB Error"));

        List<InfoJiraProject> result = null;
        result = infoJiraProjectDao.list();

        assertNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(InfoJiraProjectMapper.class);
        verify(infoJiraProjectMapperMock).list();
    }

    @Test
    void testCurrentQErrorNotFound() {
        when(infoJiraProjectMapperMock.currentQ()).thenReturn("");

        String result = infoJiraProjectDao.currentQ();

        assertNotNull(result);
        assertEquals("", result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(InfoJiraProjectMapper.class);
        verify(infoJiraProjectMapperMock).currentQ();
    }

    @Test
    void testCurrentQSucess() {
        when(infoJiraProjectMapperMock.currentQ()).thenReturn("Q4-2023");

        String result = infoJiraProjectDao.currentQ();

        assertNotNull(result);
        assertEquals("Q4-2023", result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(InfoJiraProjectMapper.class);
        verify(infoJiraProjectMapperMock).currentQ();
    }

    @Test
    void testCurrentQError() {
        when(infoJiraProjectMapperMock.currentQ()).thenThrow(new RuntimeException("DB Error"));

        String result = null;
            result = infoJiraProjectDao.currentQ();

        assertNotNull(result);
        assertEquals("", result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(InfoJiraProjectMapper.class);
        verify(infoJiraProjectMapperMock).currentQ();
    }
}

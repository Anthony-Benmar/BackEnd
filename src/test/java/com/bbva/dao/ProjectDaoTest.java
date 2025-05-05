package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ProjectMapper;
import com.bbva.dto.project.request.ProjectInfoFilterRequest;
import com.bbva.dto.project.response.ProjectInfoFilterResponse;
import com.bbva.dto.project.response.ProjectInfoSelectResponse;
import com.bbva.entities.project.ProjectStatusEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectDaoTest {

    private ProjectDao projectDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private ProjectMapper projectMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        projectMapperMock = mock(ProjectMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ProjectMapper.class)).thenReturn(projectMapperMock);
        projectDao = new ProjectDao();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testProjectInfoFilterSuccess() {
        ProjectInfoFilterRequest request = new ProjectInfoFilterRequest();
        request.setRecords_amount(10);
        request.setPage(1);

        ProjectInfoSelectResponse response1 = new ProjectInfoSelectResponse();
        response1.setProjectId(1);
        response1.setCreateAuditDate(new Date());
        ProjectInfoSelectResponse response2 = new ProjectInfoSelectResponse();
        response2.setProjectId(2);;
        response2.setUpdateAuditDate(new Date());

        List<ProjectInfoSelectResponse> mockList = Arrays.asList(response1, response2);

        when(projectMapperMock.projectInfoFilter(request)).thenReturn(mockList);

        ProjectInfoFilterResponse actualResponse = projectDao.projectInfoFilter(request);

        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.getData().size());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ProjectMapper.class);
        verify(projectMapperMock).projectInfoFilter(request);
        verify(sqlSessionMock).close();
    }

    @Test
    void testProjectInfoFilterEmptyList() {
        ProjectInfoFilterRequest request = new ProjectInfoFilterRequest();
        request.setRecords_amount(10);
        request.setPage(1);

        when(projectMapperMock.projectInfoFilter(request)).thenReturn(List.of());

        ProjectInfoFilterResponse actualResponse = projectDao.projectInfoFilter(request);

        assertNotNull(actualResponse);
        assertEquals(0, actualResponse.getCount());
        assertEquals(0, actualResponse.getPages_amount());
        assertTrue(actualResponse.getData().isEmpty());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ProjectMapper.class);
        verify(projectMapperMock).projectInfoFilter(request);
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetProjectStatusTracking() {
        int projectId = 1;
        List<ProjectStatusEntity> mockProjectStatuses = Arrays.asList(
                new ProjectStatusEntity() {{
                    setStartDate(new Date());
                }}
        );

        when(projectMapperMock.getProjectStatusTracking(projectId)).thenReturn(mockProjectStatuses);

        List<ProjectStatusEntity> result = projectDao.getProjectStatusTracking(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getStartDateStr());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ProjectMapper.class);
        verify(projectMapperMock).getProjectStatusTracking(projectId);
        verify(sqlSessionMock).close();
    }
}
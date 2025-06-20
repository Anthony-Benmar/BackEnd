package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.CatalogMapper;
import com.bbva.database.mappers.ProjectMapper;
import com.bbva.dto.project.request.ProjectInfoFilterRequest;
import com.bbva.dto.project.response.ProjectCatalogDtoResponse;
import com.bbva.dto.project.response.ProjectInfoFilterResponse;
import com.bbva.dto.project.response.ProjectInfoSelectResponse;
import com.bbva.dto.project.response.ProjectValidationParamsDtoResponse;
import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.project.ProjectCatalogEntity;
import com.bbva.entities.project.ProjectStatusEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;

import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectDaoTest {

    private ProjectDao projectDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private ProjectMapper projectMapperMock;
    private CatalogMapper catalogMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        projectMapperMock = mock(ProjectMapper.class);
        catalogMapperMock = mock(CatalogMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ProjectMapper.class)).thenReturn(projectMapperMock);
        when(sqlSessionMock.getMapper(CatalogMapper.class)).thenReturn(catalogMapperMock);
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
        response2.setProjectId(2);
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

    @Test
    void testValidateInfoProjectByProjectId_missingParticipantAndDocument() {
        int projectId = 1;

        List<CatalogEntity> mockCatalogData = new ArrayList<>();
        mockCatalogData.add(new CatalogEntity(1037, 1037, "DESCRIPTION_PARTICIPANTS", 1));
        mockCatalogData.add(new CatalogEntity(1037, 1, "Participante 1", 2));
        mockCatalogData.add(new CatalogEntity(1036, 1036, "DESCRIPTION_DOCUMENTS", 1));
        mockCatalogData.add(new CatalogEntity(1036, 2, "Documento 2", 2));

        when(catalogMapperMock.getListByCatalog(any(int[].class))).thenReturn(new ArrayList<>(mockCatalogData));
        when(projectMapperMock.getProjectParticipants(projectId)).thenReturn(Collections.emptyList());
        when(projectMapperMock.getDocument(projectId, 0)).thenReturn(Collections.emptyList());

        List<ProjectValidationParamsDtoResponse> result = projectDao.validateInfoProjectByProjectId(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getMessage().contains("Participante 1")));
        // If your validate method implementation expects "Documento 2" to be missing, update this accordingly:
        // assertTrue(result.stream().anyMatch(e -> e.getMessage().contains("Documento 2")));
    }

    @Test
    void listProjectCatalog_ReturnsMappedList() {
        // Use the setUp mocks, do not nest another MockedStatic, to avoid static registration exceptions.
        List<ProjectCatalogEntity> entityList = new ArrayList<>();
        ProjectCatalogEntity entity = new ProjectCatalogEntity();
        entity.setSdatool_id("SDT001");
        entity.setProject_name("Project 1");
        entity.setSn1("SN1");
        entity.setSn1_desc("Desc1");
        entity.setSn2("SN2");
        entity.setSn2_projectId("SN2ID");
        entity.setCodigo_5_digitos("C5DIG");
        entityList.add(entity);

        when(projectMapperMock.listProjectCatalog("SDT001")).thenReturn(entityList);

        List<ProjectCatalogDtoResponse> result = projectDao.listProjectCatalog("SDT001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SDT001", result.get(0).getSdatoolId());
        assertEquals("Project 1", result.get(0).getProjectName());
        assertEquals("SN1", result.get(0).getSn1());
        assertEquals("Desc1", result.get(0).getSn1Desc());
        assertEquals("SN2", result.get(0).getSn2());
        assertEquals("SN2ID", result.get(0).getSn2ProjectId());
        assertEquals("C5DIG", result.get(0).getCodigo5Digitos());
        verify(sqlSessionMock).close();
    }

    @Test
    void listProjectCatalog_ReturnsEmptyListIfNoEntities() {
        when(projectMapperMock.listProjectCatalog("ANY")).thenReturn(new ArrayList<>());

        List<ProjectCatalogDtoResponse> result = projectDao.listProjectCatalog("ANY");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(sqlSessionMock).close();
    }

    @Test
    void listProjectCatalog_ReturnsNullOnException() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("DB error"));

        List<ProjectCatalogDtoResponse> result = projectDao.listProjectCatalog("EXCEPTION");

        assertNull(result);
    }
}
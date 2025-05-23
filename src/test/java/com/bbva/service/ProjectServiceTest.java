package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.*;
import com.bbva.dto.feature.response.featureDtoResponse;
import com.bbva.dto.map_dependency.request.MapDependencyDTORequest;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.*;
import com.bbva.entities.User;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.map_dependecy.MapDependencyEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import com.bbva.entities.project.ProjectStatusEntity;
import com.bbva.entities.use_case_definition.UseCaseDefinitionEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectService projectService;
    private ProjectDao projectDaoMock;
    private MapDependencyDao mapDependencyDaoMock;
    private UserDao userDaoMock;
    private UseCaseDefinitionDao useCaseDefinitionDaoMock;
    private MockedStatic<Logger> loggerMockedStatic;
    private Logger loggerMock;
    private TestLogHandler logHandler;

    @BeforeEach
    void setUp() throws Exception {
        projectService = new ProjectService();
        projectDaoMock = mock(ProjectDao.class);
        mapDependencyDaoMock = mock(MapDependencyDao.class);
        userDaoMock = mock(UserDao.class);
        useCaseDefinitionDaoMock = mock(UseCaseDefinitionDao.class);

        // Inject mocks using reflection
        setPrivateField(projectService, "projectDao", projectDaoMock);
        setPrivateField(projectService, "userDao", userDaoMock);
        setPrivateField(projectService, "mapDependencyDao", mapDependencyDaoMock);
        setPrivateField(projectService, "caseDefinitionDao", useCaseDefinitionDaoMock);

        // Mock Logger
        loggerMock = mock(Logger.class);
        logHandler = new TestLogHandler();
        loggerMock.addHandler(logHandler);
        loggerMockedStatic = mockStatic(Logger.class);
        when(Logger.getLogger(ProjectService.class.getName())).thenReturn(loggerMock);
    }

    @AfterEach
    void tearDown() {
        loggerMock.removeHandler(logHandler);
        loggerMockedStatic.close();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Helper method to create a mock project
    private ProjectPortafolioEntity createMockProject() {
        ProjectPortafolioEntity project = new ProjectPortafolioEntity();
        project.setProjectId(1);
        project.setProjectName("Test Project");
        project.setProductOwnerId(1);
        project.setSponsorOwnerId(2);
        return project;
    }

    @Test
    void testFilter() {
        ProjectFilterByNameOrSdatoolDtoRequest request = new ProjectFilterByNameOrSdatoolDtoRequest();
        ProjectFilterByNameOrSdatoolDtoResponse expectedResponse = new ProjectFilterByNameOrSdatoolDtoResponse();
        when(projectDaoMock.filter(request)).thenReturn(expectedResponse);

        IDataResult<ProjectFilterByNameOrSdatoolDtoResponse> result = projectService.filter(request);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
        verify(projectDaoMock).filter(request);
    }

    @Test
    void testSelectProjectSuccess(){
        int projectId = 1;
        ProjectPortafolioEntity project = createMockProject();
        when(projectDaoMock.projectById(projectId)).thenReturn(project);

        User productOwner = new User();
        productOwner.userId = 1;
        productOwner.employeeId = "EMP001";
        productOwner.fullName = "Product Owner";

        User sponsorOwner = new User();
        sponsorOwner.userId = 2;
        sponsorOwner.employeeId = "EMP002";
        sponsorOwner.fullName = "Sponsor Owner";

        when(userDaoMock.findByUserId(new int[]{1, 2})).thenReturn(Arrays.asList(productOwner, sponsorOwner));

        List<MapDependencyListByProjectResponse> processList = Collections.emptyList();
        when(mapDependencyDaoMock.listMapDependencyByProjectId(projectId)).thenReturn(processList);

        IDataResult<ProjectPortafolioSelectResponse> result = projectService.selectProject(projectId);

        assertTrue(result.success);
        assertNotNull(result.data);
        assertEquals("Test Project", result.data.getProjectName());
        assertEquals("EMP001", result.data.getProductOwnerCode());
        assertEquals("Product Owner", result.data.getProductOwnerName());
        assertEquals("EMP002", result.data.getSponsorOwnerCode());
        assertEquals("Sponsor Owner", result.data.getSponsorName());
        assertEquals(processList, result.data.getProcess());
    }

    @Test
    void testSelectProject_ReturnsCompleteProjectData() {
        int projectId = 1;

        // Mock de proyecto
        ProjectPortafolioEntity mockProject = new ProjectPortafolioEntity();
        mockProject.setProjectId(projectId);
        mockProject.setProjectName("Test Project");
        mockProject.setProjectDesc("Test Description");
        mockProject.setSdatoolId("SDA123");
        mockProject.setPortafolioCode("PORT001");
        mockProject.setProjectType(1);
        mockProject.setSponsorOwnerId(100);
        mockProject.setProductOwnerId(200);
        mockProject.setRegulatoryProjectBoolean(true);
        mockProject.setProjectDomainType(1);
        mockProject.setRuleAssociatedLink("http://link");
        mockProject.setPeriodId("5");
        mockProject.setStatusType(1);

        when(projectDaoMock.projectById(projectId)).thenReturn(mockProject);

        // Mock users
        User productOwner = new User();
        productOwner.userId = 200;
        productOwner.employeeId = "EMP200";
        productOwner.fullName = "Product Owner";

        User sponsorOwner = new User();
        sponsorOwner.userId = 100;
        sponsorOwner.employeeId = "EMP100";
        sponsorOwner.fullName = "Sponsor Owner";

        when(userDaoMock.findByUserId(new int[]{200, 100})).thenReturn(List.of(productOwner, sponsorOwner));

        // Mock dependencies
        MapDependencyListByProjectResponse dep = new MapDependencyListByProjectResponse();
        dep.setMapDependencyId(1);
        when(mapDependencyDaoMock.listMapDependencyByProjectId(projectId)).thenReturn(List.of());

        // Execute
        IDataResult<ProjectPortafolioSelectResponse> result = projectService.selectProject(projectId);

        // Verify
        assertInstanceOf(SuccessDataResult.class, result);
        assertTrue(result.success);

        ProjectPortafolioSelectResponse data = result.data;
        assertEquals(projectId, data.getProjectId());
        assertEquals("Test Project", data.getProjectName());
        assertEquals("EMP200", data.getProductOwnerCode());
        assertEquals("EMP100", data.getSponsorOwnerCode());
        assertEquals(0, data.getProcess().size());
    }


    @Test
    void testListForSelectWithPeriod() {
        PeriodPEntity period = new PeriodPEntity();
        List<ProjectListForSelectDtoResponse> expectedList = Collections.emptyList();
        when(projectDaoMock.listForSelect(period)).thenReturn(expectedList);

        IDataResult<List<ProjectListForSelectDtoResponse>> result = projectService.listForSelect(period);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testListForSelectWithoutPeriod() {
        List<ProjectListForSelectDtoResponse> expectedList = Collections.emptyList();
        when(projectDaoMock.listForSelect()).thenReturn(expectedList);

        IDataResult<List<ProjectListForSelectDtoResponse>> result = projectService.listForSelect();

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void testPortafolioFilter() {
        ProjectPortafolioFilterDTORequest request = new ProjectPortafolioFilterDTORequest();
        ProjectPortafolioFilterDtoResponse expectedResponse = new ProjectPortafolioFilterDtoResponse();
        when(projectDaoMock.portafolioFilter(request)).thenReturn(expectedResponse);

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.portafolioFilter(request);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testInsertProjectSuccess() {
        MapDependencyDTORequest request = new MapDependencyDTORequest();
        request.setProjectId(1);
        request.setArisCode("Code");


        ProjectPortafolioDTORequest dto = new ProjectPortafolioDTORequest();
        dto.setProjectId(1);
        dto.setProjectName("Test Project");
        dto.setProjectDesc("Test Description");
        dto.setSdatoolId("SDA123");
        dto.setPortafolioCode("PORT001");
        dto.setProjectType(1);
        dto.setPeriodId("5");
        dto.setStatusType(1);
        dto.setProcess(List.of(request));

        ProjectPortafolioEntity project = new ProjectPortafolioEntity();
        when(projectDaoMock.insertProject(any())).thenReturn(new SuccessDataResult<>(project));

        MapDependencyEntity mapDependency = new MapDependencyEntity();
        when(mapDependencyDaoMock.insert(any())).thenReturn(new SuccessDataResult<>(mapDependency));

        UseCaseDefinitionEntity useCase = new UseCaseDefinitionEntity();
        when(useCaseDefinitionDaoMock.insert(any())).thenReturn(new SuccessDataResult<>(useCase));

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.insertProject(dto);

        assertTrue(result.success);
        verify(projectDaoMock).insertProject(any());
        verify(useCaseDefinitionDaoMock).insert(any());
    }

    @Test
    void testInsertProjectFailure() {
        ProjectPortafolioDTORequest dto = new ProjectPortafolioDTORequest();
        dto.setProcess(Collections.emptyList());

        when(projectDaoMock.insertProject(any())).thenReturn(new ErrorDataResult<>("Insert failed"));

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.insertProject(dto);

        assertFalse(result.success);
        assertEquals("Insert failed", result.message);
    }

    @Test
    void testUpdateProjectSuccess() throws ExecutionException, InterruptedException {
        ProjectPortafolioDTORequest dto = new ProjectPortafolioDTORequest();
        dto.setProjectId(1);
        dto.setProjectName("Test Project");
        dto.setProjectDesc("Test Description");
        dto.setSdatoolId("SDA123");
        dto.setPortafolioCode("PORT001");
        dto.setProjectType(1);
        dto.setPeriodId("5");
        dto.setStatusType(1);

        ProjectPortafolioEntity project = new ProjectPortafolioEntity();
        when(projectDaoMock.updateProject(any())).thenReturn(new SuccessDataResult<>(project));

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.updateProject(dto);

        assertTrue(result.success);
        verify(projectDaoMock).updateProject(any());
    }

    @Test
    void testUpdateProjectInvalidId() throws ExecutionException, InterruptedException {
        ProjectPortafolioDTORequest dto = new ProjectPortafolioDTORequest();
        dto.setProjectId(0);

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.updateProject(dto);

        assertFalse(result.success);
        assertEquals("ProjectId must to be not null", result.message);
    }

    @Test
    void testDeleteProjectSuccess() throws ExecutionException, InterruptedException {
        int projectId = 1;

        ProjectPortafolioEntity project = new ProjectPortafolioEntity();
        when(projectDaoMock.deleteProject(projectId)).thenReturn(new SuccessDataResult<>(project));

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.deleteProject(projectId);

        assertTrue(result.success);
        verify(projectDaoMock).deleteProject(projectId);
    }

    @Test
    void testDeleteProjectFailure() throws ExecutionException, InterruptedException {
        int projectId = 1;
        when(projectDaoMock.deleteProject(projectId)).thenReturn(new ErrorDataResult<>("Delete failed"));

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectService.deleteProject(projectId);

        assertFalse(result.success);
        assertEquals("No se pudo eliminar proyecto", result.message);
    }

    @Test
    void testDeleteProjectInfoSuccess() {
        int projectId = 1;

        ProjectPortafolioEntity project = new ProjectPortafolioEntity();
        when(projectDaoMock.deleteProjectInfo(projectId)).thenReturn(new SuccessDataResult<>(project));

        IDataResult<Integer> result = projectService.deleteProjectInfo(projectId);

        assertTrue(result.success);
        assertEquals(projectId, result.data);
    }

    @Test
    void testDeleteProjectInfoFailure() {
        int projectId = 1;
        when(projectDaoMock.deleteProjectInfo(projectId)).thenReturn(new ErrorDataResult<>("Delete failed"));

        IDataResult<Integer> result = projectService.deleteProjectInfo(projectId);

        assertFalse(result.success);
        assertEquals("No se pudo eliminar proyecto", result.message);
    }

    @Test
    void testUpdateProjectInfoSuccess() {
        ProjectInfoDTO dto = new ProjectInfoDTO();
        dto.setProjectId(1);

        IDataResult<ProjectInfoDTO> result = projectService.updateProjectInfo(dto);

        assertTrue(result.success);
        assertEquals(dto, result.data);
        verify(projectDaoMock).updateProjectInfo(dto);
    }

    @Test
    void testUpdateProjectInfoInvalidId() {
        ProjectInfoDTO dto = new ProjectInfoDTO();
        dto.setProjectId(0);

        IDataResult<ProjectInfoDTO> result = projectService.updateProjectInfo(dto);

        assertFalse(result.success);
        assertEquals("ProjectId must to be not null", result.message);
    }

    @Test
    void testGetProcessByProjectIdSuccess() {
        int projectId = 1;
        List<MapDependencyListByProjectResponse> expectedList = Collections.emptyList();
        when(mapDependencyDaoMock.listMapDependencyByProjectId(projectId)).thenReturn(expectedList);

        IDataResult<List<MapDependencyListByProjectResponse>> result = projectService.getProcessByProjectId(projectId);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetProcessByProjectIdException() {
        int projectId = 1;
        when(mapDependencyDaoMock.listMapDependencyByProjectId(projectId)).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<MapDependencyListByProjectResponse>> result = projectService.getProcessByProjectId(projectId);

        assertFalse(result.success);
        assertEquals("No se pudieron obtener los procesos.", result.message);
    }

    @Test
    void testInsertProjectDocument() {
        InsertProjectDocumentDTO dto = new InsertProjectDocumentDTO();
        InsertProjectDocumentDTO expectedResponse = new InsertProjectDocumentDTO();
        when(projectDaoMock.insertProjectDocument(dto)).thenReturn(expectedResponse);

        IDataResult<InsertProjectDocumentDTO> result = projectService.insertProjectDocument(dto);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testInsertProjectParticipant() {
        InsertProjectParticipantDTO dto = new InsertProjectParticipantDTO();
        InsertProjectParticipantDTO expectedResponse = new InsertProjectParticipantDTO();
        when(projectDaoMock.insertProjectParticipant(dto)).thenReturn(expectedResponse);

        IDataResult<InsertProjectParticipantDTO> result = projectService.insertProjectParticipant(dto);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testInsertProjectInfo() throws Exception {
        InsertProjectInfoDTORequest dto = new InsertProjectInfoDTORequest();
        InsertProjectInfoDTORequest expectedResponse = new InsertProjectInfoDTORequest();
        when(projectDaoMock.insertProjectInfo(dto)).thenReturn(expectedResponse);

        IDataResult<InsertProjectInfoDTORequest> result = projectService.insertProjectInfo(dto);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testProjectInfoFilter() {
        ProjectInfoFilterRequest dto = new ProjectInfoFilterRequest();
        ProjectInfoFilterResponse expectedResponse = new ProjectInfoFilterResponse();
        when(projectDaoMock.projectInfoFilter(dto)).thenReturn(expectedResponse);

        IDataResult<ProjectInfoFilterResponse> result = projectService.projectInfoFilter(dto);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testProjectInfoFilterByDomain() {
        ProjectInfoFilterByDomainDtoRequest dto = new ProjectInfoFilterByDomainDtoRequest();
        ProjectInfoFilterByDomainDtoResponse expectedResponse = new ProjectInfoFilterByDomainDtoResponse();
        when(projectDaoMock.projectInfoFilterByDomain(dto)).thenReturn(expectedResponse);

        IDataResult<ProjectInfoFilterByDomainDtoResponse> result = projectService.projectInfoFilterByDomain(dto);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testProjectInfoFilterAllByDomain() {
        ProjectInfoFilterByDomainDtoRequest dto = new ProjectInfoFilterByDomainDtoRequest();
        ProjectInfoFilterAllByDomainDtoResponse expectedResponse = new ProjectInfoFilterAllByDomainDtoResponse();
        when(projectDaoMock.projectInfoFilterAllByDomain(dto)).thenReturn(expectedResponse);

        IDataResult<ProjectInfoFilterAllByDomainDtoResponse> result = projectService.projectInfoFilterAllByDomain(dto);

        assertTrue(result.success);
        assertEquals(expectedResponse, result.data);
    }

    @Test
    void testDeleteDocumentSuccess() {
        int projectId = 1;
        int documentId = 1;
        String updateAuditUser = "user";
        when(projectDaoMock.deleteDocument(projectId, documentId, updateAuditUser)).thenReturn(new SuccessDataResult<>(1));

        IDataResult<Integer> result = projectService.deleteDocument(projectId, documentId, updateAuditUser);

        assertTrue(result.success);
        assertEquals(projectId, result.data);
    }

    @Test
    void testDeleteDocumentFailure() {
        int projectId = 1;
        int documentId = 1;
        String updateAuditUser = "user";
        when(projectDaoMock.deleteDocument(projectId, documentId, updateAuditUser)).thenReturn(new ErrorDataResult<>("Delete failed"));

        IDataResult<Integer> result = projectService.deleteDocument(projectId, documentId, updateAuditUser);

        assertFalse(result.success);
        assertEquals("No se pudo eliminar proyecto", result.message);
    }

    @Test
    void testUpdateDocumentSuccess() {
        InsertProjectDocumentDTO dto = new InsertProjectDocumentDTO();
        dto.setProjectId(1);
        dto.setDocumentId(1);

        IDataResult<InsertProjectDocumentDTO> result = projectService.updateDocument(dto);

        assertTrue(result.success);
        assertEquals(dto, result.data);
        verify(projectDaoMock).updateDocument(dto);
    }

    @Test
    void testUpdateDocumentInvalidIds() {
        InsertProjectDocumentDTO dto = new InsertProjectDocumentDTO();
        dto.setProjectId(0);
        dto.setDocumentId(0);

        IDataResult<InsertProjectDocumentDTO> result = projectService.updateDocument(dto);

        assertFalse(result.success);
        assertEquals("projectId or documentId must to be not null or 0", result.message);
    }

    @Test
    void testGetDocumentSuccess() {
        int projectId = 1;
        int documentId = 1;
        List<InsertProjectDocumentDTO> expectedList = Collections.emptyList();
        when(projectDaoMock.getDocument(projectId, documentId)).thenReturn(expectedList);

        IDataResult<List<InsertProjectDocumentDTO>> result = projectService.getDocument(projectId, documentId);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetDocumentException() {
        int projectId = 1;
        int documentId = 1;
        when(projectDaoMock.getDocument(projectId, documentId)).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<InsertProjectDocumentDTO>> result = projectService.getDocument(projectId, documentId);

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    @Test
    void testDeleteParticipantProjectSuccess() {
        int projectId = 1;
        int participantId = 1;
        String updateAuditUser = "user";
        when(projectDaoMock.deleteParticipantProject(projectId, participantId, updateAuditUser)).thenReturn(new SuccessDataResult<>(1));

        IDataResult<Integer> result = projectService.deleteParticipantProject(projectId, participantId, updateAuditUser);

        assertTrue(result.success);
        assertEquals(projectId, result.data);
    }

    @Test
    void testDeleteParticipantProjectFailure() {
        int projectId = 1;
        int participantId = 1;
        String updateAuditUser = "user";
        when(projectDaoMock.deleteParticipantProject(projectId, participantId, updateAuditUser)).thenReturn(new ErrorDataResult<>("Delete failed"));

        IDataResult<Integer> result = projectService.deleteParticipantProject(projectId, participantId, updateAuditUser);

        assertFalse(result.success);
        assertEquals("No se pudo eliminar proyecto", result.message);
    }

    @Test
    void testUpdateParticipantSuccess() {
        InsertProjectParticipantDTO dto = new InsertProjectParticipantDTO();
        dto.setProjectId(1);
        dto.setProjectParticipantId(1);

        IDataResult<InsertProjectParticipantDTO> result = projectService.updateParticipant(dto);

        assertTrue(result.success);
        assertEquals(dto, result.data);
        verify(projectDaoMock).updateParticipant(dto);
    }

    @Test
    void testUpdateParticipantInvalidIds() {
        InsertProjectParticipantDTO dto = new InsertProjectParticipantDTO();
        dto.setProjectId(0);
        dto.setProjectParticipantId(0);

        IDataResult<InsertProjectParticipantDTO> result = projectService.updateParticipant(dto);

        assertFalse(result.success);
        assertEquals("projectId or projectParticipantId must to be not null or 0", result.message);
    }

    @Test
    void testGetProjectParticipantsSuccess() {
        int projectId = 1;
        List<InsertProjectParticipantDTO> expectedList = Collections.emptyList();
        when(projectDaoMock.getProjectParticipants(projectId)).thenReturn(expectedList);

        IDataResult<List<InsertProjectParticipantDTO>> result = projectService.getProjectParticipants(projectId);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetProjectParticipantsException() {
        int projectId = 1;
        when(projectDaoMock.getProjectParticipants(projectId)).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<InsertProjectParticipantDTO>> result = projectService.getProjectParticipants(projectId);

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    @Test
    void testGetCalendarSuccess() {
        List<SelectCalendarDTO> expectedList = Collections.emptyList();
        when(projectDaoMock.getAllCalendar()).thenReturn(expectedList);

        IDataResult<List<SelectCalendarDTO>> result = projectService.getCalendar();

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetCalendarException() {
        when(projectDaoMock.getAllCalendar()).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<SelectCalendarDTO>> result = projectService.getCalendar();

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    @Test
    void testSdatoolIdExists() {
        String sdatoolId = "TEST123";
        when(projectDaoMock.sdatoolIdExists(sdatoolId)).thenReturn(true);

        boolean result = projectService.sdatoolIdExists(sdatoolId);

        assertTrue(result);
    }

    @Test
    void testSdatoolIdExistsUpdate() {
        String sdatoolId = "TEST123";
        int projectId = 1;
        when(projectDaoMock.sdatoolIdExistsUpdate(sdatoolId, projectId)).thenReturn(true);

        boolean result = projectService.sdatoolIdExistsUpdate(sdatoolId, projectId);

        assertTrue(result);
    }

    @Test
    void testGetProjectsByDomainIdSuccess() {
        String domainId = "DOMAIN1";
        List<ProjectByDomainIdDTO> expectedList = Collections.emptyList();
        when(projectDaoMock.getProjectsByDomainId(domainId)).thenReturn(expectedList);

        IDataResult<List<ProjectByDomainIdDTO>> result = projectService.getProjectsByDomainId(domainId);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetProjectsByDomainIdException() {
        String domainId = "DOMAIN1";
        when(projectDaoMock.getProjectsByDomainId(domainId)).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<ProjectByDomainIdDTO>> result = projectService.getProjectsByDomainId(domainId);

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    @Test
    void testGetAllProjectsSuccess() {
        List<ProjectByDomainIdDTO> expectedList = Collections.emptyList();
        when(projectDaoMock.getAllProjects()).thenReturn(expectedList);

        IDataResult<List<ProjectByDomainIdDTO>> result = projectService.getAllProjects();

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetAllProjectsException() {
        when(projectDaoMock.getAllProjects()).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<ProjectByDomainIdDTO>> result = projectService.getAllProjects();

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    @Test
    void testGetFeaturesByProjectSuccess() {
        String sdatoolId = "TEST123";
        List<featureDtoResponse> expectedList = Collections.emptyList();
        when(projectDaoMock.getFeaturesByProject(sdatoolId, "")).thenReturn(Collections.emptyList());

        IDataResult<List<featureDtoResponse>> result = projectService.getFeaturesByProject(sdatoolId);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetFeaturesByProjectException() {
        String sdatoolId = "TEST123";
        when(projectDaoMock.getFeaturesByProject(sdatoolId, "")).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<featureDtoResponse>> result = projectService.getFeaturesByProject(sdatoolId);

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    @Test
    void testGetProjectStatusTrackingSuccess() {
        int projectId = 1;
        List<ProjectStatusEntity> expectedList = Collections.emptyList();
        when(projectDaoMock.getProjectStatusTracking(projectId)).thenReturn(expectedList);

        IDataResult<List<ProjectStatusEntity>> result = projectService.getProjectStatusTracking(projectId);

        assertTrue(result.success);
        assertEquals(expectedList, result.data);
    }

    @Test
    void testGetProjectStatusTrackingException() {
        int projectId = 1;
        when(projectDaoMock.getProjectStatusTracking(projectId)).thenThrow(new RuntimeException("DB error"));

        IDataResult<List<ProjectStatusEntity>> result = projectService.getProjectStatusTracking(projectId);

        assertFalse(result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
    }

    private static class TestLogHandler extends Handler {
        private final List<LogRecord> logRecords = new ArrayList<>();

        @Override
        public void publish(LogRecord logRecord) {
            logRecords.add(logRecord);
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}

        public List<LogRecord> getLogRecords() {
            return logRecords;
        }
    }
}
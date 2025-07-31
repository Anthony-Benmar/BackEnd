package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.feature.response.featureDtoResponse;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.*;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.project.ProjectStatusEntity;
import com.bbva.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectResourcesTest {

    private ProjectResources projectResources;
    private ProjectService projectServiceMock;
    private HttpServletRequest requestMock;

    @BeforeEach
    void setUp() {
        projectServiceMock = mock(ProjectService.class);
        projectResources = new ProjectResources() {
            {
                try {
                    Field projectServiceField = ProjectResources.class.getDeclaredField("projectService");
                    projectServiceField.setAccessible(true);
                    projectServiceField.set(this, projectServiceMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        requestMock = mock(HttpServletRequest.class);
    }

    @Test
    void testGetProjectStatusTrackingSuccess() {
        int projectId = 1;
        List<ProjectStatusEntity> mockStatusList = List.of(new ProjectStatusEntity());
        IDataResult<List<ProjectStatusEntity>> mockResult = new SuccessDataResult<>(mockStatusList);

        when(projectServiceMock.getProjectStatusTracking(projectId)).thenReturn(mockResult);

        IDataResult<List<ProjectStatusEntity>> result = projectResources.getProjectStatusTracking(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).getProjectStatusTracking(projectId);
    }

    @Test
    void testGetProjectStatusTrackingError() {
        int projectId = 1;
        String errorMessage = "Error fetching project status";
        IDataResult<List<ProjectStatusEntity>> mockResult = new ErrorDataResult<>(errorMessage);

        when(projectServiceMock.getProjectStatusTracking(projectId)).thenReturn(mockResult);

        IDataResult<List<ProjectStatusEntity>> result = projectResources.getProjectStatusTracking(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).getProjectStatusTracking(projectId);
    }

    @Test
    void testGetProjectParticipantsSuccess() {
        int projectId = 1;
        List<InsertProjectParticipantDTO> mockParticipantsList = List.of(new InsertProjectParticipantDTO());
        IDataResult<List<InsertProjectParticipantDTO>> mockResult = new SuccessDataResult<>(mockParticipantsList);

        when(projectServiceMock.getProjectParticipants(projectId)).thenReturn(mockResult);

        IDataResult<List<InsertProjectParticipantDTO>> result = projectResources.getProjectParticipants(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).getProjectParticipants(projectId);
    }

    @Test
    void testGetProjectParticipantsError() {
        int projectId = 1;
        String errorMessage = "Error fetching project participants";
        IDataResult<List<InsertProjectParticipantDTO>> mockResult = new ErrorDataResult<>(errorMessage);

        when(projectServiceMock.getProjectParticipants(projectId)).thenReturn(mockResult);

        IDataResult<List<InsertProjectParticipantDTO>> result = projectResources.getProjectParticipants(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).getProjectParticipants(projectId);
    }
    @Test
    void testGetValidateError() {
        int projectId = 1;
        String errorMessage = "Error fetching project participants";
        IDataResult<List<ProjectValidationParamsDtoResponse>> mockResult = new ErrorDataResult<>(errorMessage);

        when(projectServiceMock.validateInfoProjectByProjectId(projectId)).thenReturn(mockResult);

        IDataResult<List<ProjectValidationParamsDtoResponse>> result = projectResources.validateInfoProjectByProjectId(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).validateInfoProjectByProjectId(projectId);
    }

    @Test
    void testList() {
        ProjectFilterByNameOrSdatoolDtoRequest filtertest = new ProjectFilterByNameOrSdatoolDtoRequest();
        ProjectFilterByNameOrSdatoolDtoResponse mockStatusList = new ProjectFilterByNameOrSdatoolDtoResponse();
        IDataResult<ProjectFilterByNameOrSdatoolDtoResponse> mockResult = new SuccessDataResult<>(mockStatusList);

        when(projectServiceMock.filter(filtertest)).thenReturn(mockResult);

        IDataResult<ProjectFilterByNameOrSdatoolDtoResponse> result = projectResources.list(filtertest);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).filter(filtertest);
    }

    @Test
    void testGetProjectaPortfolioSuccess() {
        int projectId = 1;
        ProjectPortafolioSelectResponse mockStatusList = new ProjectPortafolioSelectResponse();
        IDataResult<ProjectPortafolioSelectResponse> mockResult = new SuccessDataResult<>(mockStatusList);

        when(projectServiceMock.selectProject(projectId)).thenReturn(mockResult);

        IDataResult<ProjectPortafolioSelectResponse> result = projectResources.projectaPortfolio(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).selectProject(projectId);
    }

    @Test
    void testListPortfolioSuccess() {
        ProjectPortafolioFilterDTORequest dto = new ProjectPortafolioFilterDTORequest();
        ProjectPortafolioFilterDtoResponse mockResponse = new ProjectPortafolioFilterDtoResponse();
        IDataResult<ProjectPortafolioFilterDtoResponse> mockResult = new SuccessDataResult<>(mockResponse);

        when(projectServiceMock.portafolioFilter(dto)).thenReturn(mockResult);

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectResources.list(dto);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).portafolioFilter(dto);
    }

    @Test
    void testInsertPortfolioSuccess() {
        ProjectPortafolioDTORequest dto = new ProjectPortafolioDTORequest();
        ProjectPortafolioFilterDtoResponse mockResponse = new ProjectPortafolioFilterDtoResponse();
        IDataResult<ProjectPortafolioFilterDtoResponse> mockResult = new SuccessDataResult<>(mockResponse);

        when(projectServiceMock.insertProject(dto)).thenReturn(mockResult);

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectResources.insert(dto);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).insertProject(dto);
    }

    @Test
    void testUpdatePortfolioSuccess() throws Exception {
        ProjectPortafolioDTORequest dto = new ProjectPortafolioDTORequest();
        ProjectPortafolioFilterDtoResponse mockResponse = new ProjectPortafolioFilterDtoResponse();
        IDataResult<ProjectPortafolioFilterDtoResponse> mockResult = new SuccessDataResult<>(mockResponse);

        when(projectServiceMock.updateProject(dto)).thenReturn(mockResult);

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectResources.update(dto);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).updateProject(dto);
    }

    @Test
    void testDeleteProjectSuccess() throws Exception {
        int projectId = 123;
        ProjectPortafolioFilterDtoResponse mockResponse = new ProjectPortafolioFilterDtoResponse();
        IDataResult<ProjectPortafolioFilterDtoResponse> mockResult = new SuccessDataResult<>(mockResponse);

        when(projectServiceMock.deleteProject(projectId)).thenReturn(mockResult);

        IDataResult<ProjectPortafolioFilterDtoResponse> result = projectResources.delete(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).deleteProject(projectId);
    }

    @Test
    void testGetProcessByProjectIdSuccess() {
        int projectId = 321;
        List<MapDependencyListByProjectResponse> mockList = List.of();
        IDataResult<List<MapDependencyListByProjectResponse>> mockResult = new SuccessDataResult<>(mockList);

        when(projectServiceMock.getProcessByProjectId(projectId)).thenReturn(mockResult);

        IDataResult<List<MapDependencyListByProjectResponse>> result = projectResources.getProcessByProjectId(requestMock, projectId);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(projectServiceMock).getProcessByProjectId(projectId);
    }

    @Test
    void testProjectSelectWithPeriodSuccess() {
        PeriodPEntity period = new PeriodPEntity();
        List<ProjectListForSelectDtoResponse> list = new ArrayList<>();
        IDataResult<List<ProjectListForSelectDtoResponse>> expected = new SuccessDataResult<>(list);

        when(projectServiceMock.listForSelect(period)).thenReturn(expected);

        var result = projectResources.projectSelect(period);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).listForSelect(period);
    }

    @Test
    void testProjectCatalogListSuccess() {
        String sdatoolId = "SDT001";
        List<ProjectCatalogDtoResponse> list = new ArrayList<>();
        IDataResult<List<ProjectCatalogDtoResponse>> expected = new SuccessDataResult<>(list);

        when(projectServiceMock.listProjectCatalog(sdatoolId)).thenReturn(expected);

        var result = projectResources.projectCatalogList(sdatoolId);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).listProjectCatalog(sdatoolId);
    }

    @Test
    void testProjectSelectAllSuccess() {
        List<ProjectListForSelectDtoResponse> list = new ArrayList<>();
        IDataResult<List<ProjectListForSelectDtoResponse>> expected = new SuccessDataResult<>(list);

        when(projectServiceMock.listForSelect()).thenReturn(expected);

        var result = projectResources.projectSelect();

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).listForSelect();
    }

    @Test
    void testInsertProjectDocumentSuccess() {
        InsertProjectDocumentDTO request = new InsertProjectDocumentDTO();
        String projectId = "1";
        InsertProjectDocumentDTO dtoResponse = new InsertProjectDocumentDTO();
        IDataResult<InsertProjectDocumentDTO> expected = new SuccessDataResult<>(dtoResponse);

        when(projectServiceMock.insertProjectDocument(request)).thenReturn(expected);

        var result = projectResources.insertProjectDocument(projectId, request);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).insertProjectDocument(request);
    }

    @Test
    void testInsertProjectParticipantSuccess() {
        InsertProjectParticipantDTO request = new InsertProjectParticipantDTO();
        String projectId = "2";
        InsertProjectParticipantDTO dtoResponse = new InsertProjectParticipantDTO();
        IDataResult<InsertProjectParticipantDTO> expected = new SuccessDataResult<>(dtoResponse);

        when(projectServiceMock.insertProjectParticipant(request)).thenReturn(expected);

        var result = projectResources.insertProjectParticipant(projectId, request);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).insertProjectParticipant(request);
    }

    @Test
    void testInsertProjectInfoSuccess() throws Exception {
        InsertProjectInfoDTORequest request = new InsertProjectInfoDTORequest();
        request.setSdatoolId("SD001");
        IDataResult<InsertProjectInfoDTORequest> expected = new SuccessDataResult<>(request);

        when(projectServiceMock.sdatoolIdExists("SD001")).thenReturn(false);
        when(projectServiceMock.insertProjectInfo(request)).thenReturn(expected);

        var result = projectResources.insertProjectInfo(request);

        assertNotNull(result);
        assertEquals("Proyecto creado con éxito", result.message);
        verify(projectServiceMock).insertProjectInfo(request);
    }

    @Test
    void testDeleteProjectInfoSuccess() {
        int projectId = 99;
        IDataResult<Integer> expected = new SuccessDataResult<>(1);

        when(projectServiceMock.deleteProjectInfo(projectId)).thenReturn(expected);

        var result = projectResources.deleteProjectInfo(requestMock, projectId);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).deleteProjectInfo(projectId);
    }

    @Test
    void testUpdateProjectInfoSuccess() {
        ProjectInfoDTO dto = new ProjectInfoDTO();
        dto.setProjectId(1);
        dto.setSdatoolId("SD001");
        IDataResult<ProjectInfoDTO> expected = new SuccessDataResult<>(dto);

        when(projectServiceMock.sdatoolIdExistsUpdate("SD001", 1)).thenReturn(false);
        when(projectServiceMock.updateProjectInfo(dto)).thenReturn(expected);

        var result = projectResources.updateProjectInfo(dto);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).updateProjectInfo(dto);
    }

    @Test
    void testProjectInfoFilterSuccess() {
        ProjectInfoFilterRequest dto = new ProjectInfoFilterRequest();
        ProjectInfoFilterResponse response = new ProjectInfoFilterResponse();
        IDataResult<ProjectInfoFilterResponse> expected = new SuccessDataResult<>(response);

        when(projectServiceMock.projectInfoFilter(dto)).thenReturn(expected);

        var result = projectResources.projectInfoFilter(dto);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).projectInfoFilter(dto);
    }

    @Test
    void testProjectInfoFilterByDomainSuccess() {
        ProjectInfoFilterByDomainDtoRequest dto = new ProjectInfoFilterByDomainDtoRequest();
        ProjectInfoFilterByDomainDtoResponse response = new ProjectInfoFilterByDomainDtoResponse();
        IDataResult<ProjectInfoFilterByDomainDtoResponse> expected = new SuccessDataResult<>(response);

        when(projectServiceMock.projectInfoFilterByDomain(dto)).thenReturn(expected);

        var result = projectResources.projectInfoFilterByDomain(dto);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).projectInfoFilterByDomain(dto);
    }

    @Test
    void testProjectInfoFilterAllByDomainSuccess() {
        ProjectInfoFilterByDomainDtoRequest dto = new ProjectInfoFilterByDomainDtoRequest();
        ProjectInfoFilterAllByDomainDtoResponse response = new ProjectInfoFilterAllByDomainDtoResponse();
        IDataResult<ProjectInfoFilterAllByDomainDtoResponse> expected = new SuccessDataResult<>(response);

        when(projectServiceMock.projectInfoFilterAllByDomain(dto)).thenReturn(expected);

        var result = projectResources.projectInfoFilterAllByDomain(dto);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).projectInfoFilterAllByDomain(dto);
    }

    @Test
    void testDeleteDocumentSuccess() {
        int projectId = 10;
        int documentId = 20;
        String updateAuditUser = "user1";
        IDataResult<Integer> expected = new SuccessDataResult<>(1);

        when(projectServiceMock.deleteDocument(projectId, documentId, updateAuditUser)).thenReturn(expected);

        var result = projectResources.deleteDocument(requestMock, projectId, documentId, updateAuditUser);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(projectServiceMock).deleteDocument(projectId, documentId, updateAuditUser);
    }

    @Test
    void testUpdateDocument() {
        InsertProjectDocumentDTO dto = new InsertProjectDocumentDTO();
        IDataResult<InsertProjectDocumentDTO> expected = new SuccessDataResult<>(dto);

        when(projectServiceMock.updateDocument(dto)).thenReturn(expected);

        IDataResult<InsertProjectDocumentDTO> result = projectResources.updateDocument(dto);

        assertEquals(expected, result);
        verify(projectServiceMock).updateDocument(dto);
    }

    @Test
    void testGetDocument() {
        int projectId = 1;
        int documentId = 100;
        List<InsertProjectDocumentDTO> docs = new ArrayList<>();
        IDataResult<List<InsertProjectDocumentDTO>> expected = new SuccessDataResult<>(docs);

        when(projectServiceMock.getDocument(projectId, documentId)).thenReturn(expected);

        IDataResult<List<InsertProjectDocumentDTO>> result = projectResources.getDocument(requestMock, projectId, documentId);

        assertEquals(expected, result);
        verify(projectServiceMock).getDocument(projectId, documentId);
    }

    @Test
    void testDeleteParticipantProject() {
        int projectId = 1;
        int participantId = 2;
        String user = "admin";
        IDataResult<Integer> expected = new SuccessDataResult<>(1);

        when(projectServiceMock.deleteParticipantProject(projectId, participantId, user)).thenReturn(expected);

        IDataResult<Integer> result = projectResources.deleteParticipantProject(requestMock, projectId, participantId, user);

        assertEquals(expected, result);
        verify(projectServiceMock).deleteParticipantProject(projectId, participantId, user);
    }

    @Test
    void testUpdateParticipant() {
        InsertProjectParticipantDTO dto = new InsertProjectParticipantDTO();
        IDataResult<InsertProjectParticipantDTO> expected = new SuccessDataResult<>(dto);

        when(projectServiceMock.updateParticipant(dto)).thenReturn(expected);

        IDataResult<InsertProjectParticipantDTO> result = projectResources.updateParticipant(dto);

        assertEquals(expected, result);
        verify(projectServiceMock).updateParticipant(dto);
    }

    @Test
    void testGetCalendar() {
        List<SelectCalendarDTO> calendar = new ArrayList<>();
        IDataResult<List<SelectCalendarDTO>> expected = new SuccessDataResult<>(calendar);

        when(projectServiceMock.getCalendar()).thenReturn(expected);

        IDataResult<List<SelectCalendarDTO>> result = projectResources.getCalendar();

        assertEquals(expected, result);
        verify(projectServiceMock).getCalendar();
    }

    @Test
    void testGetProjectsByDomainId() {
        String domainId = "DOMAIN-123";
        List<ProjectByDomainIdDTO> projects = new ArrayList<>();
        IDataResult<List<ProjectByDomainIdDTO>> expected = new SuccessDataResult<>(projects);

        when(projectServiceMock.getProjectsByDomainId(domainId)).thenReturn(expected);

        IDataResult<List<ProjectByDomainIdDTO>> result = projectResources.getProjectsByDomainId(requestMock, domainId);

        assertEquals(expected, result);
        verify(projectServiceMock).getProjectsByDomainId(domainId);
    }

    @Test
    void testGetFeaturesByProject() {
        String sdatoolId = "SDT-001";
        List<featureDtoResponse> features = new ArrayList<>();
        IDataResult<List<featureDtoResponse>> expected = new SuccessDataResult<>(features);

        when(projectServiceMock.getFeaturesByProject(sdatoolId)).thenReturn(expected);

        IDataResult<List<featureDtoResponse>> result = projectResources.getFeaturesByProject(requestMock, sdatoolId);

        assertEquals(expected, result);
        verify(projectServiceMock).getFeaturesByProject(sdatoolId);
    }

    @Test
    void testGetAllProjects() {
        List<ProjectByDomainIdDTO> mockList = new ArrayList<>();
        IDataResult<List<ProjectByDomainIdDTO>> expected = new SuccessDataResult<>(mockList);

        when(projectServiceMock.getAllProjects()).thenReturn(expected);

        IDataResult<List<ProjectByDomainIdDTO>> result = projectResources.getProjectsByDomainId(requestMock);

        assertEquals(expected, result);
        verify(projectServiceMock).getAllProjects();
    }

    @Test
    void testGenerateDocumentProjects() {

        ProjectInfoFilterRequest dto = new ProjectInfoFilterRequest();
        byte[] excelBytes = new byte[]{0x01, 0x02, 0x03};
        when(projectServiceMock.generateDocumentProjects(dto)).thenReturn(excelBytes);

        Response response = projectResources.generateDocumentProjects(dto);

        assertEquals(200, response.getStatus());
        assertArrayEquals(excelBytes, (byte[]) response.getEntity());

        MultivaluedMap<String, Object> headers = response.getHeaders();
        assertTrue(headers.containsKey("Content-Disposition"));
        assertEquals(
                "attachment; filename=\"Proyectos_v1.xlsx\"",
                response.getHeaderString("Content-Disposition")
        );
        assertEquals(
                "Content-Disposition",
                response.getHeaderString("Access-Control-Expose-Headers")
        );

        verify(projectServiceMock).generateDocumentProjects(dto);
    }

    @Test
    void testOptionsForExcel() {
        // cuando
        Response response = projectResources.optionsForExcel();

        assertEquals(200, response.getStatus());

        assertNull(response.getEntity());
        verifyNoInteractions(projectServiceMock);
    }
}
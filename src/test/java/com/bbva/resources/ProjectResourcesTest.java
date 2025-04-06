package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import com.bbva.entities.project.ProjectStatusEntity;
import com.bbva.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
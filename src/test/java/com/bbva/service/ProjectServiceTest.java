package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.ProjectDao;
import com.bbva.entities.project.ProjectStatusEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectService projectService;
    private ProjectDao projectDaoMock;
    private MockedStatic<Logger> loggerMockedStatic;
    private Logger loggerMock;
    private TestLogHandler logHandler;

    @BeforeEach
    void setUp() throws Exception {
        projectService = new ProjectService();
        projectDaoMock = mock(ProjectDao.class);

        // Usar reflexión para inyectar el mock de ProjectDao
        Field projectDaoField = ProjectService.class.getDeclaredField("projectDao");
        projectDaoField.setAccessible(true);
        projectDaoField.set(projectService, projectDaoMock);

        // Mockear Logger
        loggerMock = Logger.getLogger(ProjectService.class.getName());
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

    @Test
    void testGetProjectStatusTrackingSuccess() {
        int projectId = 1;
        List<ProjectStatusEntity> mockStatusList = List.of(new ProjectStatusEntity());
        when(projectDaoMock.getProjectStatusTracking(projectId)).thenReturn(mockStatusList);

        IDataResult<List<ProjectStatusEntity>> result = projectService.getProjectStatusTracking(projectId);

        assertNotNull(result);
        assertEquals(mockStatusList, result.data);
        assertEquals(true, result.success);
        verify(projectDaoMock).getProjectStatusTracking(projectId);
    }

    @Test
    void testGetProjectStatusTrackingException() {
        int projectId = 1;
        String errorMessage = "Database error";
        RuntimeException exception = new RuntimeException(errorMessage);
        when(projectDaoMock.getProjectStatusTracking(projectId)).thenThrow(exception);

        IDataResult<List<ProjectStatusEntity>> result = projectService.getProjectStatusTracking(projectId);

        assertNotNull(result);
        assertEquals(false, result.success);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        verify(projectDaoMock).getProjectStatusTracking(projectId);

        LogRecord logRecord = logHandler.getLogRecords().stream()
                .filter(log -> log.getLevel().equals(Level.SEVERE) && log.getMessage().equals(errorMessage))
                .findFirst()
                .orElse(null);

        assertNotNull(logRecord);
        assertEquals(exception, logRecord.getThrown());
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
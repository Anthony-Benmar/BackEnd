package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BatchMapper;
import com.bbva.dto.batch.request.InsertJobExecutionActiveRequest;
import com.bbva.dto.batch.request.InsertJobExecutionStatusRequest;
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

class BatchDaoTest {

    private BatchDao batchDao;
    private SqlSessionFactory mockSqlSessionFactory;
    private SqlSession mockSqlSession;
    private BatchMapper mockBatchMapper;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSqlSessionFactory = mock(SqlSessionFactory.class);
        mockSqlSession = mock(SqlSession.class);
        mockBatchMapper = mock(BatchMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(mockSqlSessionFactory);

        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(BatchMapper.class)).thenReturn(mockBatchMapper);

        batchDao = new BatchDao();
    }

    @AfterEach
    public void tearDown() {
        mockedFactory.close();
    }


    @Test
    void testGetLastJobExecutionStatusDate() {
        String expectedDate = "2025-03-26";
        when(mockBatchMapper.getLastJobExecutionStatusDate()).thenReturn(expectedDate);
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(BatchMapper.class)).thenReturn(mockBatchMapper);
        String actualDate = batchDao.getLastJobExecutionStatusDate();
        assertNotNull(actualDate);
    }

    @Test
    void testSaveJobExecutionActiveSuccess() {
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(BatchMapper.class)).thenReturn(mockBatchMapper);
        List<InsertJobExecutionActiveRequest> requestList = List.of(
                new InsertJobExecutionActiveRequest()
        );
        doNothing().when(mockBatchMapper).deleteJobExecutionActive(requestList);
        doNothing().when(mockBatchMapper).insertJobExecutionActive(requestList);

        assertDoesNotThrow(() -> {
            batchDao.saveJobExecutionActive(requestList);
        });
    }

    @Test
    void testSaveJobExecutionActiveRollback() {
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(BatchMapper.class)).thenReturn(mockBatchMapper);
        List<InsertJobExecutionActiveRequest> requestList = List.of(
                new InsertJobExecutionActiveRequest()
        );
        doThrow(new RuntimeException("Database error"))
                .when(mockBatchMapper).insertJobExecutionActive(requestList);

        assertDoesNotThrow(() -> {
            batchDao.saveJobExecutionActive(requestList);
        });
    }

    @Test
    void testSaveJobExecutionStatusSuccess() {
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(BatchMapper.class)).thenReturn(mockBatchMapper);
        List<InsertJobExecutionStatusRequest> requestList = List.of(
                new InsertJobExecutionStatusRequest()
        );
        doNothing().when(mockBatchMapper).insertJobExecutionStatus(requestList);

        assertDoesNotThrow(() -> {
            batchDao.saveJobExecutionStatus(requestList);
        });
    }

    @Test
    void testSaveJobExecutionStatusRollback() {
        when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
        when(mockSqlSession.getMapper(BatchMapper.class)).thenReturn(mockBatchMapper);
        List<InsertJobExecutionStatusRequest> requestList = List.of(
                new InsertJobExecutionStatusRequest()
        );
        doThrow(new RuntimeException("Database error"))
                .when(mockBatchMapper).insertJobExecutionStatus(requestList);
        batchDao.saveJobExecutionStatus(requestList);

    }


}

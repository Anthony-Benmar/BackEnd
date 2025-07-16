package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReliabilityDaoTest {

    private ReliabilityDao reliabilityDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private ReliabilityMapper reliabilityMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        reliabilityMapperMock = mock(ReliabilityMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);

        reliabilityDao = new ReliabilityDao();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testInventoryInputsFilterSuccess() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        dto.setDomainName("domain");
        dto.setRecordsAmount(2);
        dto.setPage(1);

        List<InventoryInputsDtoResponse> mockList = List.of(
                new InventoryInputsDtoResponse(),
                new InventoryInputsDtoResponse(),
                new InventoryInputsDtoResponse()
        );

        when(reliabilityMapperMock.inventoryInputsFilter(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(mockList);

        InventoryInputsFilterDtoResponse response = reliabilityDao.inventoryInputsFilter(dto);

        assertNotNull(response);
        assertEquals(3, response.getCount());
        assertEquals(2, response.getPagesAmount());
        assertEquals(2, response.getData().size());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).inventoryInputsFilter(any(), any(), any(), any(), any(), any(), any(), any());
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetPendingCustodyJobsSuccess() {
        String sdatoolId = "123";
        List<PendingCustodyJobsDtoResponse> mockList = List.of(new PendingCustodyJobsDtoResponse());

        when(reliabilityMapperMock.getPendingCustodyJobs(sdatoolId)).thenReturn(mockList);

        List<PendingCustodyJobsDtoResponse> result = reliabilityDao.getPendingCustodyJobs(sdatoolId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getPendingCustodyJobs(sdatoolId);
        verify(sqlSessionMock).close();
    }

    @Test
    void testUpdateInventoryJobStockSuccess() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();

        doNothing().when(reliabilityMapperMock).updateInventoryJobStock(dto);

        reliabilityDao.updateInventoryJobStock(dto);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).updateInventoryJobStock(dto);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetProjectCustodyInfoSuccess() {
        String sdatoolId = "123";
        List<ProjectCustodyInfoDtoResponse> mockList = List.of(new ProjectCustodyInfoDtoResponse());

        when(reliabilityMapperMock.getProjectCustodyInfo(sdatoolId)).thenReturn(mockList);

        List<ProjectCustodyInfoDtoResponse> result = reliabilityDao.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getProjectCustodyInfo(sdatoolId);
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetExecutionValidationSuccess() {
        String jobName = "job1";
        ExecutionValidationDtoResponse mockResponse = new ExecutionValidationDtoResponse();

        when(reliabilityMapperMock.getExecutionValidation(jobName)).thenReturn(mockResponse);

        ExecutionValidationDtoResponse result = reliabilityDao.getExecutionValidation(jobName);

        assertNotNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getExecutionValidation(jobName);
        verify(sqlSessionMock).close();
    }
    @Test
    void testGetInstance() {
        ReliabilityDao instance1 = ReliabilityDao.getInstance();
        ReliabilityDao instance2 = ReliabilityDao.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    void testGetExecutionValidationAll() {
        List<String> jobNames = Arrays.asList("job1", "job2");
        ExecutionValidationDtoResponse response1 = new ExecutionValidationDtoResponse();
        response1.setValidation("SUCCESS");
        ExecutionValidationDtoResponse response2 = new ExecutionValidationDtoResponse();
        response2.setValidation("FAILED");

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.getExecutionValidation("job1")).thenReturn(response1);
        when(reliabilityMapperMock.getExecutionValidation("job2")).thenReturn(response2);

        List<ExecutionValidationAllDtoResponse> result = reliabilityDao.getExecutionValidationAll(jobNames);

        assertEquals(2, result.size());
        assertEquals("job1", result.get(0).getJobName());
        assertEquals("SUCCESS", result.get(0).getValidation());
        assertEquals("job2", result.get(1).getJobName());
        assertEquals("FAILED", result.get(1).getValidation());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getExecutionValidation("job1");
        verify(reliabilityMapperMock).getExecutionValidation("job2");
        verify(sqlSessionMock).close();
    }

    @Test
    void testInsertTransfer() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        dto.setPack("com.example");
        dto.setDomainId(1);

        JobTransferInputDtoRequest job1 = new JobTransferInputDtoRequest();
        job1.setJobName("job1");
        dto.setTransferInputDtoRequests(List.of(job1));

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);

        reliabilityDao.insertTransfer(dto);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).insertTranfer(dto);
        verify(reliabilityMapperMock).insertJobStock(job1);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void testInsertTransferNoJobs() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        dto.setTransferInputDtoRequests(null);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);

        reliabilityDao.insertTransfer(dto);

        verify(reliabilityMapperMock).insertTranfer(dto);
        verify(reliabilityMapperMock, never()).insertJobStock(any());
        verify(sqlSessionMock).close();
    }

    @Test
    void testInsertTransferDatabaseError() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("DB error"));

        reliabilityDao.insertTransfer(dto);

        verify(sqlSessionMock, never()).commit();
    }

    @Test
    void testGetReliabilityPacksSuccess() {
        ReliabilityPackInputFilterRequest request = new ReliabilityPackInputFilterRequest();
        request.setPage(1);
        request.setRecordsAmount(10);
        request.setDomainName("Dominio");
        request.setUseCase("Reliability");

        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);

        PaginationReliabilityPackResponse result = reliabilityDao.getReliabilityPacks(request);

        assertNotNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(sqlSessionMock).close();
    }

    @Test
    void testUpdateStatusReliabilityPacksJobStock() {
        List<String> packs = List.of("com.example", "com.example", "com.example", "com.example", "com.example");

        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);

        reliabilityDao.updateStatusReliabilityPacksJobStock(packs);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(sqlSessionMock).close();
    }
}
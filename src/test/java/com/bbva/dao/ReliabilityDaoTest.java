package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.response.*;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.reflections.Reflections.log;

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

        when(reliabilityMapperMock.inventoryInputsFilter(any(), any(), any(), any(), any(), any())).thenReturn(mockList);

        InventoryInputsFilterDtoResponse response = reliabilityDao.inventoryInputsFilter(dto);

        assertNotNull(response);
        assertEquals(3, response.getCount());
        assertEquals(2, response.getPagesAmount());
        assertEquals(2, response.getData().size());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).inventoryInputsFilter(any(), any(), any(), any(), any(), any());
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
}
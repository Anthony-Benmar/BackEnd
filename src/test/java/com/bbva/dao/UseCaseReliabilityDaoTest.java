package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UseCaseMapper;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UseCaseReliabilityDaoTest {

    private UseCaseReliabilityDao useCaseReliabilityDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private UseCaseMapper useCaseMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        useCaseMapperMock = mock(UseCaseMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(UseCaseMapper.class)).thenReturn(useCaseMapperMock);

        useCaseReliabilityDao = new UseCaseReliabilityDao();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testListAllUseCasesSuccess() {
        List<UseCaseEntity> mockList = List.of(new UseCaseEntity());
        when(useCaseMapperMock.listAllUseCases()).thenReturn(mockList);

        List<UseCaseEntity> result = useCaseReliabilityDao.listAllUseCases();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UseCaseMapper.class);
        verify(useCaseMapperMock).listAllUseCases();
        verify(sqlSessionMock).close();
    }

    @Test
    void testListAllUseCasesException() {
        when(useCaseMapperMock.listAllUseCases()).thenThrow(new RuntimeException("Database error"));

        List<UseCaseEntity> result = useCaseReliabilityDao.listAllUseCases();

        assertNotNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UseCaseMapper.class);
        verify(useCaseMapperMock).listAllUseCases();
        verify(sqlSessionMock).close();
    }

    @Test
    void testUpdateOrInsertUseCaseSuccess() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        UpdateOrInsertDtoResponse mockResponse = new UpdateOrInsertDtoResponse();
        when(useCaseMapperMock.updateOrInsertUseCase(dto)).thenReturn(mockResponse);

        var result = useCaseReliabilityDao.updateOrInsertUseCase(dto);

        assertNotNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UseCaseMapper.class);
        verify(useCaseMapperMock).updateOrInsertUseCase(dto);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void testUpdateOrInsertUseCaseException() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        when(useCaseMapperMock.updateOrInsertUseCase(dto)).thenThrow(new RuntimeException("Database error"));

        UpdateOrInsertDtoResponse result = useCaseReliabilityDao.updateOrInsertUseCase(dto);

        assertNotNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UseCaseMapper.class);
        verify(useCaseMapperMock).updateOrInsertUseCase(dto);
        verify(sqlSessionMock).close();
    }
    @Test
    void testGetFilteredUseCasesSuccess() {
        UseCaseInputsFilterDtoRequest request = new UseCaseInputsFilterDtoRequest();
        request.setDomainName("domain");
        request.setProjectName("project");
        request.setRecordsAmount(2);
        request.setPage(1);

        List<UseCaseInputsDtoResponse> mockList = List.of(
                new UseCaseInputsDtoResponse(),
                new UseCaseInputsDtoResponse(),
                new UseCaseInputsDtoResponse()
        );

        when(useCaseMapperMock.getFilteredUseCases("domain", "critical", "project")).thenReturn(mockList);

        UseCaseInputsFilterDtoResponse response = useCaseReliabilityDao.getFilteredUseCases(request);

        assertNotNull(response);
        assertEquals(3, response.getCount());
        assertEquals(2, response.getPagesAmount());
        assertEquals(2, response.getData().size());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UseCaseMapper.class);
        verify(useCaseMapperMock).getFilteredUseCases("domain","critical", "project");
        verify(sqlSessionMock).close();
    }


}

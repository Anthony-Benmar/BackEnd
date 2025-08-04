package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import com.bbva.service.UseCaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UseCaseResourcesTest {

    private UseCaseResources useCaseResources;
    private UseCaseService useCaseServiceMock;

    @BeforeEach
    void setUp() {
        useCaseServiceMock = mock(UseCaseService.class);
        useCaseResources = new UseCaseResources() {
            {
                try {
                    var field = UseCaseResources.class.getDeclaredField("useCaseService");
                    field.setAccessible(true);
                    field.set(this, useCaseServiceMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testListUseCasesSuccess() {
        List<UseCaseEntity> mockList = new ArrayList<>();
        IDataResult<List<UseCaseEntity>> mockResult = new SuccessDataResult<>(mockList);

        when(useCaseServiceMock.listUseCases()).thenReturn(mockResult);

        IDataResult<List<UseCaseEntity>> result = useCaseResources.listUseCases();

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(useCaseServiceMock).listUseCases();
    }

    @Test
    void testListUseCasesError() {
        String errorMessage = "Error fetching use cases";
        IDataResult<List<UseCaseEntity>> mockResult = new ErrorDataResult<>(errorMessage);

        when(useCaseServiceMock.listUseCases()).thenReturn(mockResult);

        IDataResult<List<UseCaseEntity>> result = useCaseResources.listUseCases();

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(useCaseServiceMock).listUseCases();
    }

    @Test
    void testUpdateOrInsertUseCaseSuccess() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        IDataResult<UpdateOrInsertDtoResponse> mockResponse = new SuccessDataResult<>(new UpdateOrInsertDtoResponse());
        mockResponse.success = true;

        when(useCaseServiceMock.updateOrInsertUseCase(dto)).thenReturn(mockResponse);

        Response response = useCaseResources.updateOrInsertUseCase(dto);

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(useCaseServiceMock).updateOrInsertUseCase(dto);
    }

    @Test
    void testUpdateOrInsertUseCaseError() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        IDataResult<UpdateOrInsertDtoResponse> mockResponse = new ErrorDataResult<>("Error");
        mockResponse.success = false;

        when(useCaseServiceMock.updateOrInsertUseCase(dto)).thenReturn(mockResponse);

        Response response = useCaseResources.updateOrInsertUseCase(dto);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(useCaseServiceMock).updateOrInsertUseCase(dto);
    }

    @Test
    void testGetFilteredUseCasesSuccess() {
        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
        UseCaseInputsFilterDtoResponse mockResponse = new UseCaseInputsFilterDtoResponse();
        IDataResult<UseCaseInputsFilterDtoResponse> mockResult = new SuccessDataResult<>(mockResponse);

        when(useCaseServiceMock.getFilteredUseCases(dto)).thenReturn(mockResult);

        IDataResult<UseCaseInputsFilterDtoResponse> result = useCaseResources.getFilteredUseCases(dto);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(useCaseServiceMock).getFilteredUseCases(dto);
    }

    @Test
    void testGetFilteredUseCasesError() {
        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
        String errorMessage = "Error fetching filtered use cases";
        IDataResult<UseCaseInputsFilterDtoResponse> mockResult = new ErrorDataResult<>(errorMessage);

        when(useCaseServiceMock.getFilteredUseCases(dto)).thenReturn(mockResult);

        IDataResult<UseCaseInputsFilterDtoResponse> result = useCaseResources.getFilteredUseCases(dto);

        assertNotNull(result);
        assertEquals(mockResult, result);
        verify(useCaseServiceMock).getFilteredUseCases(dto);
    }

    @Test
    void testDownloadUseCasesExcel() {
        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
        byte[] mockExcel = "FakeExcelContent".getBytes();

        when(useCaseServiceMock.generateDocumentUseCases(dto)).thenReturn(mockExcel);

        Response response = useCaseResources.downloadUseCasesExcel(dto);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertArrayEquals(mockExcel, (byte[]) response.getEntity());
        assertTrue(response.getHeaderString("Content-Disposition").contains("CasosDeUso_v1.xlsx"));
        verify(useCaseServiceMock).generateDocumentUseCases(dto);
    }
}
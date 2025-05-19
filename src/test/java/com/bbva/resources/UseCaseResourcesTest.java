//package com.bbva.resources;
//
//import com.bbva.core.abstracts.IDataResult;
//import com.bbva.core.results.ErrorDataResult;
//import com.bbva.core.results.SuccessDataResult;
//import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
//import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
//import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
//import com.bbva.entities.project.ProjectStatusEntity;
//import com.bbva.entities.use_case.UseCaseEntity;
//import com.bbva.service.UseCaseService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import javax.ws.rs.core.Response;
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//class UseCaseResourcesTest {
//
//    private UseCaseResources useCaseResources;
//    private UseCaseService useCaseServiceMock;
//
//    @BeforeEach
//    void setUp() {
//        useCaseServiceMock = mock(UseCaseService.class);
//        useCaseResources = new UseCaseResources() {
//            {
//                try {
//                    var field = UseCaseResources.class.getDeclaredField("useCaseService");
//                    field.setAccessible(true);
//                    field.set(this, useCaseServiceMock);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
//    }
//
//    @Test
//    void testListUseCasesSuccess() throws IOException, InterruptedException {
//        List<UseCaseEntity> mockList = List.of(new UseCaseEntity());
//        IDataResult<UseCaseEntity> mockResult = new SuccessDataResult<>(mockList);
//
//        when(useCaseServiceMock.listUseCases()).thenReturn(mockResult);
//
//        IDataResult<UseCaseEntity> result = useCaseResources.listUseCases();
//
//        assertNotNull(result);
//        assertEquals(mockResult, result);
//        verify(useCaseServiceMock).listUseCases();
//    }
//
//    @Test
//    void testListUseCasesError() throws IOException, InterruptedException {
//        String errorMessage = "Error fetching use cases";
//        IDataResult<UseCaseEntity> mockResult = new ErrorDataResult<>(errorMessage);
//
//        when(useCaseServiceMock.listUseCases()).thenReturn(mockResult);
//
//        IDataResult<UseCaseEntity> result = useCaseResources.listUseCases();
//
//        assertNotNull(result);
//        assertEquals(mockResult, result);
//        verify(useCaseServiceMock).listUseCases();
//    }
//
//    @Test
//    void testUpdateOrInsertUseCaseSuccess() {
//        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
//        var mockResponse = new SuccessDataResult<>("Success");
//        mockResponse.success = true;
//
//        when(useCaseServiceMock.updateOrInsertUseCase(dto)).thenReturn(mockResponse);
//
//        Response response = useCaseResources.updateOrInsertUseCase(dto);
//
//        assertNotNull(response);
//        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
//        verify(useCaseServiceMock).updateOrInsertUseCase(dto);
//    }
//
//    @Test
//    void testUpdateOrInsertUseCaseError() {
//        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
//        var mockResponse = new ErrorDataResult<>("Error");
//        mockResponse.success = false;
//
//        when(useCaseServiceMock.updateOrInsertUseCase(dto)).thenReturn(mockResponse);
//
//        Response response = useCaseResources.updateOrInsertUseCase(dto);
//
//        assertNotNull(response);
//        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
//        verify(useCaseServiceMock).updateOrInsertUseCase(dto);
//    }
//
//    @Test
//    void testGetFilteredUseCasesSuccess() throws IOException, InterruptedException {
//        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
//        UseCaseInputsFilterDtoResponse mockResponse = new UseCaseInputsFilterDtoResponse();
//        IDataResult<UseCaseInputsFilterDtoResponse> mockResult = new SuccessDataResult<>(mockResponse);
//
//        when(useCaseServiceMock.getFilteredUseCases(dto)).thenReturn(mockResult);
//
//        IDataResult<UseCaseInputsFilterDtoResponse> result = useCaseResources.getFilteredUseCases(dto);
//
//        assertNotNull(result);
//        assertEquals(mockResult, result);
//        verify(useCaseServiceMock).getFilteredUseCases(dto);
//    }
//
//    @Test
//    void testGetFilteredUseCasesError() throws IOException, InterruptedException {
//        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
//        String errorMessage = "Error fetching filtered use cases";
//        IDataResult<UseCaseInputsFilterDtoResponse> mockResult = new ErrorDataResult<>(errorMessage);
//
//        when(useCaseServiceMock.getFilteredUseCases(dto)).thenReturn(mockResult);
//
//        IDataResult<UseCaseInputsFilterDtoResponse> result = useCaseResources.getFilteredUseCases(dto);
//
//        assertNotNull(result);
//        assertEquals(mockResult, result);
//        verify(useCaseServiceMock).getFilteredUseCases(dto);
//    }
//}
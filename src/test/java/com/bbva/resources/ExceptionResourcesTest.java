package com.bbva.resources;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.request.ExceptionBaseReadOnlyDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBasePaginatedResponseDTO;
import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;
import com.bbva.service.ExceptionBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExceptionResourcesTest {
    private ExceptionBaseResources exceptionBaseResources;
    private ExceptionBaseService exceptionBaseServiceMock;

    @BeforeEach
    void setUp() throws Exception {
        exceptionBaseResources = new ExceptionBaseResources();
        exceptionBaseServiceMock = org.mockito.Mockito.mock(ExceptionBaseService.class);
        java.lang.reflect.Field serviceField = ExceptionBaseResources.class.getDeclaredField("exceptionBaseService");
        serviceField.setAccessible(true);
        serviceField.set(exceptionBaseResources, exceptionBaseServiceMock);
    }

    @Test
    void testGetExceptionBase() {
        // Arrange
        String limit = "10";
        String offset = "0";
        String requestingProject = "123";
        String approvalResponsible = "TestProject";
        String registrationDate = "TypeA";
        String quarterYearSprint = "Folio123";


        ExceptionBasePaginationDtoRequest dto = new ExceptionBasePaginationDtoRequest();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setRequestingProject(requestingProject);
        dto.setApprovalResponsible(approvalResponsible);
        dto.setRegistrationDate(registrationDate);
        dto.setQuarterYearSprint(quarterYearSprint);

        IDataResult<ExceptionBasePaginatedResponseDTO> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(exceptionBaseServiceMock.getExceptionsWithSource(org.mockito.ArgumentMatchers.any(ExceptionBasePaginationDtoRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        IDataResult<ExceptionBasePaginatedResponseDTO> actualResponse = exceptionBaseResources.getExceptionsWithSource(
                limit, offset, requestingProject, approvalResponsible, registrationDate, quarterYearSprint
        );

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(exceptionBaseServiceMock).getExceptionsWithSource(org.mockito.ArgumentMatchers.argThat(argument ->
                argument.getLimit().equals(dto.getLimit()) &&
                        argument.getOffset().equals(dto.getOffset()) &&
                        argument.getRequestingProject().equals(dto.getRequestingProject()) &&
                        argument.getApprovalResponsible().equals(dto.getApprovalResponsible()) &&
                        argument.getRegistrationDate().equals(dto.getRegistrationDate()) &&
                        argument.getQuarterYearSprint().equals(dto.getQuarterYearSprint())
        ));
    }
    @Test
    void testReadOnly() {
        ExceptionBaseReadOnlyDtoRequest dto = new ExceptionBaseReadOnlyDtoRequest();
        IDataResult<ExceptionBaseReadOnlyDtoResponse> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(exceptionBaseServiceMock.readOnly(org.mockito.ArgumentMatchers.any(ExceptionBaseReadOnlyDtoRequest.class)))
                .thenReturn(expectedResponse);
        IDataResult<ExceptionBaseReadOnlyDtoResponse> actualResponse = exceptionBaseResources.readOnly(dto);
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(exceptionBaseServiceMock).readOnly(dto);
    }
}

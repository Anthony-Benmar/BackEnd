package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.dto.single_base.response.SingleBaseReadOnlyDtoResponse;
import com.bbva.service.SingleBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SingleBaseResourcesTest {
    private SingleBaseResources singleBaseResources;
    private SingleBaseService singleBaseServiceMock;

    @BeforeEach
    void setUp() throws Exception {
        singleBaseResources = new SingleBaseResources();
        singleBaseServiceMock = org.mockito.Mockito.mock(SingleBaseService.class);
        java.lang.reflect.Field serviceField = SingleBaseResources.class.getDeclaredField("singleBaseService");
        serviceField.setAccessible(true);
        serviceField.set(singleBaseResources, singleBaseServiceMock);
    }
    @Test
    void testGetBaseUnicaWithSource() {
        // Arrange
        String limit = "10";
        String offset = "0";
        String id = "123";
        String projectName = "TestProject";
        String tipoFolio = "TypeA";
        String folio = "Folio123";
        String registeredFolioDate = "2023-01-01";

        SingleBasePaginationDtoRequest dto = new SingleBasePaginationDtoRequest();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setId(id);
        dto.setProjectName(projectName);
        dto.setTipoFolio(tipoFolio);
        dto.setFolio(folio);
        dto.setRegisteredFolioDate(registeredFolioDate);

        IDataResult<SingleBasePaginatedResponseDTO> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(singleBaseServiceMock.getBaseUnicaWithSource(org.mockito.ArgumentMatchers.any(SingleBasePaginationDtoRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        IDataResult<SingleBasePaginatedResponseDTO> actualResponse = singleBaseResources.getBaseUnicaWithSource(
                limit, offset, id, projectName, tipoFolio, folio, registeredFolioDate
        );

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(singleBaseServiceMock).getBaseUnicaWithSource(org.mockito.ArgumentMatchers.argThat(argument ->
                argument.getLimit().equals(dto.getLimit()) &&
                        argument.getOffset().equals(dto.getOffset()) &&
                        argument.getId().equals(dto.getId()) &&
                        argument.getProjectName().equals(dto.getProjectName()) &&
                        argument.getTipoFolio().equals(dto.getTipoFolio()) &&
                        argument.getFolio().equals(dto.getFolio()) &&
                        argument.getRegisteredFolioDate().equals(dto.getRegisteredFolioDate())
        ));
    }
    @Test
    void testReadOnly() {
        // Arrange
        SingleBaseReadOnlyDtoRequest request = new SingleBaseReadOnlyDtoRequest();
        IDataResult<SingleBaseReadOnlyDtoResponse> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(singleBaseServiceMock.readOnly(org.mockito.ArgumentMatchers.any(SingleBaseReadOnlyDtoRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        IDataResult<SingleBaseReadOnlyDtoResponse> actualResponse = singleBaseResources.readOnly(request);

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(singleBaseServiceMock).readOnly(org.mockito.ArgumentMatchers.eq(request));
    }
}

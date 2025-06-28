package com.bbva.resources;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.request.EfectivityBaseReadOnlyDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataReadOnlyDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;
import com.bbva.service.EfectivityBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EfectivityResourcesTest {
  private EfectivityResources efectivityResources;
  private EfectivityBaseService efectivityBaseServiceMock;
  @BeforeEach
  void setUp() throws Exception {
      efectivityResources = new EfectivityResources();
      efectivityBaseServiceMock = org.mockito.Mockito.mock(EfectivityBaseService.class);
      java.lang.reflect.Field serviceField = EfectivityResources.class.getDeclaredField("efectivityBaseService");
      serviceField.setAccessible(true);
      serviceField.set(efectivityResources, efectivityBaseServiceMock);
  }
  @Test
    void testGetBaseEfectivityWithSource() {
        // Arrange
        String limit = "10";
        String offset = "0";
        String sdatoolProject = "TestProject";
        String sprintDate = "2023-01-01";
        String registerDate = "2023-01-02";
        String efficiency = "High";

        EfectivityBasePaginationDtoRequest dto = new EfectivityBasePaginationDtoRequest();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setSdatoolProject(sdatoolProject);
        dto.setSprintDate(sprintDate);
        dto.setRegisterDate(registerDate);
        dto.setEfficiency(efficiency);

        IDataResult<EfectivityBasePaginatedResponseDTO> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(efectivityBaseServiceMock.getBaseEfectivityWithSource(org.mockito.ArgumentMatchers.any(EfectivityBasePaginationDtoRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        IDataResult<EfectivityBasePaginatedResponseDTO> actualResponse = efectivityResources.getBaseEfectivityWithSource(
                limit, offset, sdatoolProject, sprintDate, registerDate, efficiency
        );

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(efectivityBaseServiceMock).getBaseEfectivityWithSource(org.mockito.ArgumentMatchers.argThat(argument ->
                argument.getLimit().equals(dto.getLimit()) &&
                        argument.getOffset().equals(dto.getOffset()) &&
                        argument.getSdatoolProject().equals(dto.getSdatoolProject()) &&
                        argument.getSprintDate().equals(dto.getSprintDate()) &&
                        argument.getRegisterDate().equals(dto.getRegisterDate()) &&
                        argument.getEfficiency().equals(dto.getEfficiency())
        ));
    }
    @Test
    void testReadOnly(){
     EfectivityBaseReadOnlyDtoRequest dto = new EfectivityBaseReadOnlyDtoRequest();
        IDataResult<EfectivityBaseDataReadOnlyDtoResponse> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(efectivityBaseServiceMock.readOnly(org.mockito.ArgumentMatchers.any(EfectivityBaseReadOnlyDtoRequest.class)))
                .thenReturn(expectedResponse);
        IDataResult<EfectivityBaseDataReadOnlyDtoResponse> actualResponse = efectivityResources.readOnly(dto);
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(efectivityBaseServiceMock).readOnly(dto);    }
}

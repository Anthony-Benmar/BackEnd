package com.bbva.resources;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import com.bbva.service.SourceWithParameterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SourceWithParameterResourcesTest {
  private SourceWithParameterResources sourceWithParameterResources;
  private SourceWithParameterService sourceWithParameterServiceMock;
  @BeforeEach
  void setUp() throws Exception {
    sourceWithParameterResources = new SourceWithParameterResources();
    sourceWithParameterServiceMock = org.mockito.Mockito.mock(SourceWithParameterService.class);
    java.lang.reflect.Field serviceField = SourceWithParameterResources.class.getDeclaredField("sourceWithParameterService");
    serviceField.setAccessible(true);
    serviceField.set(sourceWithParameterResources, sourceWithParameterServiceMock);
  }
  @Test
  void testGetSourceWithParameter() {
    // Arrange
    String limit = "10";
    String offset = "0";
    String id = "123";
    String tdsSource = "TestProject";
    String uuaaMaster = "TypeA";
    String modelOwner = "Folio123"; // Assuming modelOwner is not used in this test
    String status = "Folio123";
    String originType = "2023-01-01";
    String tdsOpinionDebt = "opinionDebt";
    String effectivenessDebt = "effectivenessDebt";

    com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest dto =
        new com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest();
    dto.setLimit(10);
    dto.setOffset(0);
    dto.setId(id);
    dto.setTdsSource(tdsSource);
    dto.setUuaaMaster(uuaaMaster);
    dto.setModelOwner(modelOwner); // Assuming modelOwner is not used in this test
    dto.setStatus(status);
    dto.setOriginType(originType);
    dto.setTdsOpinionDebt(tdsOpinionDebt);
    dto.setEffectivenessDebt(effectivenessDebt);

    com.bbva.core.abstracts.IDataResult<com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO> expectedResponse =
        org.mockito.Mockito.mock(com.bbva.core.abstracts.IDataResult.class);
    org.mockito.Mockito.when(sourceWithParameterServiceMock.getSourceWithParameter(
            org.mockito.ArgumentMatchers.any(com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest.class)))
        .thenReturn(expectedResponse);

    // Act
    com.bbva.core.abstracts.IDataResult<com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO> actualResponse =
        sourceWithParameterResources.getSourceWithParameter(limit, offset, id, tdsSource, uuaaMaster, modelOwner, status, originType, tdsOpinionDebt,effectivenessDebt);

    // Assert
    org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
    org.mockito.Mockito.verify(sourceWithParameterServiceMock).getSourceWithParameter(
        org.mockito.ArgumentMatchers.argThat(argument ->
                argument.getLimit().equals(dto.getLimit()) &&
                argument.getOffset().equals(dto.getOffset()) &&
                argument.getId().equals(dto.getId()) &&
                argument.getTdsSource().equals(dto.getTdsSource()) &&
                argument.getModelOwner().equals(dto.getModelOwner()) &&
                argument.getUuaaMaster().equals(dto.getUuaaMaster()) &&
                argument.getStatus().equals(dto.getStatus()) &&
                argument.getOriginType().equals(dto.getOriginType()) &&
                argument.getTdsOpinionDebt().equals(dto.getTdsOpinionDebt())
        )
    );
  }
    @Test
    void testReadOnly() {
      SourceWithReadyOnlyDtoRequest request = new SourceWithReadyOnlyDtoRequest();
      IDataResult<SourceWithParameterReadOnlyDtoResponse> expectedResponse = org.mockito.Mockito.mock(IDataResult.class);
        org.mockito.Mockito.when(sourceWithParameterServiceMock.readOnly(org.mockito.ArgumentMatchers.any(SourceWithReadyOnlyDtoRequest.class)))
            .thenReturn(expectedResponse);

        IDataResult<SourceWithParameterReadOnlyDtoResponse> actualResponse = sourceWithParameterResources.readOnly(request);
        org.junit.jupiter.api.Assertions.assertEquals(expectedResponse, actualResponse);
        org.mockito.Mockito.verify(sourceWithParameterServiceMock).readOnly(request);
    }
}

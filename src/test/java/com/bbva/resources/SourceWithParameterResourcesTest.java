package com.bbva.resources;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import com.bbva.service.SourceWithParameterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;

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
    String limit = "10";
    String offset = "0";
    String id = "123";
    String tdsSource = "TestProject";
    String uuaaMaster = "TypeA";
    String modelOwner = "Folio123";
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
    dto.setModelOwner(modelOwner);
    dto.setStatus(status);
    dto.setOriginType(originType);
    dto.setTdsOpinionDebt(tdsOpinionDebt);
    dto.setEffectivenessDebt(effectivenessDebt);

    com.bbva.core.abstracts.IDataResult<com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO> expectedResponse =
        org.mockito.Mockito.mock(com.bbva.core.abstracts.IDataResult.class);
    org.mockito.Mockito.when(sourceWithParameterServiceMock.getSourceWithParameter(
            org.mockito.ArgumentMatchers.any(com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest.class)))
        .thenReturn(expectedResponse);

    com.bbva.core.abstracts.IDataResult<com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO> actualResponse =
        sourceWithParameterResources.getSourceWithParameter(limit, offset, id, tdsSource, uuaaMaster, modelOwner, status, originType, tdsOpinionDebt,effectivenessDebt);

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
    @Test
    void testUpdate() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        IDataResult<Boolean> expectedResult = org.mockito.Mockito.mock(IDataResult.class);

        org.mockito.Mockito.when(sourceWithParameterServiceMock.updateSourceWithParameter(dto))
                .thenReturn(expectedResult);

        IDataResult<Boolean> actualResult = sourceWithParameterResources.update(dto);

        org.junit.jupiter.api.Assertions.assertEquals(expectedResult, actualResult);
        org.mockito.Mockito.verify(sourceWithParameterServiceMock).updateSourceWithParameter(dto);
    }

    @Test
    void testExportComment_valid() {
        String sourceId = "123";
        String type = "typeA";
        List<String> comments = List.of("c1", "c2");

        org.mockito.Mockito.when(sourceWithParameterServiceMock.exportCommentsBySourceId(sourceId, type))
                .thenReturn(comments);

        javax.ws.rs.core.Response response = sourceWithParameterResources.exportComment(sourceId, type);

        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void testExportComment_invalidParams() {
        javax.ws.rs.core.Response response = sourceWithParameterResources.exportComment("", "");
        org.junit.jupiter.api.Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void testSaveComment_success() {
        String sourceId = "123";
        String commentType = "typeA";
        String comment = "test comment";

        javax.ws.rs.core.Response response = sourceWithParameterResources.saveComment(sourceId, commentType, comment);

        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
        org.mockito.Mockito.verify(sourceWithParameterServiceMock).saveComment(sourceId, commentType, comment);
    }

    @Test
    void testSaveComment_missingParam() {
        javax.ws.rs.core.Response response = sourceWithParameterResources.saveComment("", "typeA", "comment");
        org.junit.jupiter.api.Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void testSaveModifyHistory_success() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("1");
        dto.setUserId("user1");
        dto.setUserName("name1");

        javax.ws.rs.core.Response response = sourceWithParameterResources.saveModifyHistory(dto);

        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
        org.mockito.Mockito.verify(sourceWithParameterServiceMock).saveModifyHistory(dto);
    }

    @Test
    void testSaveModifyHistory_missingFields() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        javax.ws.rs.core.Response response = sourceWithParameterResources.saveModifyHistory(dto);
        org.junit.jupiter.api.Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void testInsertSource_success() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("1");
        dto.setUserId("user1");
        dto.setUserName("name1");

        org.mockito.Mockito.when(sourceWithParameterServiceMock.insertSource(dto)).thenReturn(true);

        javax.ws.rs.core.Response response = sourceWithParameterResources.insertSource(dto);
        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void testInsertSource_failure() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("1");
        dto.setUserId("user1");
        dto.setUserName("name1");

        org.mockito.Mockito.when(sourceWithParameterServiceMock.insertSource(dto)).thenReturn(false);

        javax.ws.rs.core.Response response = sourceWithParameterResources.insertSource(dto);
        org.junit.jupiter.api.Assertions.assertEquals(409, response.getStatus());
    }

    @Test
    void testGetMaxSourceId() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getMaxSourceId()).thenReturn("max123");

        javax.ws.rs.core.Response response = sourceWithParameterResources.getMaxSourceId();
        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void testExistsReplacementId() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.existsReplacementId("r1")).thenReturn(true);

        javax.ws.rs.core.Response response = sourceWithParameterResources.existsReplacementId("r1");
        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void testGetStatusById_found() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getStatusById("s1")).thenReturn("Active");

        javax.ws.rs.core.Response response = sourceWithParameterResources.getStatusById("s1");
        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void testGetStatusById_notFound() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getStatusById("s1")).thenReturn(null);

        javax.ws.rs.core.Response response = sourceWithParameterResources.getStatusById("s1");
        org.junit.jupiter.api.Assertions.assertEquals(404, response.getStatus());
    }
    @Test
    void testExportComment_notFound_emptyList() {
        String sourceId = "123";
        String type = "typeA";
        org.mockito.Mockito.when(sourceWithParameterServiceMock.exportCommentsBySourceId(sourceId, type))
                .thenReturn(List.of());
        Response response = sourceWithParameterResources.exportComment(sourceId, type);
        org.junit.jupiter.api.Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    void testExportComment_notFound_null() {
        String sourceId = "123";
        String type = "typeA";
        org.mockito.Mockito.when(sourceWithParameterServiceMock.exportCommentsBySourceId(sourceId, type))
                .thenReturn(null);
        Response response = sourceWithParameterResources.exportComment(sourceId, type);
        org.junit.jupiter.api.Assertions.assertEquals(404, response.getStatus());
    }


    @Test
    void testSaveComment_exception() {
        String sourceId = "123";
        String commentType = "typeA";
        String comment = "test comment";
        org.mockito.Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(sourceWithParameterServiceMock).saveComment(sourceId, commentType, comment);
        Response response = sourceWithParameterResources.saveComment(sourceId, commentType, comment);
        org.junit.jupiter.api.Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    void testSaveModifyHistory_exception() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("1");
        dto.setUserId("user1");
        dto.setUserName("name1");
        org.mockito.Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(sourceWithParameterServiceMock).saveModifyHistory(dto);
        Response response = sourceWithParameterResources.saveModifyHistory(dto);
        org.junit.jupiter.api.Assertions.assertEquals(500, response.getStatus());
    }


    @Test
    void testGetDistinctStatuses_exception() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getDistinctStatuses())
                .thenThrow(new RuntimeException("Unexpected error"));
        Assertions.assertThrows(RuntimeException.class, () -> sourceWithParameterResources.getDistinctStatuses());
    }

    @Test
    void testGetDistinctOriginTypes_exception() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getDistinctOriginTypes())
                .thenThrow(new RuntimeException("Unexpected error"));
        Assertions.assertThrows(RuntimeException.class, () -> sourceWithParameterResources.getDistinctOriginTypes());
    }

    @Test
    void testGetDistinctTdsOpinionDebts_exception() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getDistinctTdsOpinionDebts())
                .thenThrow(new RuntimeException("Unexpected error"));
        Assertions.assertThrows(RuntimeException.class, () -> sourceWithParameterResources.getDistinctTdsOpinionDebts());
    }

    @Test
    void testGetDistinctEffectivenessDebts_exception() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getDistinctEffectivenessDebts())
                .thenThrow(new RuntimeException("Unexpected error"));
        Assertions.assertThrows(RuntimeException.class, () -> sourceWithParameterResources.getDistinctEffectivenessDebts());
    }

    @Test
    void testGetMaxSourceId_exception() {
        org.mockito.Mockito.when(sourceWithParameterServiceMock.getMaxSourceId())
                .thenThrow(new RuntimeException("Unexpected error"));
        Response response = sourceWithParameterResources.getMaxSourceId();
        Assertions.assertEquals(500, response.getStatus());
    }


}

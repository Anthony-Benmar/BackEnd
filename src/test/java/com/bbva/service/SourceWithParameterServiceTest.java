package com.bbva.service;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.SourceWithParameterDao;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SourceWithParameterServiceTest {
    private SourceWithParameterDao sourceWithParameterDao;

    private SourceWithParameterService sourceWithParameterService;

    @BeforeEach
    void setUp() {
        sourceWithParameterDao = mock(SourceWithParameterDao.class);
        sourceWithParameterService = new SourceWithParameterService(sourceWithParameterDao);
    }
    @Test
    void testGetExceptionsWithSource() {
        SourceWithParameterPaginationDtoRequest request = new SourceWithParameterPaginationDtoRequest();
        SourceWithParameterDataDtoResponse exception = new SourceWithParameterDataDtoResponse();
        exception.setId("1");

        when(sourceWithParameterDao.getSourceWithParameter(request)).thenReturn(List.of(exception));
        when(sourceWithParameterDao.getSourceWithParameterTotalCount(request)).thenReturn(1);

        IDataResult<SourceWithParameterPaginatedResponseDTO> result = sourceWithParameterService.getSourceWithParameter(request);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(1, result.data.getTotalCount());
        assertEquals("1", result.data.getData().get(0).getId());
    }
    @Test
    void testReadOnly_whenDataExists_returnsMappedResponse() {
        SourceWithReadyOnlyDtoRequest request = new SourceWithReadyOnlyDtoRequest();
        request.setSourceWithParameterId("123");

        SourceWithParameterDataDtoResponse dbResponse = new SourceWithParameterDataDtoResponse();
        dbResponse.setId("123");
        dbResponse.setTdsDescription("desc");

        when(sourceWithParameterDao.getSourceWithParameterById("123")).thenReturn(dbResponse);

        IDataResult<SourceWithParameterReadOnlyDtoResponse> result = sourceWithParameterService.readOnly(request);
        assertNotNull(result.data);
        assertEquals("123", result.data.getId());
        assertEquals("desc", result.data.getTdsDescription());
    }
    @Test
    void testReadOnly_whenDataNull_returnsNullResponse() {
        SourceWithReadyOnlyDtoRequest request = new SourceWithReadyOnlyDtoRequest();
        request.setSourceWithParameterId("123");

        when(sourceWithParameterDao.getSourceWithParameterById("123")).thenReturn(null);

        IDataResult<SourceWithParameterReadOnlyDtoResponse> result = sourceWithParameterService.readOnly(request);
        assertNotNull(result.data);
        assertNull(result.data.getId());
    }
    @Test
    void testgetDistinctStatuses() {
       when(sourceWithParameterDao.getDistinctStatuses()).thenReturn(List.of("Active", "Inactive"));
       List<String> statuses = sourceWithParameterService.getDistinctStatuses();
        assertEquals(2, statuses.size());
        assertTrue(statuses.contains("Active"));
    }

    @Test
    void testGetDistinctOriginTypes() {
        when(sourceWithParameterDao.getDistinctOriginTypes()).thenReturn(List.of("Type1", "Type2"));
        List<String> originTypes = sourceWithParameterService.getDistinctOriginTypes();
        assertEquals(2, originTypes.size());
        assertTrue(originTypes.contains("Type1"));
    }
    @Test
    void testGetDistinctTdsOpinionDebts() {
        when(sourceWithParameterDao.getDistinctTdsOpinionDebts()).thenReturn(List.of("Debt1", "Debt2"));
        List<String> tdsOpinionDebts = sourceWithParameterService.getDistinctTdsOpinionDebts();
        assertEquals(2, tdsOpinionDebts.size());
        assertTrue(tdsOpinionDebts.contains("Debt1"));
    }
    @Test
    void testGetDistinctEffectivenessDebts(){
        when(sourceWithParameterDao.getDistinctEffectivenessDebts()).thenReturn(List.of("Effective", "Ineffective"));
        List<String> effectivenessDebts = sourceWithParameterService.getDistinctEffectivenessDebts();
        assertEquals(2, effectivenessDebts.size());
        assertTrue(effectivenessDebts.contains("Effective"));
    }
    @Test
    void testUpdateSourceWithParameter_success() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        when(sourceWithParameterDao.update(dto)).thenReturn(true);

        IDataResult<Boolean> result = sourceWithParameterService.updateSourceWithParameter(dto);

        assertTrue(result.success);
        assertTrue(result.data);
        assertEquals("Exitoso", result.message);
    }

    @Test
    void testUpdateSourceWithParameter_failure() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        when(sourceWithParameterDao.update(dto)).thenReturn(false);

        IDataResult<Boolean> result = sourceWithParameterService.updateSourceWithParameter(dto);

        assertFalse(result.success);
        assertFalse(result.data);
        assertEquals("Error", result.message);
    }

    @Test
    void testExportCommentsBySourceId_returnsComments() {
        when(sourceWithParameterDao.getCommentsBySourceIdAndType("1", "type"))
                .thenReturn(List.of("Comment1", "Comment2"));

        List<String> comments = sourceWithParameterService.exportCommentsBySourceId("1", "type");

        assertEquals(2, comments.size());
        assertTrue(comments.contains("Comment1"));
    }

    @Test
    void testExportCommentsBySourceId_emptySourceId() {
        List<String> comments = sourceWithParameterService.exportCommentsBySourceId("", "type");
        assertTrue(comments.isEmpty());
    }

    @Test
    void testSaveComment_validInput_callsDao() {
        String sourceId = "1";
        String type = "type";
        String comment = "comment";
        sourceWithParameterService.saveComment(sourceId, type, comment);
        verify(sourceWithParameterDao).saveCommentBySourceIdAndType(sourceId, type, comment);
    }



    @Test
    void testSaveComment_invalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> sourceWithParameterService.saveComment("", "type", "comment"));
    }

    @Test
    void testInsertSource_success() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        when(sourceWithParameterDao.insert(dto)).thenReturn(true);

        boolean result = sourceWithParameterService.insertSource(dto);
        assertTrue(result);
    }

    @Test
    void testInsertSource_failure() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        when(sourceWithParameterDao.insert(dto)).thenThrow(new RuntimeException("DB error"));
        boolean result = sourceWithParameterService.insertSource(dto);
        assertFalse(result);
    }


    @Test
    void testGetMaxSourceId_returnsValue() {
        when(sourceWithParameterDao.getMaxSourceId()).thenReturn("MAX123");
        String maxId = sourceWithParameterService.getMaxSourceId();
        assertEquals("MAX123", maxId);
    }

    @Test
    void testExistsReplacementId_true() {
        when(sourceWithParameterDao.existsReplacementId("R1")).thenReturn(true);
        assertTrue(sourceWithParameterService.existsReplacementId("R1"));
    }

    @Test
    void testExistsReplacementId_false() {
        when(sourceWithParameterDao.existsReplacementId("R2")).thenThrow(new RuntimeException());
        assertFalse(sourceWithParameterService.existsReplacementId("R2"));
    }

    @Test
    void testGetStatusById_returnsValue() {
        when(sourceWithParameterDao.getStatusById("1")).thenReturn("ACTIVE");
        String status = sourceWithParameterService.getStatusById("1");
        assertEquals("ACTIVE", status);
    }
    @Test
    void testSaveModifyHistory_validInput_callsDao() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        dto.setId("1");
        dto.setUserId("U1");
        dto.setUserName("User");

        sourceWithParameterService.saveModifyHistory(dto);

        verify(sourceWithParameterDao).insertModifyHistory(dto);
    }

    @Test
    void testSaveModifyHistory_nullDto_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sourceWithParameterService.saveModifyHistory(null));
    }


    @Test
    void testUpdateSourceWithParameter_throwsException_returnsErrorDataResult() {
        SourceWithParameterDataDtoResponse dto = new SourceWithParameterDataDtoResponse();
        when(sourceWithParameterDao.update(dto)).thenThrow(new RuntimeException("DB error"));

        IDataResult<Boolean> result = sourceWithParameterService.updateSourceWithParameter(dto);

        assertFalse(result.success);
        assertFalse(result.data);
        assertEquals("DB error", result.message);
    }
    @Test
    void testSaveModifyHistory_invalidFields_throwsException() {
        SourceWithParameterDataDtoResponse dto1 = new SourceWithParameterDataDtoResponse();
        dto1.setId("");
        dto1.setUserId("U1");
        dto1.setUserName("User");
        assertThrows(IllegalArgumentException.class, () -> sourceWithParameterService.saveModifyHistory(dto1));

        SourceWithParameterDataDtoResponse dto2 = new SourceWithParameterDataDtoResponse();
        dto2.setId("1");
        dto2.setUserId("");
        dto2.setUserName("User");
        assertThrows(IllegalArgumentException.class, () -> sourceWithParameterService.saveModifyHistory(dto2));

        SourceWithParameterDataDtoResponse dto3 = new SourceWithParameterDataDtoResponse();
        dto3.setId("1");
        dto3.setUserId("U1");
        dto3.setUserName("");
        assertThrows(IllegalArgumentException.class, () -> sourceWithParameterService.saveModifyHistory(dto3));
    }

}

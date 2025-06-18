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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}

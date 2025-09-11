package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.visa_sources.request.*;
import com.bbva.dto.visa_sources.response.*;
import com.bbva.service.VisaSourcesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisaSourcesResourcesTest {

    @InjectMocks
    private VisaSourcesResources resources;

    @Mock
    private VisaSourcesService visaSourcesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetVisaSources() {
        VisaSourcesPaginationDtoResponse responseDto = new VisaSourcesPaginationDtoResponse();
        responseDto.setTotalCount(10);

        IDataResult<VisaSourcesPaginationDtoResponse> expected =
                new SuccessDataResult<>(responseDto);

        when(visaSourcesService.getVisaSources(any(VisaSourcePaginationDtoRequest.class)))
                .thenReturn(expected);

        IDataResult<VisaSourcesPaginationDtoResponse> result =
                resources.getVisaSources(10, 0, 1, "Q1", "2025-01-01", "Banking", "US-123");

        assertNotNull(result);
        assertEquals(10, result.data.getTotalCount());
        verify(visaSourcesService, times(1)).getVisaSources(any(VisaSourcePaginationDtoRequest.class));
    }

    @Test
    void testRegisterVisaSource() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        dto.setId(1);
        dto.setRegisterDate("2025-09-10");
        dto.setDomain("RIC");
        dto.setFolio("CA001");

        IDataResult<Boolean> expected = new SuccessDataResult<>(true);
        when(visaSourcesService.registerVisaSource(dto)).thenReturn((SuccessDataResult<Boolean>) expected);

        IDataResult<Boolean> result = resources.registerVisaSource(dto);

        assertTrue(result.data);
        verify(visaSourcesService).registerVisaSource(dto);
    }

    @Test
    void testUpdateVisaSource() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        dto.setId(2);
        dto.setRegisterDate("2025-09-10");
        dto.setDomain("RIC");
        dto.setFolio("CA001");

        IDataResult<Boolean> expected = new SuccessDataResult<>(true);
        when(visaSourcesService.updateVisaSource(dto)).thenReturn((SuccessDataResult<Boolean>) expected);

        IDataResult<Boolean> result = resources.updateVisaSource(dto);

        assertTrue(result.data);
        verify(visaSourcesService).updateVisaSource(dto);
    }

    @Test
    void testApproveVisaSource() {
        ApproveVisaSourceDtoRequest dto = new ApproveVisaSourceDtoRequest();
        dto.setId(3);

        VisaSourceApproveDtoResponse approveResponse = new VisaSourceApproveDtoResponse();
        approveResponse.setId("3");
        approveResponse.setMessage("Approved");

        IDataResult<VisaSourceApproveDtoResponse> expected =
                new SuccessDataResult<>(approveResponse);

        when(visaSourcesService.approveVisaSource(dto)).thenReturn(expected);

        IDataResult<VisaSourceApproveDtoResponse> result = resources.approveVisaSource(dto);

        assertNotNull(result.data);
        assertEquals("Approved", result.data.getMessage());
        verify(visaSourcesService).approveVisaSource(dto);
    }

    @Test
    void testUpdateStatusVisaSource() {
        UpdateStatusVisaSourceDtoRequest dto = new UpdateStatusVisaSourceDtoRequest();
        dto.setId(4);
        dto.setStatus("ACTIVE");

        SuccessDataResult<Boolean> expected = new SuccessDataResult<>(true);
        when(visaSourcesService.updateStatusVisaSource(dto)).thenReturn(expected);

        SuccessDataResult<Boolean> result = resources.updateStatusVisaSource(dto);

        assertTrue(result.data);
        verify(visaSourcesService).updateStatusVisaSource(dto);
    }

    @Test
    void testValidateSourceIds() {
        String ids = "1,2,3";

        VisaSourceValidateExistDtoResponse validateResponse = new VisaSourceValidateExistDtoResponse();
        validateResponse.setMultipleValidation(true);
        validateResponse.setValidated(true);

        IDataResult<VisaSourceValidateExistDtoResponse> expected =
                new SuccessDataResult<>(validateResponse);

        when(visaSourcesService.validateSourceIds(ids)).thenReturn(expected);

        IDataResult<VisaSourceValidateExistDtoResponse> result = resources.validateSourceIds(ids);

        assertTrue(result.data.getValidated());
        assertTrue(result.data.getMultipleValidation());
        verify(visaSourcesService).validateSourceIds(ids);
    }
}
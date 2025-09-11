package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.VisaSourcesDao;
import com.bbva.dto.visa_sources.request.*;
import com.bbva.dto.visa_sources.response.*;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisaSourcesServiceTest {

    @Mock
    private VisaSourcesDao visaSourcesDao;

    @InjectMocks
    private VisaSourcesService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetVisaSources() {
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();
        VisaSourcesDataDtoResponse data = new VisaSourcesDataDtoResponse();
        data.setId(1);
        data.setSourceType("Database");

        when(visaSourcesDao.getVisaSources(dto)).thenReturn(List.of(data));
        when(visaSourcesDao.getVisaSourcesTotalCount(dto)).thenReturn(1);

        IDataResult<VisaSourcesPaginationDtoResponse> result = service.getVisaSources(dto);

        assertNotNull(result);
        assertEquals(1, result.data.getTotalCount());
        assertEquals("Database", result.data.getData().get(0).getSourceType());
    }

    @Test
    void testRegisterVisaSource_Success() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        InsertEntity entity = new InsertEntity();
        entity.setNew_register(1);

        when(visaSourcesDao.registerVisaSource(dto)).thenReturn(entity);

        SuccessDataResult<Boolean> result = service.registerVisaSource(dto);

        assertTrue(result.data);
        assertEquals("Estado de la Solicitud de visado registrada correctamente.", result.message);
    }

    @Test
    void testRegisterVisaSource_Failure() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        InsertEntity entity = new InsertEntity();
        entity.setNew_register(0);

        when(visaSourcesDao.registerVisaSource(dto)).thenReturn(entity);

        SuccessDataResult<Boolean> result = service.registerVisaSource(dto);

        assertFalse(result.data);
        assertEquals("Falló el registro de solicitud de visado.", result.message);
    }

    @Test
    void testUpdateVisaSource_Success() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        UpdateEntity entity = new UpdateEntity();
        entity.setUpdated_register(1);

        when(visaSourcesDao.updateVisaSource(dto)).thenReturn(entity);

        SuccessDataResult<Boolean> result = service.updateVisaSource(dto);

        assertTrue(result.data);
        assertEquals("Estado de la Solicitud de visado actualizada correctamente.", result.message);
    }

    @Test
    void testUpdateVisaSource_Failure() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        UpdateEntity entity = new UpdateEntity();
        entity.setUpdated_register(0);

        when(visaSourcesDao.updateVisaSource(dto)).thenReturn(entity);

        SuccessDataResult<Boolean> result = service.updateVisaSource(dto);

        assertFalse(result.data);
        assertEquals("Falló la actualizacion de solicitud de visado.", result.message);
    }

    @Test
    void testApproveVisaSource() {
        ApproveVisaSourceDtoRequest dto = new ApproveVisaSourceDtoRequest();
        VisaSourceApproveDtoResponse response = new VisaSourceApproveDtoResponse();
        response.setId("123");
        response.setMessage("Approved");

        when(visaSourcesDao.approveVisaSource(dto)).thenReturn(response);

        IDataResult<VisaSourceApproveDtoResponse> result = service.approveVisaSource(dto);

        assertEquals("123", result.data.getId());
        assertEquals("Approved", result.data.getMessage());
    }

    @Test
    void testValidateSourceIds_Valid() {
        VisaSourceValidateExistDtoResponse response = new VisaSourceValidateExistDtoResponse();
        response.setValidated(true);

        when(visaSourcesDao.validateSourceIds("1")).thenReturn(response);

        IDataResult<VisaSourceValidateExistDtoResponse> result = service.validateSourceIds("1");

        assertTrue(result.data.getValidated());
        assertEquals("La Fuente ID es valida.", result.message);
    }

    @Test
    void testValidateSourceIds_Invalid() {
        VisaSourceValidateExistDtoResponse response = new VisaSourceValidateExistDtoResponse();
        response.setValidated(false);

        when(visaSourcesDao.validateSourceIds("2")).thenReturn(response);

        IDataResult<VisaSourceValidateExistDtoResponse> result = service.validateSourceIds("2");

        assertFalse(result.data.getValidated());
        assertEquals("Fuente/s invalida/s.", result.message);
    }
}
package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.CatalogResponseDto;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.spp.Period;
import com.bbva.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class CatalogResourcesTest {

    private CatalogResources catalogResources;
    private CatalogService catalogServiceMock;

    @BeforeEach
    void setUp() throws Exception {
        catalogResources = new CatalogResources();
        catalogServiceMock = Mockito.mock(CatalogService.class);
        Field serviceField = CatalogResources.class.getDeclaredField("catalogService");
        serviceField.setAccessible(true);
        serviceField.set(catalogResources, catalogServiceMock);
    }

    @Test
    void testGetActivePeriod() {
        List<PeriodEntity> mockPeriods = List.of(new PeriodEntity());
        IDataResult<List<PeriodEntity>> mockResult = new IDataResult<>();
        mockResult.data = mockPeriods;
        mockResult.success = true;

        when(catalogServiceMock.getActivePeriod()).thenReturn(mockResult);

        IDataResult<List<PeriodEntity>> result = catalogResources.getActivePeriod();

        assertNotNull(result);
        assertEquals(mockPeriods, result.data);
        verify(catalogServiceMock).getActivePeriod();
    }

    @Test
    void testGetCatalog() {
        Integer catalogId = 1;
        Integer parentCatalogId = 2;
        Integer parentElementId = 3;
        CatalogResponseDto mockCatalog = new CatalogResponseDto();
        IDataResult<CatalogResponseDto> mockResult = new IDataResult<>();
        mockResult.data = mockCatalog;
        mockResult.success = true;

        when(catalogServiceMock.getCatalog(catalogId, parentCatalogId, parentElementId)).thenReturn(mockResult);

        IDataResult<CatalogResponseDto> result = catalogResources.getCatalog(parentCatalogId, parentElementId, catalogId);

        assertNotNull(result);
        assertEquals(mockCatalog, result.data);
        verify(catalogServiceMock).getCatalog(catalogId, parentCatalogId, parentElementId);
    }

    @Test
    void testCatalogsByCatalogId() {
        ListByCatalogIdDtoRequest mockRequest = new ListByCatalogIdDtoRequest();
        ListByCatalogIdDtoResponse mockResponse = new ListByCatalogIdDtoResponse();
        IDataResult<ListByCatalogIdDtoResponse> mockResult = new IDataResult<>();
        mockResult.data = mockResponse;
        mockResult.success = true;

        when(catalogServiceMock.catalogosByCatalogoId(mockRequest)).thenReturn(mockResult);

        IDataResult<ListByCatalogIdDtoResponse> result = catalogResources.catalogsByCatalogId(mockRequest);

        assertNotNull(result);
        assertEquals(mockResponse, result.data);
        verify(catalogServiceMock).catalogosByCatalogoId(mockRequest);
    }

    @Test
    void testListPeriods() throws IOException, InterruptedException {
        Period mockPeriod = new Period();
        IDataResult<Period> mockResult = new IDataResult<>();
        mockResult.data = mockPeriod;
        mockResult.success = true;

        when(catalogServiceMock.listPeriods()).thenReturn(mockResult);

        IDataResult<Period> result = catalogResources.listPeriods();

        assertNotNull(result);
        assertEquals(mockPeriod, result.data);
        verify(catalogServiceMock).listPeriods();
    }

    @Test
    void testListAllPeriods() {
        PeriodEntity mockPeriodEntity = new PeriodEntity();
        IDataResult<PeriodEntity> mockResult = new IDataResult<>();
        mockResult.data = mockPeriodEntity;
        mockResult.success = true;

        when(catalogServiceMock.listAllPeriods()).thenReturn(mockResult);

        IDataResult<PeriodEntity> result = catalogResources.listAllPeriods();

        assertNotNull(result);
        assertEquals(mockPeriodEntity, result.data);
        verify(catalogServiceMock).listAllPeriods();
    }
}

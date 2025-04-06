package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.dao.CatalogDao;
import com.bbva.dao.SppDao;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.CatalogResponseDto;
import com.bbva.dto.catalog.response.ElementsDto;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.entities.batch.GetCatalogEntity;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.spp.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogServiceTest {

    private CatalogService catalogService;
    private CatalogDao catalogDaoMock;
    private SppDao sppDaoMock;

    @BeforeEach
    void setUp() {
        catalogDaoMock = mock(CatalogDao.class);
        sppDaoMock = mock(SppDao.class);

        catalogService = new CatalogService() {
            // Inyectar mocks usando reflexión
            {
                try {
                    Field catalogDaoField = CatalogService.class.getDeclaredField("catalogDao");
                    catalogDaoField.setAccessible(true);
                    catalogDaoField.set(this, catalogDaoMock);

                    Field sppDaoField = CatalogService.class.getDeclaredField("sppDao");
                    sppDaoField.setAccessible(true);
                    sppDaoField.set(this, sppDaoMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testGetCatalogNotFound() {
        int catalogId = 1;
        int parentCatalogId = 0;
        int parentElementId = 0;

        List<GetCatalogEntity> catalogEntities = new ArrayList<>();
        when(catalogDaoMock.getCatalog(catalogId, parentCatalogId, parentElementId)).thenReturn((ArrayList<GetCatalogEntity>) catalogEntities);

        IDataResult<CatalogResponseDto> result = catalogService.getCatalog(catalogId, parentCatalogId, parentElementId);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("404", result.status);
        assertEquals("Catalog not found.", result.message);
    }

    @Test
    void testCatalogosByCatalogoId() {
        ListByCatalogIdDtoRequest dtoRequest = new ListByCatalogIdDtoRequest();
        ListByCatalogIdDtoResponse dtoResponse = new ListByCatalogIdDtoResponse();

        when(catalogDaoMock.getCatalogoByCatalogoId(dtoRequest)).thenReturn(dtoResponse);

        IDataResult<ListByCatalogIdDtoResponse> result = catalogService.catalogosByCatalogoId(dtoRequest);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(dtoResponse, result.data);
    }

    @Test
    void testGetActivePeriod() {
        List<PeriodEntity> periodEntities = new ArrayList<>();

        when(catalogDaoMock.getActivePeriod()).thenReturn(periodEntities);

        IDataResult<List<PeriodEntity>> result = catalogService.getActivePeriod();

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(periodEntities, result.data);
    }
}
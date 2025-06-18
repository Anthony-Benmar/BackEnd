package com.bbva.service;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.dto.single_base.response.SingleBaseReadOnlyDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SingleBaseServiceTest {
  private SingleBaseService singleBaseService;
  private SingleBaseDao singleBaseDao;

  @BeforeEach
  void setUp() {
      singleBaseDao = org.mockito.Mockito.mock(SingleBaseDao.class);
      singleBaseService = new SingleBaseService(singleBaseDao);
  }
  @Test
  void testGetBaseEfectivityWithSource() {
        SingleBasePaginationDtoRequest request = new SingleBasePaginationDtoRequest();
        SingleBaseDataDtoResponse singleBase = new SingleBaseDataDtoResponse();
        singleBase.setId("1");

        when(singleBaseDao.getBaseUnicaWithSource(request)).thenReturn(List.of(singleBase));
        when(singleBaseDao.getBaseUnicaTotalCount(request)).thenReturn(1);

        IDataResult<SingleBasePaginatedResponseDTO> result = singleBaseService.getBaseUnicaWithSource(request);
        assertNotNull(result);

        assertTrue(result.success);
        assertEquals(1, result.data.getTotalCount());
        assertEquals("1", result.data.getData().get(0).getId());
  }
    @Test
    void testReadOnly_whenDataExists_returnsMappedResponse() {
        SingleBaseReadOnlyDtoRequest request = new SingleBaseReadOnlyDtoRequest();
        request.setSingleBaseId("123");

        SingleBaseDataDtoResponse dbResponse = new SingleBaseDataDtoResponse();
        dbResponse.setId("123");

        when(singleBaseDao.getSingleBaseById(ArgumentMatchers.anyString())).thenReturn(dbResponse);

        IDataResult<SingleBaseReadOnlyDtoResponse> result = singleBaseService.readOnly(request);
        assertNotNull(result.data);
        assertEquals("123", result.data.getId());
    }
    @Test
    void testReadOnly_whenDataDoesNotExist_returnsNullResponse() {
        SingleBaseReadOnlyDtoRequest request = new SingleBaseReadOnlyDtoRequest();
        request.setSingleBaseId("123");

        when(singleBaseDao.getSingleBaseById(ArgumentMatchers.anyString())).thenReturn(null);

        IDataResult<SingleBaseReadOnlyDtoResponse> result = singleBaseService.readOnly(request);
        assertNotNull(result.data);
        assertNull(result.data.getId());
    }

    @Test
    void testGetDistinctFolios(){
        when(singleBaseDao.getDistinctFolios()).thenReturn(Collections.singletonList("Folio1"));
        List<String> result = singleBaseService.getDistinctFolios();
        assertEquals(1, result.size());
    }
    @Test
    void testGetDistinctProjectNames(){
        when(singleBaseDao.getDistinctProjectNames()).thenReturn(Collections.singletonList("Project1"));
        List<String> result = singleBaseService.getDistinctProjectNames();
        assertEquals(1, result.size());
    }
    @Test
    void testGetDistinctRegisteredFolioDates(){
        when(singleBaseDao.getDistinctRegisteredFolioDates()).thenReturn(Collections.singletonList(java.sql.Date.valueOf("2023-01-01")));
        List<java.sql.Date> result = singleBaseService.getDistinctRegisteredFolioDates();
        assertEquals(1, result.size());
    }
    @Test
    void testGetDistinctStatusFolioTypes(){
        when(singleBaseDao.getDistinctStatusFolioTypes()).thenReturn(Collections.singletonList("Active"));
        List<String> result = singleBaseService.getDistinctStatusFolioTypes();
        assertEquals(1, result.size());
    }
    @Test
    void testGetDistinctFolioTypes() {
        when(singleBaseDao.getDistinctFolioTypes()).thenReturn(Collections.singletonList("Type1"));
        List<String> result = singleBaseService.getDistinctFolioTypes();
        assertEquals(1, result.size());
    }
}

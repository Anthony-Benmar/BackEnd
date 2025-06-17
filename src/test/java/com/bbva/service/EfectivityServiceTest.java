package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.EfectivityBaseDao;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.request.EfectivityBaseReadOnlyDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class EfectivityServiceTest {
 private EfectivityBaseDao mockDao;
 private EfectivityBaseService service;

 @BeforeEach
 void setUp() {
  mockDao = Mockito.mock(EfectivityBaseDao.class);
  service = new EfectivityBaseService() {
  };
 }

 @Test
 void testGetBaseEfectivityWithSource() {
  when(mockDao.getBaseEfectivityWithSource(ArgumentMatchers.any(EfectivityBasePaginationDtoRequest.class)))
          .thenReturn(Collections.singletonList(new EfectivityBaseDataDtoResponse()));
  when(mockDao.getBaseEfectivityTotalCount(ArgumentMatchers.any(EfectivityBasePaginationDtoRequest.class)))
          .thenReturn(1);

  IDataResult<EfectivityBasePaginatedResponseDTO> result =
          service.getBaseEfectivityWithSource(new EfectivityBasePaginationDtoRequest());

  assertNotNull(result);
 }

 @Test
 void testReadOnly() {
  when(mockDao.getBaseEfectivityById(String.valueOf(ArgumentMatchers.anyLong())))
          .thenReturn(new EfectivityBaseDataDtoResponse());

  IDataResult<?> result =
          service.readOnly(new EfectivityBaseReadOnlyDtoRequest());

  assertNotNull(result);
 }
}

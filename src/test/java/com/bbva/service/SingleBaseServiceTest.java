package com.bbva.service;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class SingleBaseServiceTest {
  private SingleBaseService service;
  private SingleBaseDao mockDao;

  @BeforeEach
  void setUp() {
   mockDao = org.mockito.Mockito.mock(SingleBaseDao.class);
   service = new SingleBaseService() {

   };
  }
  @Test
    void testGetBaseEfectivityWithSource() {
    when(mockDao.getBaseUnicaWithSource(ArgumentMatchers.any(SingleBasePaginationDtoRequest.class)))
            .thenReturn(Collections.singletonList(new SingleBaseDataDtoResponse()));
    when(mockDao.getBaseUnicaWithSource(ArgumentMatchers.any(SingleBasePaginationDtoRequest.class)))
            .thenReturn(Collections.singletonList(new SingleBaseDataDtoResponse()));
   IDataResult<SingleBasePaginatedResponseDTO> result =
            service.getBaseUnicaWithSource(new SingleBasePaginationDtoRequest());
   assertNotNull(result);

  }
    @Test
    void testReadOnly() {
        when(mockDao.getSingleBaseById(String.valueOf(ArgumentMatchers.anyLong())))
                .thenReturn(new SingleBaseDataDtoResponse());

        IDataResult<?> result = service.readOnly(new SingleBaseReadOnlyDtoRequest());
        assertNotNull(result);
    }
}

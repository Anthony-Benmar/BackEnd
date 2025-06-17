package com.bbva.service;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.ExceptionBaseDao;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.request.ExceptionBaseReadOnlyDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import com.bbva.dto.exception_base.response.ExceptionBasePaginatedResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ExceptionServiceTest {
    @Mock
    private ExceptionBaseDao mockDao;
    @InjectMocks
    private ExceptionBaseService service;

    @BeforeEach
    void setUp() {
        mockDao = org.mockito.Mockito.mock(ExceptionBaseDao.class);
        service = new ExceptionBaseService() {
            // You can override methods here if needed for testing
        };
    }
    @Test
    void testGetExceptionWithSource() {
        when(mockDao.getExceptionsWithSource(any(ExceptionBasePaginationDtoRequest.class)))
                .thenReturn(Collections.singletonList(new ExceptionBaseDataDtoResponse()));
        when(mockDao.getExceptionsWithSource(any(ExceptionBasePaginationDtoRequest.class)))
                .thenReturn(Collections.singletonList(new ExceptionBaseDataDtoResponse()));
        IDataResult<ExceptionBasePaginatedResponseDTO> result =
                service.getExceptionsWithSource(new ExceptionBasePaginationDtoRequest());
        assertNotNull(result);
    }
    @Test
    void testReadOnly() {
        when(mockDao.getExceptionById(String.valueOf(ArgumentMatchers.anyLong())))
                .thenReturn(new ExceptionBaseDataDtoResponse());

        IDataResult<?> result = service.readOnly(new ExceptionBaseReadOnlyDtoRequest());
        assertNotNull(result);
    }
}

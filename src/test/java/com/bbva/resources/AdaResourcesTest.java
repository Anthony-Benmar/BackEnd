package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.AdaDao;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.JobExecutionFilterResponseDTO;
import com.bbva.service.AdaService;
import com.bbva.service.AdaService;
import com.bbva.util.Helper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AdaResourcesTest {
    private AdaService adaServiceMock;
    private AdaResources adaResources;

    @BeforeEach
    void setUp() throws Exception {
        adaResources = new AdaResources();
        adaServiceMock = Mockito.mock(AdaService.class);
        Field serviceField = AdaResources.class.getDeclaredField("adaService");
        serviceField.setAccessible(true);
        serviceField.set(adaResources, adaServiceMock);
    }

    @Test
    void testFilter() {
        AdaJobExecutionFilterRequestDTO request = new AdaJobExecutionFilterRequestDTO();
        request.setRecords_amount(1);
        request.setPage(1);
        when(adaServiceMock.filter(request)).thenReturn(new SuccessDataResult<>(null));
        IDataResult<AdaJobExecutionFilterResponseDTO> result = adaResources.filter(request.getRecords_amount().toString(),
                request.getPage().toString(), request.getJobName(),request.getStartDate(),
                request.getEndDate(),request.getFrequency(),request.getIsTransferred(),
                request.getJobType(),request.getServerExecution(),request.getDomain());
        assertNull(result);
    }
}
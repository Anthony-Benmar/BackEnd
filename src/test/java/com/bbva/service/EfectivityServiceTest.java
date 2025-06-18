package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.EfectivityBaseDao;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.request.EfectivityBaseReadOnlyDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataReadOnlyDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class EfectivityServiceTest {
 private EfectivityBaseDao efectivityBaseDao;
 private EfectivityBaseService efectivityBaseService;

 @BeforeEach
 void setUp() {
  efectivityBaseDao = Mockito.mock(EfectivityBaseDao.class);
  efectivityBaseService = new EfectivityBaseService(efectivityBaseDao);
 }

 @Test
 void testGetBaseEfectivityWithSource() {
  EfectivityBasePaginationDtoRequest request = new EfectivityBasePaginationDtoRequest();
    EfectivityBaseDataDtoResponse efectivity = new EfectivityBaseDataDtoResponse();
    efectivity.setId("1");
    when(efectivityBaseDao.getBaseEfectivityWithSource(request)).thenReturn(Collections.singletonList(efectivity));
    when(efectivityBaseDao.getBaseEfectivityTotalCount(request)).thenReturn(1);

    IDataResult<EfectivityBasePaginatedResponseDTO> result = efectivityBaseService.getBaseEfectivityWithSource(request);
    assertNotNull(result);
    assertEquals(1, result.data.getTotalCount());
  assertEquals("1", result.data.getData().get(0).getId());
 }

    @Test
    void testReadOnly_whenDataExists_returnsMappedResponse() {
        EfectivityBaseReadOnlyDtoRequest request = new EfectivityBaseReadOnlyDtoRequest();
        request.setEfectivityBaseId("123");

        EfectivityBaseDataDtoResponse dbResponse = new EfectivityBaseDataDtoResponse();
        dbResponse.setId("123");
        dbResponse.setTdsDescription("desc");

        when(efectivityBaseDao.getBaseEfectivityById("123"))
                .thenReturn(dbResponse);

        IDataResult<EfectivityBaseDataReadOnlyDtoResponse> result = efectivityBaseService.readOnly(request);
        assertNotNull(result);
        assertEquals("123", result.data.getId());
        assertEquals("desc", result.data.getTdsDescription());
    }
    @Test
    void testReadOnly_whenDataDoesNotExist_returnsEmptyResponse() {
        EfectivityBaseReadOnlyDtoRequest request = new EfectivityBaseReadOnlyDtoRequest();
        request.setEfectivityBaseId("123");

        when(efectivityBaseDao.getBaseEfectivityById("123"))
                .thenReturn(null);

        IDataResult<EfectivityBaseDataReadOnlyDtoResponse> result = efectivityBaseService.readOnly(request);
        assertNotNull(result);
    }
    @Test
    void testGetDistinctSdatoolProjects() {
        when(efectivityBaseDao.getDistinctSdatoolProjects()).thenReturn(Collections.singletonList("Project1"));

        List<String> projects = efectivityBaseService.getDistinctSdatoolProjects();
        assertNotNull(projects);
        assertEquals(1, projects.size());
        assertEquals("Project1", projects.get(0));
    }
    @Test
    void testGetDistinctSprintDates() {
        when(efectivityBaseDao.getDistinctSprintDates()).thenReturn(Collections.singletonList("2023-10-01"));

        List<String> sprintDates = efectivityBaseService.getDistinctSprintDates();
        assertNotNull(sprintDates);
        assertEquals(1, sprintDates.size());
        assertEquals("2023-10-01", sprintDates.get(0));
    }

    @Test
    void testGetDistinctRegistrationDates() {
        when(efectivityBaseDao.getDistinctRegisterDates()).thenReturn(Collections.<Date>singletonList(Date.valueOf("2023-10-01")));

        List<Date> registrationDates = efectivityBaseService.getDistinctRegisterDates();
        assertNotNull(registrationDates);
        assertEquals(1, registrationDates.size());
        assertEquals("2023-10-01", registrationDates.get(0).toString());
    }
    @Test
    void testGetDistinctEfficiencies() {
        when(efectivityBaseDao.getDistinctEfficiencies()).thenReturn(Collections.singletonList("High"));

        List<String> efficiencies = efectivityBaseService.getDistinctEfficiencies();
        assertNotNull(efficiencies);
        assertEquals(1, efficiencies.size());
        assertEquals("High", efficiencies.get(0));
    }

}

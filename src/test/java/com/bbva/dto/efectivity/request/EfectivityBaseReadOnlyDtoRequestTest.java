package com.bbva.dto.efectivity.request;

import com.bbva.dto.efectivity_base.request.EfectivityBaseReadOnlyDtoRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EfectivityBaseReadOnlyDtoRequestTest {
    @Test
    void gettersAndSetters(){
        EfectivityBaseReadOnlyDtoRequest request = new EfectivityBaseReadOnlyDtoRequest();
        request.setEfectivityBaseId("12345");
        assertBasicFields(request);
    }
    private void assertBasicFields(EfectivityBaseReadOnlyDtoRequest dto) {
        assertEquals("12345", dto.getEfectivityBaseId());
    }
}

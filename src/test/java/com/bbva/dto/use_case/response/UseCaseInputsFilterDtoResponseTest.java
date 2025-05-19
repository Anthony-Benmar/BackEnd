package com.bbva.dto.use_case.response;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UseCaseInputsFilterDtoResponseTest {
    @Test
    void testUseCaseInputsFilterDtoResponse() {
        UseCaseInputsFilterDtoResponse useCaseInputsFilterDtoResponse = new UseCaseInputsFilterDtoResponse();
        useCaseInputsFilterDtoResponse.setCount(10);
        useCaseInputsFilterDtoResponse.setPagesAmount(2);

        assertEquals(10, useCaseInputsFilterDtoResponse.getCount());
        assertEquals(2, useCaseInputsFilterDtoResponse.getPagesAmount());
    }
}

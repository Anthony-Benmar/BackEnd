package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.sourceWithParameter.response.SourceWithParameterDTO;
import com.bbva.service.SourceWithParameterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class SourceWithParameterResourcesTest {
    private SourceWithParameterService sourceWithParameterServiceMock;
    private SourceWithParameterResources sourceWithParameterResources;

    @BeforeEach
    void setUp() throws Exception {
        sourceWithParameterServiceMock = org.mockito.Mockito.mock(SourceWithParameterService.class);
        sourceWithParameterResources = org.mockito.Mockito.mock(SourceWithParameterResources.class);
        // Update the field name to match the actual field in SingleBaseResources
        java.lang.reflect.Field serviceField = SourceWithParameterResources.class.getDeclaredField("sourceWithParameterService");
        serviceField.setAccessible(true);
        serviceField.set(sourceWithParameterResources, sourceWithParameterServiceMock);
    }
     @Test
     void testGetBaseUnicaWithSource() {
         // Simulate the service response
         org.mockito.Mockito.when(sourceWithParameterServiceMock.getSourceWithParameter()).thenReturn(getResponseDto());
         org.mockito.Mockito.when(sourceWithParameterResources.getSourceWithParameter()).thenCallRealMethod();

         // Call the resource method
         IDataResult<List<SourceWithParameterDTO>> response = sourceWithParameterResources.getSourceWithParameter();

         // Verify that the response is as expected
         assertEquals(String.valueOf(javax.ws.rs.core.Response.Status.OK.getStatusCode()), response.status);
     }
    private IDataResult<List<SourceWithParameterDTO>> getResponseDto() {
        return new SuccessDataResult<>(List.of());
    }
}

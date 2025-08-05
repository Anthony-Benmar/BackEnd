package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RawSn2DtoResponseTest {
    @Test
    void testValueGetterSetter() {
        RawSn2DtoResponse dto = new RawSn2DtoResponse();
        assertNull(dto.getValue());

        dto.setValue(12345);
        assertEquals(12345, dto.getValue());
    }

    @Test
    void testRawDescGetterSetter() {
        RawSn2DtoResponse dto = new RawSn2DtoResponse();
        assertNull(dto.getRawDesc());

        String desc = "ABC-TEST-DESC-XYZ";
        dto.setRawDesc(desc);
        assertEquals(desc, dto.getRawDesc());
    }
}

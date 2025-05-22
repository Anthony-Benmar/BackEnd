package com.bbva.dto.drive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FolderDtoTest {

    @Test
    void testFolderDto() {
        FolderDto folder = new FolderDto();

        folder.setId("folder123");
        folder.setName("Main Folder");

        assertEquals("folder123", folder.getId());
        assertEquals("Main Folder", folder.getName());
    }
}

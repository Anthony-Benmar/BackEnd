package com.bbva.dto.drive;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FolderDtoTest {

    @Test
    void testFolderDtoAllArgsConstructor() {
        FolderDto folder = new FolderDto("123", "Test Folder");

        assertEquals("123", folder.getId());
        assertEquals("Test Folder", folder.getName());
    }

    @Test
    void testFolderDtoSettersAndGetters() {
        FolderDto folder = new FolderDto();
        folder.setId("456");
        folder.setName("Another Folder");

        assertEquals("456", folder.getId());
        assertEquals("Another Folder", folder.getName());
    }
}

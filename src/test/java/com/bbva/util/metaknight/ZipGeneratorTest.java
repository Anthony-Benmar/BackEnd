package com.bbva.util.metaknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ZipGeneratorTest {

    private ZipGenerator zipGenerator;

    @BeforeEach
    void setUp() {
        zipGenerator = new ZipGenerator();
    }

    @Test
    void testCrearZip_Success() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("archivo1.txt", "Contenido del archivo 1".getBytes(StandardCharsets.UTF_8));
        archivos.put("archivo2.conf", "config=value".getBytes(StandardCharsets.UTF_8));
        archivos.put("documento.json", "{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);
        assertTrue(zipBytes.length > 0);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertEquals(3, extractedFiles.size());
        assertEquals("Contenido del archivo 1", extractedFiles.get("archivo1.txt"));
        assertEquals("config=value", extractedFiles.get("archivo2.conf"));
        assertEquals("{\"key\":\"value\"}", extractedFiles.get("documento.json"));
    }

    @Test
    void testCrearZip_EmptyMap() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);
        assertTrue(zipBytes.length > 0);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertTrue(extractedFiles.isEmpty());
    }

    @Test
    void testCrearZip_SingleFile() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("single.txt", "Single file content".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertEquals(1, extractedFiles.size());
        assertEquals("Single file content", extractedFiles.get("single.txt"));
    }

    @Test
    void testCrearZip_BinaryFiles() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();
        byte[] binaryData = {0x00, 0x01, 0x02, (byte) 0xFF, (byte) 0xFE};
        archivos.put("binary.dat", binaryData);
        archivos.put("text.txt", "Text content".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);

        Map<String, byte[]> extractedBinaryFiles = extractZipBinaryContents(zipBytes);
        assertEquals(2, extractedBinaryFiles.size());
        assertArrayEquals(binaryData, extractedBinaryFiles.get("binary.dat"));
        assertArrayEquals("Text content".getBytes(StandardCharsets.UTF_8), extractedBinaryFiles.get("text.txt"));
    }

    @Test
    void testCrearZip_LargeFiles() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();

        byte[] largeContent = new byte[1024 * 1024];
        Arrays.fill(largeContent, (byte) 'A');
        archivos.put("large.txt", largeContent);

        archivos.put("small.txt", "small".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);
        assertTrue(zipBytes.length > 0);
        assertTrue(zipBytes.length < largeContent.length); // Should be compressed

        Map<String, byte[]> extractedFiles = extractZipBinaryContents(zipBytes);
        assertEquals(2, extractedFiles.size());
        assertArrayEquals(largeContent, extractedFiles.get("large.txt"));
        assertArrayEquals("small".getBytes(StandardCharsets.UTF_8), extractedFiles.get("small.txt"));
    }

    @Test
    void testCrearZip_FilesWithPaths() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("folder1/file1.txt", "Content 1".getBytes(StandardCharsets.UTF_8));
        archivos.put("folder1/subfolder/file2.txt", "Content 2".getBytes(StandardCharsets.UTF_8));
        archivos.put("folder2/file3.conf", "config=test".getBytes(StandardCharsets.UTF_8));
        archivos.put("root.txt", "Root content".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertEquals(4, extractedFiles.size());
        assertEquals("Content 1", extractedFiles.get("folder1/file1.txt"));
        assertEquals("Content 2", extractedFiles.get("folder1/subfolder/file2.txt"));
        assertEquals("config=test", extractedFiles.get("folder2/file3.conf"));
        assertEquals("Root content", extractedFiles.get("root.txt"));
    }

    @Test
    void testCrearZip_NullFileName() {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put(null, "content".getBytes(StandardCharsets.UTF_8));
        archivos.put("valid.txt", "valid content".getBytes(StandardCharsets.UTF_8));

        assertThrows(Exception.class, () -> {
            zipGenerator.crearZip(archivos);
        });
    }

    @Test
    void testCrearZip_NullFileContent() {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("file1.txt", null);
        archivos.put("file2.txt", "valid content".getBytes(StandardCharsets.UTF_8));

        assertThrows(Exception.class, () -> {
            zipGenerator.crearZip(archivos);
        });
    }

    @Test
    void testCrearZip_EmptyFileName() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("", "content".getBytes(StandardCharsets.UTF_8));
        archivos.put("valid.txt", "valid content".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertEquals(2, extractedFiles.size());
        assertEquals("content", extractedFiles.get(""));
        assertEquals("valid content", extractedFiles.get("valid.txt"));
    }

    @Test
    void testCrearZip_EmptyFileContent() throws Exception {
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("empty.txt", new byte[0]);
        archivos.put("nonempty.txt", "content".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertEquals(2, extractedFiles.size());
        assertEquals("", extractedFiles.get("empty.txt"));
        assertEquals("content", extractedFiles.get("nonempty.txt"));
    }

    @Test
    void testCrearZip_SpecialCharactersInFileName() throws Exception {
        // Arrange
        Map<String, byte[]> archivos = new HashMap<>();
        archivos.put("file with spaces.txt", "content1".getBytes(StandardCharsets.UTF_8));
        archivos.put("file-with-dashes.conf", "content2".getBytes(StandardCharsets.UTF_8));
        archivos.put("file_with_underscores.json", "content3".getBytes(StandardCharsets.UTF_8));
        archivos.put("file.with.dots.xml", "content4".getBytes(StandardCharsets.UTF_8));

        byte[] zipBytes = zipGenerator.crearZip(archivos);

        assertNotNull(zipBytes);

        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertEquals(4, extractedFiles.size());
        assertEquals("content1", extractedFiles.get("file with spaces.txt"));
        assertEquals("content2", extractedFiles.get("file-with-dashes.conf"));
        assertEquals("content3", extractedFiles.get("file_with_underscores.json"));
        assertEquals("content4", extractedFiles.get("file.with.dots.xml"));
    }

    private Map<String, String> extractZipContents(byte[] zipBytes) throws IOException {
        Map<String, String> contents = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    byte[] data = zis.readAllBytes();
                    contents.put(entry.getName(), new String(data, StandardCharsets.UTF_8));
                }
                zis.closeEntry();
            }
        }

        return contents;
    }

    private Map<String, byte[]> extractZipBinaryContents(byte[] zipBytes) throws IOException {
        Map<String, byte[]> contents = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    byte[] data = zis.readAllBytes();
                    contents.put(entry.getName(), data);
                }
                zis.closeEntry();
            }
        }

        return contents;
    }
}
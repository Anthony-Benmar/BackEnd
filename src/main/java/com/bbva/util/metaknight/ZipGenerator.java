package com.bbva.util.metaknight;

import com.bbva.core.HandledException;

import java.io.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class ZipGenerator {

    /**
     * Crear archivo ZIP con todos los archivos generados
     */
    public byte[] crearZip(Map<String, byte[]> archivos) throws Exception {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : archivos.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileData = entry.getValue();

                // Crear entrada en el ZIP
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);

                // Escribir datos del archivo
                zos.write(fileData);
                zos.closeEntry();
            }

            // Finalizar ZIP
            zos.finish();

            return baos.toByteArray();
        }catch (IOException e){
            throw new HandledException("ZIP_GENERATION_ERROR", "Error generando zip", e);
        }
    }
}
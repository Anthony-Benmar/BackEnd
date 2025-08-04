package com.bbva.util.metaknight;

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
        }
    }

    /**
     * Crear ZIP con estructura de carpetas
     */
    public byte[] crearZipConEstructura(Map<String, Map<String, byte[]>> estructura) throws Exception {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, Map<String, byte[]>> carpeta : estructura.entrySet()) {
                String carpetaNombre = carpeta.getKey();
                Map<String, byte[]> archivos = carpeta.getValue();

                // Crear carpeta (si no existe)
                if (!carpetaNombre.endsWith("/")) {
                    carpetaNombre += "/";
                }

                ZipEntry carpetaEntry = new ZipEntry(carpetaNombre);
                zos.putNextEntry(carpetaEntry);
                zos.closeEntry();

                // Agregar archivos a la carpeta
                for (Map.Entry<String, byte[]> archivo : archivos.entrySet()) {
                    String nombreCompleto = carpetaNombre + archivo.getKey();
                    byte[] datosArchivo = archivo.getValue();

                    ZipEntry archivoEntry = new ZipEntry(nombreCompleto);
                    zos.putNextEntry(archivoEntry);
                    zos.write(datosArchivo);
                    zos.closeEntry();
                }
            }

            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * Agregar archivo individual al ZIP
     */
    private void agregarArchivoAlZip(ZipOutputStream zos, String nombreArchivo, byte[] datos) throws IOException {
        ZipEntry entry = new ZipEntry(nombreArchivo);
        zos.putNextEntry(entry);
        zos.write(datos);
        zos.closeEntry();
    }

    /**
     * Crear carpeta en ZIP
     */
    private void crearCarpetaEnZip(ZipOutputStream zos, String nombreCarpeta) throws IOException {
        if (!nombreCarpeta.endsWith("/")) {
            nombreCarpeta += "/";
        }
        ZipEntry entry = new ZipEntry(nombreCarpeta);
        zos.putNextEntry(entry);
        zos.closeEntry();
    }
}
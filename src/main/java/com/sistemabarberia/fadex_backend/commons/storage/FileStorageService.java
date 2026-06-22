package com.sistemabarberia.fadex_backend.commons.storage;


import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class  FileStorageService {

    @Value("${file.upload-dir}") private String uploadDir;
    @Value("${app.upload.max-size}") private long maxSizeMb;
    /**
     * @param archivo El archivo a guardar
     * @param subCarpeta Carpeta destino (ej: "categorias", "documentos")
     * @param tiposPermitidos Lista de MimeTypes (ej: "image/jpeg", "application/vnd.ms-excel")
     */

    public String guardarArchivo(MultipartFile archivo, String subCarpeta, List<String> tiposPermitidos) {
        // 1. Validar vacío
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Archivo inválido o vacío", HttpStatus.BAD_REQUEST);
        }
        // 2. Validar tamaño
        long maxSize = maxSizeMb * 1024 * 1024;// 5 MB

        if (archivo.getSize() > maxSize) {
            throw new BusinessException( "El archivo excede el tamaño máximo permitido de " + maxSizeMb + " MB", HttpStatus.BAD_REQUEST);
        }

        // 3. Validar formato
        String contentType = archivo.getContentType();
        if (tiposPermitidos != null && !tiposPermitidos.contains(contentType)) {
            throw new BusinessException("Formato no permitido: " + contentType, HttpStatus.BAD_REQUEST);
        }
        // 4. Generar estructura de fecha
        LocalDate ahora = LocalDate.now();
        String fechaPath = String.format("%d/%02d", ahora.getYear(), ahora.getMonthValue());
        try {
            Path rutaDestino = Paths.get(uploadDir).resolve(subCarpeta).resolve(fechaPath);
            if (!Files.exists(rutaDestino)) {
                Files.createDirectories(rutaDestino);
            }
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nuevoNombre = UUID.randomUUID().toString() + extension;
            Path rutaCompleta = rutaDestino.resolve(nuevoNombre);
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
             return subCarpeta + "/" + fechaPath + "/" + nuevoNombre;
        } catch (IOException e) {
            log.error("Error guardando archivo", e);
            throw new BusinessException("Error al guardar archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        return (nombreArchivo != null && nombreArchivo.contains(".")) ? nombreArchivo.substring(nombreArchivo.lastIndexOf(".")) : "";
    }

    public void eliminarArchivo(String urlRelativa) {
        if (urlRelativa == null || urlRelativa.isEmpty()) {
            return;
        }
        try {
            Path rutaCompleta = Paths.get(uploadDir).resolve(urlRelativa).toAbsolutePath();
            boolean eliminado = Files.deleteIfExists(rutaCompleta);
            if (eliminado) {log.info("Borrado físico exitoso: {}", rutaCompleta);}
            else {log.warn("Archivo no encontrado en: {}", rutaCompleta);}
        } catch (IOException e) {
            log.error("Error eliminando archivo: {}", urlRelativa, e);
            throw new BusinessException("Error al eliminar archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
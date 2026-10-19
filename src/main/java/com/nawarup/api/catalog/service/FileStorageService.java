package com.nawarup.api.catalog.service;

import com.nawarup.api.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Almacenamiento local en disco. OJO: en Render (plan gratis) el disco es efímero;
 * para producción migrar a Supabase Storage / Cloudinary manteniendo esta misma interfaz.
 */
@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String dir) throws IOException {
        this.root = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public Path root() { return root; }

    public String store(MultipartFile file) {
        String type = file.getContentType() == null ? "" : file.getContentType();
        String ext = switch (type) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw ApiException.badRequest("Solo se permiten imágenes JPG, PNG o WEBP");
        };
        String name = UUID.randomUUID() + ext;
        try {
            Files.copy(file.getInputStream(), root.resolve(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return name;
    }

    public void delete(String name) {
        try {
            Files.deleteIfExists(root.resolve(name));
        } catch (IOException ignored) { }
    }
}

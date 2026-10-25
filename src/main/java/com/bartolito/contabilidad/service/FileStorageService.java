package com.bartolito.contabilidad.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String guardarArchivo(
            MultipartFile archivo,
            String carpeta,
            String nombreArchivo
    ) throws IOException {

        String extension = obtenerExtension(archivo);

        String nombreFinal =
                nombreArchivo + extension;

        String rutaRelativa =
                carpeta + "/" + nombreFinal;

        Path rutaFisica =
                Paths.get(uploadDir, rutaRelativa);

        Files.createDirectories(
                rutaFisica.getParent()
        );

        Files.copy(
                archivo.getInputStream(),
                rutaFisica,
                StandardCopyOption.REPLACE_EXISTING
        );

        return rutaRelativa;
    }

    private String obtenerExtension(
            MultipartFile archivo
    ) {

        String nombreOriginal =
                archivo.getOriginalFilename();

        if (nombreOriginal == null ||
                !nombreOriginal.contains(".")) {

            throw new IllegalArgumentException(
                    "El archivo no tiene extensión"
            );
        }

        String extension =
                nombreOriginal.substring(
                        nombreOriginal.lastIndexOf(".")
                ).toLowerCase();

        if (!extension.equals(".pdf")
                && !extension.equals(".txt")) {

            throw new IllegalArgumentException(
                    "Solo se permiten archivos PDF o TXT"
            );
        }

        return extension;
    }

}
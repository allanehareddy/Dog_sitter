package com.misha.springbootnewswagger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class ImageHandler {

    private static final Logger log = LoggerFactory.getLogger(ImageHandler.class);
    private static final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads").toString();
    private static final Path ROOT_LOCATION = Paths.get(UPLOAD_DIR);

    static {
        ensureUploadDirectoryExists(ROOT_LOCATION);
    }

    public String uploadImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            log.warn("Attempted to upload an empty or null image.");
            return null;
        }

        String originalFilename = image.getOriginalFilename();
        String extension = extractFileExtension(originalFilename);
        String imageName = UUID.randomUUID().toString() + extension;
        Path filePath = ROOT_LOCATION.resolve(imageName);

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Uploaded image: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to upload image: {}", imageName, e);
            throw new IOException("Failed to upload image: " + imageName, e);
        }

        return imageName;
    }

    public String updateImage(String oldImageName, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            log.warn("Attempted to update with an empty or null image.");
            return null;
        }

        deleteImage(oldImageName);
        return uploadImage(image);
    }

    public boolean deleteImage(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            log.warn("Attempted to delete an image with a null or empty name.");
            return false;
        }

        Path filePath = ROOT_LOCATION.resolve(imageName).normalize();
        File file = filePath.toFile();

        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("Successfully deleted image: {}", imageName);
                return true;
            } else {
                log.error("Failed to delete image: {}", imageName);
                return false;
            }
        }

        log.warn("Image file does not exist: {}", imageName);
        return false;
    }

    public String getImageAsString(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            log.warn("Attempted to fetch an image with a null or empty name.");
            return getDefaultImageUrl();
        }

        if (imageName.contains("..")) {
            log.warn("Invalid file name detected: {}", imageName);
            throw new IllegalArgumentException("Invalid file name: " + imageName);
        }

        Path filePath = ROOT_LOCATION.resolve(imageName).normalize();
        File file = filePath.toFile();

        if (file.exists() && file.isFile()) {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String imageUrl = baseUrl + "/sitters/images/" + imageName;
            log.info("Generated image URL: {}", imageUrl);
            return imageUrl;
        }

        log.warn("File not found or unreadable: {}. Serving default image.", imageName);
        return getDefaultImageUrl();
    }

    private static void ensureUploadDirectoryExists(Path rootLocation) {
        if (!Files.exists(rootLocation)) {
            try {
                Files.createDirectories(rootLocation);
                log.info("Upload directory created: {}", rootLocation);
            } catch (IOException e) {
                log.error("Failed to create upload directory: {}", rootLocation, e);
                throw new RuntimeException("Failed to create upload directory: " + rootLocation, e);
            }
        } else {
            log.debug("Upload directory already exists: {}", rootLocation);
        }

        Path userImageFilePath = rootLocation.resolve("sitter.png");
        if (!Files.exists(userImageFilePath)) {
            log.info("sitter.png not found in upload directory. Copying from resources...");

            try (InputStream inputStream = ImageHandler.class.getClassLoader().getResourceAsStream("static/sitter.png")) {
                if (inputStream == null) {
                    log.error("Default sitter.png not found in resources/static/");
                    throw new RuntimeException("Default sitter.png not found in resources/static/");
                }
                Files.copy(inputStream, userImageFilePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("sitter.png successfully copied to upload directory.");
            } catch (IOException e) {
                log.error("Failed to copy sitter.png to upload directory.", e);
                throw new RuntimeException("Failed to copy sitter.png to upload directory.", e);
            }
        } else {
            log.debug("sitter.png already exists in upload directory.");
        }
    }

    private static String extractFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        return lastIndex != -1 ? fileName.substring(lastIndex) : "";
    }

    private String getDefaultImageUrl() {
        Path defaultFilePath = ROOT_LOCATION.resolve("sitter.png").normalize();
        File defaultFile = defaultFilePath.toFile();

        if (defaultFile.exists() && defaultFile.isFile()) {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String defaultImageUrl = baseUrl + "/sitters/images/sitter.png";
            log.info("Generated default image URL: {}", defaultImageUrl);
            return defaultImageUrl;
        }

        log.error("Default image (sitter.png) not found or unreadable.");
        throw new RuntimeException("Default image (sitter.png) is missing or unreadable");
    }
}
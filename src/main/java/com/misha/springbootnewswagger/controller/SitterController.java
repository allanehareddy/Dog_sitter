package com.misha.springbootnewswagger.controller;

import com.misha.springbootnewswagger.dto.DeleteResponse;
import com.misha.springbootnewswagger.dto.LocationDto;
import com.misha.springbootnewswagger.dto.SitterDto;
import com.misha.springbootnewswagger.dto.SitterResponse;
import com.misha.springbootnewswagger.entities.SitterEntity;
import com.misha.springbootnewswagger.services.SitterService;
import com.misha.springbootnewswagger.utils.ImageHandler;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/sitters")
public class SitterController {

    private static final Logger log = LoggerFactory.getLogger(SitterController.class);

    private final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads").toString();
    private final Path rootLocation = Paths.get(UPLOAD_DIR);

    @Autowired
    private ImageHandler imageHandler;
    @Autowired
    private SitterService sitterService;


    @PostMapping("/nearby")
    public ResponseEntity<List<SitterResponse>> getSittersNearby(@RequestBody LocationDto locationDto) {
        List<SitterResponse> sitters = sitterService.getSittersWithinRadius(locationDto);
        return ResponseEntity.ok(sitters);
    }

    @PostMapping("/emailExists")
    public Boolean isEmailAlreadyExisting(@RequestParam String email) {
        return sitterService.isEmailAlreadyExisting(email);
    }

    @GetMapping("/getLoggedInUser")
    public ResponseEntity<SitterResponse> getLoggedInUser(Authentication authentication) {
        String email = authentication.getName();

        SitterResponse user = sitterService.getUser(email);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/getFilteredSitters")
    public ResponseEntity<?> getFilteredSitters(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") Integer pageNo,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "5") Integer pageSize,
            @Parameter(description = "Filter by contact name") @RequestParam(value = "contactName", required = false) String contactName,
            @Parameter(description = "Filter by company name") @RequestParam(value = "companyName", required = false) String companyName,
            @Parameter(description = "Filter by charges") @RequestParam(value = "charges", required = false) Double charges,
            @Parameter(description = "Sort by field") @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort order") @RequestParam(value = "order", defaultValue = "desc", required = false) String order) {

        try {
            Page<SitterResponse> page = sitterService.getFilteredSitters(pageNo, pageSize, contactName, companyName, charges, sortBy, order);
            return ResponseEntity.ok(page);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching filtered sitters: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("An unexpected error occurred. Please try again later.");
        }
    }

    @PostMapping
    public ResponseEntity<SitterResponse> createNewSitter(
            @ModelAttribute("sitterDto") @Valid SitterDto sitterDto,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        log.info("Creating a new sitter with details: {}", sitterDto);

        try {
            String imageName = null;
            if (image != null && !image.isEmpty()) {
                imageName = imageHandler.uploadImage(image);
                log.info("Successfully uploaded this {} image", imageName);
            } else {
                imageName = "sitter.png";
            }

            SitterResponse savedSitter= sitterService.createNewSitter(sitterDto, imageName);

            log.info("Sitter created successfully with ID: {}", savedSitter.getId());
            return new ResponseEntity<>(savedSitter, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error creating sitter: {}", e.getMessage());

            if (image != null && !image.isEmpty()) {
                try {
                    imageHandler.deleteImage(image.getOriginalFilename());
                } catch (Exception deleteException) {
                    log.error("Failed to clean up uploaded image: {}", deleteException.getMessage());
                }
            }

            throw new RuntimeException("Failed to create sitter", e);
        }
    }

    @PutMapping(path = "/{sitterId}")
    public ResponseEntity<SitterResponse> updateSitterById(@ModelAttribute @Valid SitterDto sitterDto,
                                                           @PathVariable Long sitterId,
                                                           @RequestParam(value = "image", required = false) MultipartFile image) {
        log.info("Updating sitter with ID: {}. Updated details: {}", sitterId, sitterDto);
        try {
            SitterResponse updateSitter = sitterService.updateSitterById(sitterDto, sitterId, image);
            log.info("Sitter with ID {} updated successfully", sitterId);
            return ResponseEntity.ok(updateSitter);
        } catch (Exception e) {
            log.error("Error updating sitter with ID {}: {}", sitterId, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping(path = "/{sitterId}")
    public ResponseEntity<DeleteResponse> deleteUserById(@PathVariable Long sitterId) {
        log.info("Deleting sitter with ID: {}", sitterId);
        try {
            boolean gotDeleted = sitterService.deleteSitterById(sitterId);
            if (gotDeleted) {
                log.info("Sitter with ID {} deleted successfully", sitterId);
                return ResponseEntity.ok(new DeleteResponse(true, "User deleted successfully"));
            } else {
                log.warn("Sitter with ID {} not found for deletion", sitterId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting sitter with ID {}: {}", sitterId, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/images/{imageName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            log.info("Fetching image with file name: {}", imageName);

            if (imageName.contains("..")) {
                log.warn("Invalid file name detected: {}", imageName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Path filePath = rootLocation.resolve(imageName).normalize();
            log.debug("Resolved file path: {}", filePath);

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                log.debug("File content type: {}", contentType);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                log.warn("File not found or unreadable: {}. Serving default image.", imageName);

                Path defaultFilePath = rootLocation.resolve("sitter.png").normalize();
                Resource defaultResource = new UrlResource(defaultFilePath.toUri());

                if (defaultResource.exists() && defaultResource.isReadable()) {
                    String defaultContentType = Files.probeContentType(defaultFilePath);
                    if (defaultContentType == null) {
                        defaultContentType = "application/octet-stream";
                    }
                    log.debug("Serving default image with content type: {}", defaultContentType);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"sitter.png\"")
                            .contentType(MediaType.parseMediaType(defaultContentType))
                            .body(defaultResource);
                } else {
                    log.error("Default image (sitter.png) not found or unreadable.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }

        } catch (MalformedURLException e) {
            log.error("Malformed URL error while fetching file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            log.error("IO error while fetching file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
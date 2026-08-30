package com.ner.logistics.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/evidence/";
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File size exceeds 10MB limit.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ResponseEntity.badRequest().body("Invalid file extension. Only JPG, PNG, and WEBP images are allowed.");
        }

        // Server-Side Magic Byte Signature Validation (Prevents malware disguised as .jpg)
        try (InputStream is = file.getInputStream()) {
            byte[] headerBytes = new byte[12];
            int read = is.read(headerBytes);
            if (read < 4 || !isValidImageMagicBytes(headerBytes)) {
                log.warn("⚠️ SECURITY REJECTION: File {} failed server-side magic byte signature validation.", originalFilename);
                return ResponseEntity.badRequest().body("Security Violation: File header magic bytes do not match a valid JPEG, PNG, or WEBP image format.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not read file header bytes.");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_") : "evidence.jpg");
            Path targetPath = uploadPath.resolve(uniqueFileName);

            // Calculate SHA-256 Hash while copying file
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream dis = new DigestInputStream(file.getInputStream(), md)) {
                Files.copy(dis, targetPath);
            }
            String sha256Hash = HexFormat.of().formatHex(md.digest());

            String fileUrl = "/uploads/evidence/" + uniqueFileName;
            String username = authentication != null ? authentication.getName() : "FIELD_OFFICER";
            log.info("📸 Photo Evidence Uploaded: {} (SHA-256: {}) by user {}", fileUrl, sha256Hash, username);

            FileUploadResponseDto response = FileUploadResponseDto.builder()
                    .fileUrl(fileUrl)
                    .fileName(uniqueFileName)
                    .fileType(file.getContentType())
                    .sizeBytes(file.getSize())
                    .sha256Hash(sha256Hash)
                    .uploadedBy(username)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("💥 Failed to store uploaded file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not store file: " + e.getMessage());
        }
    }

    private boolean isValidImageMagicBytes(byte[] bytes) {
        // JPEG magic bytes: FF D8
        boolean isJpeg = (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8;
        // PNG magic bytes: 89 50 4E 47 (.PNG)
        boolean isPng = (bytes[0] & 0xFF) == 0x89 && (bytes[1] & 0xFF) == 0x50 && (bytes[2] & 0xFF) == 0x4E && (bytes[3] & 0xFF) == 0x47;
        // WEBP magic bytes: RIFF...WEBP (52 49 46 46 ... 57 45 42 50)
        boolean isWebp = (bytes[0] & 0xFF) == 0x52 && (bytes[1] & 0xFF) == 0x49 && (bytes[2] & 0xFF) == 0x46 && (bytes[3] & 0xFF) == 0x46;

        return isJpeg || isPng || isWebp;
    }
}

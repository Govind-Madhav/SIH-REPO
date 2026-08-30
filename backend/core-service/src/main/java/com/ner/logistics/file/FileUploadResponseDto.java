package com.ner.logistics.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDto {
    private String fileUrl;
    private String fileName;
    private String fileType;
    private long sizeBytes;
    private LocalDateTime uploadedAt;
}

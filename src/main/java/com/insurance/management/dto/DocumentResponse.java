package com.insurance.management.dto;

import com.insurance.management.entity.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class DocumentResponse {
    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private DocumentType documentType;
    private Long policyId;
    private Long claimId;
    private String uploadedByEmail;
    private LocalDate uploadedOn;
}
package com.insurance.management.controller;

import com.insurance.management.dto.DocumentResponse;
import com.insurance.management.entity.Document;
import com.insurance.management.entity.DocumentType;
import com.insurance.management.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "policyId", required = false) Long policyId,
            @RequestParam(value = "claimId", required = false) Long claimId,
            Authentication auth) {
        return ResponseEntity.ok(documentService.upload(file, documentType, policyId, claimId, auth.getName()));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Document document = documentService.getDocumentEntity(id);
        Resource resource = documentService.loadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/me")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(Authentication auth) {
        return ResponseEntity.ok(documentService.getMyDocuments(auth.getName()));
    }

    @GetMapping("/policy/{policyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<DocumentResponse>> getByPolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(documentService.getByPolicyId(policyId));
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<DocumentResponse>> getByClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(documentService.getByClaimId(claimId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
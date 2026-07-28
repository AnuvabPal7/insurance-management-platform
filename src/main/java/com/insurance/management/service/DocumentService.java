package com.insurance.management.service;

import com.insurance.management.dto.DocumentResponse;
import com.insurance.management.entity.*;
import com.insurance.management.exception.ApiException;
import com.insurance.management.repository.ClaimRepository;
import com.insurance.management.repository.DocumentRepository;
import com.insurance.management.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public DocumentResponse upload(MultipartFile file, DocumentType documentType,
                                   Long policyId, Long claimId, String userEmail) {
        if (file.isEmpty()) {
            throw new ApiException("File is empty", HttpStatus.BAD_REQUEST);
        }

        Policy policy = null;
        if (policyId != null) {
            policy = policyRepository.findById(policyId)
                    .orElseThrow(() -> new ApiException("Policy not found", HttpStatus.NOT_FOUND));
        }

        Claim claim = null;
        if (claimId != null) {
            claim = claimRepository.findById(claimId)
                    .orElseThrow(() -> new ApiException("Claim not found", HttpStatus.NOT_FOUND));
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null && originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";
            String storedFileName = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetPath);

            Document document = Document.builder()
                    .fileName(originalFileName)
                    .storedFileName(storedFileName)
                    .filePath(targetPath.toString())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .documentType(documentType)
                    .policy(policy)
                    .claim(claim)
                    .uploadedByEmail(userEmail)
                    .build();

            Document saved = documentRepository.save(document);
            return toResponse(saved);

        } catch (IOException e) {
            throw new ApiException("Failed to store file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Resource loadFile(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ApiException("Document not found", HttpStatus.NOT_FOUND));

        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ApiException("File not found on disk", HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new ApiException("File path error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Document getDocumentEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Document not found", HttpStatus.NOT_FOUND));
    }

    public List<DocumentResponse> getByPolicyId(Long policyId) {
        return documentRepository.findByPolicyId(policyId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DocumentResponse> getByClaimId(Long claimId) {
        return documentRepository.findByClaimId(claimId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DocumentResponse> getMyDocuments(String userEmail) {
        return documentRepository.findByUploadedByEmail(userEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Document not found", HttpStatus.NOT_FOUND));

        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            throw new ApiException("Failed to delete file from disk", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        documentRepository.delete(document);
    }

    private DocumentResponse toResponse(Document d) {
        return DocumentResponse.builder()
                .id(d.getId())
                .fileName(d.getFileName())
                .contentType(d.getContentType())
                .fileSize(d.getFileSize())
                .documentType(d.getDocumentType())
                .policyId(d.getPolicy() != null ? d.getPolicy().getId() : null)
                .claimId(d.getClaim() != null ? d.getClaim().getId() : null)
                .uploadedByEmail(d.getUploadedByEmail())
                .uploadedOn(d.getUploadedOn())
                .build();
    }
}
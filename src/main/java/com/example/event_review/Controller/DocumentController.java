package com.example.event_review.Controller;

import com.example.event_review.DTO.DocumentDTO;
import com.example.event_review.Entity.ProposalDocument;
import com.example.event_review.Repo.ProposalDocumentRepo;
import com.example.event_review.Service.DocumentStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentStorageService documentStorageService;

    @Autowired
    private ProposalDocumentRepo proposalDocumentRepo;

    /**
     * Upload a single file to a given proposal.
     * Example front-end usage:
     *   POST /api/documents/upload?proposalId=123&userId=456
     *   formData: { file: <the file> }
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam Long proposalId,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ProposalDocument savedDoc = documentStorageService.uploadFile(proposalId, userId, file);
            return ResponseEntity.ok(savedDoc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * List all documents for a given proposal.
     * GET /api/documents/proposal/123
     */
   @GetMapping("/proposal/{proposalId}")
public ResponseEntity<List<DocumentDTO>> getDocumentsByProposal(@PathVariable Long proposalId) {
    try {
        List<ProposalDocument> docs = documentStorageService.getDocumentsForProposal(proposalId);
        
        List<DocumentDTO> dtos = docs.stream()
            .map(doc -> {
                DocumentDTO dto = new DocumentDTO();
                dto.setId(doc.getId());
                dto.setFileName(doc.getFileName());
                dto.setFileType(doc.getFileType());
                dto.setFileSize(doc.getFileSize());
                dto.setUploadedBy(doc.getUploadedBy());
                dto.setUploadedAt(doc.getUploadedAt());
                // or doc.getProposal() might be null if you like
                dto.setProposalId(doc.getProposal().getProposalId());
                return dto;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    } catch (Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
    }
}


    /**
     * Generate a pre-signed URL so the caller can download the file from S3.
     * E.g. GET /api/documents/99/download
     */
    @GetMapping("/{docId}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable Long docId) {
        try {
            // 1) Find the doc
            ProposalDocument doc = proposalDocumentRepo.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Document not found with id: " + docId));

            // 2) Get a pre-signed URL from the service
            String presignedUrl = documentStorageService.generatePresignedUrl(doc.getS3Key());

            // Return the URL, or a small JSON with the URL
            return ResponseEntity.ok(presignedUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error generating download URL: " + e.getMessage());
        }
    }

    /**
     * Delete a document from S3 and from the DB.
     * DELETE /api/documents/99
     */
    @DeleteMapping("/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long docId) {
        try {
            documentStorageService.deleteDocument(docId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting document: " + e.getMessage());
        }
    }
}

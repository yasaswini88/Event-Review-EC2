package com.example.event_review.Service;

import com.example.event_review.Entity.Proposal;
import com.example.event_review.Entity.ProposalDocument;
import com.example.event_review.Repo.ProposalDocumentRepo;
import com.example.event_review.Repo.ProposalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentStorageService {

    @Autowired
    private S3Client s3Client;

    @Value("${app.s3.bucketName}")
    private String bucketName;

    @Autowired
    private ProposalDocumentRepo proposalDocumentRepo;

    @Autowired
    private ProposalRepo proposalRepo;

    public ProposalDocument uploadFile(Long proposalId, Long userId, MultipartFile file) {
        Proposal proposal = proposalRepo.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));

        String uniqueS3Key = "proposals/" + proposalId + "/"
                + System.currentTimeMillis() + "-" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueS3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
            );

            ProposalDocument doc = new ProposalDocument();
            doc.setProposal(proposal);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileType(file.getContentType());
            doc.setS3Key(uniqueS3Key);
            doc.setFileSize(file.getSize());
            doc.setUploadedBy(userId);
            doc.setUploadedAt(LocalDateTime.now());

            return proposalDocumentRepo.save(doc);

        } catch (IOException e) {
            throw new RuntimeException("Error reading file content: " + e.getMessage(), e);
        }
    }

    public List<ProposalDocument> getDocumentsForProposal(Long proposalId) {
        return proposalDocumentRepo.findByProposal_ProposalId(proposalId);
    }

    /**
     * Generate a short-lived pre-signed URL so a client can download the file directly from S3.
     */
    public String generatePresignedUrl(String s3Key) {
        // For pre-signed URLs, we typically use the S3Presigner (v2).
        // You can autowire a S3Presigner bean or create it on the fly:
        S3Presigner presigner = S3Presigner.create();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        // Expiration time for the pre-signed URL (e.g. 5 minutes)
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest =
                presigner.presignGetObject(getObjectPresignRequest);

        return presignedRequest.url().toString();
    }

    /**
     * Delete from S3 and remove the record from the DB.
     */
    public void deleteDocument(Long docId) {
        ProposalDocument doc = proposalDocumentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + docId));

        // 1) Delete from S3
        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(doc.getS3Key())
                .build();

        s3Client.deleteObject(deleteReq);

        // 2) Delete from DB
        proposalDocumentRepo.delete(doc);
    }
}

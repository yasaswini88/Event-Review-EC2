package com.example.event_review.Controller;

import com.example.event_review.DTO.ApprovalHistoryDTO;
import com.example.event_review.DTO.FacultyStatsDTO;
import com.example.event_review.DTO.HistoryLogsResponse;
import com.example.event_review.DTO.ProposalDTO;
import com.example.event_review.Service.ApprovalHistoryService;
import com.example.event_review.Service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proposals")
@CrossOrigin(origins = "*")
public class ProposalController {
    @Autowired
    private ProposalService proposalService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @GetMapping
    public List<ProposalDTO> getAllProposals() {
        return proposalService.getAllProposals();
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<ProposalDTO> getProposalById(@PathVariable Long id) {
    // return proposalService.getProposalById(id)
    // .map(ResponseEntity::ok)
    // .orElse(ResponseEntity.notFound().build());
    // }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDTO> getProposalById(
            @PathVariable Long id,
            @RequestParam Long currentUserId // <--- NEW: which user is requesting?
    ) {
        // Now we want to do two things:
        // 1) Retrieve the proposal by "id"
        // 2) Check if "currentUserId" is authorized to view it.

        Optional<ProposalDTO> proposalOpt = proposalService.getProposalById(id);
        if (!proposalOpt.isPresent()) {
            // If the proposal doesn't exist, return 404
            return ResponseEntity.notFound().build();
        }

        ProposalDTO proposalDTO = proposalOpt.get();

        // 3) We must verify that currentUserId can see this proposal.
        // We'll write a small check here or call a separate method in the service.

        boolean authorized = proposalService.isUserAuthorizedToViewProposal(
                proposalDTO, currentUserId);

        if (!authorized) {
            // user is not the right approver, not the requestor, and not an admin
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        // If authorized, return the proposal
        return ResponseEntity.ok(proposalDTO);
    }

    @GetMapping("/user/{userId}")
    public List<ProposalDTO> getProposalsByUserId(@PathVariable Long userId) {
        return proposalService.getProposalsByUserId(userId);
    }

    @GetMapping("/approver/{approverId}")
    public List<ProposalDTO> getProposalsByApproverId(@PathVariable Long approverId) {
        return proposalService.getProposalsByApproverId(approverId);
    }

    @GetMapping("/status/{status}")
    public List<ProposalDTO> getProposalsByStatus(@PathVariable String status) {
        return proposalService.getProposalsByStatus(status);
    }

    @PostMapping
    public ResponseEntity<?> addProposal(@RequestBody ProposalDTO proposalDTO) {
        if (proposalDTO.getUserId() == null || proposalDTO.getDepartmentId() == null) {
            return ResponseEntity.badRequest()
                    .body("User ID and Department ID are required");
        }

        ProposalDTO createdProposal = proposalService.addProposal(proposalDTO);
        if (createdProposal == null) {
            return ResponseEntity.badRequest()
                    .body("Failed to create proposal. Please check all required fields.");
        }

        return ResponseEntity.ok(createdProposal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalDTO> updateProposal(
            @PathVariable Long id,
            @RequestBody ProposalDTO proposalDTO) {
        ProposalDTO updatedProposal = proposalService.updateProposal(id, proposalDTO);
        return updatedProposal != null ? ResponseEntity.ok(updatedProposal) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposal(@PathVariable Long id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ProposalDTO> updateProposalStatus(
            @PathVariable Long id,
            @RequestParam String newStatus,
            @RequestParam Long approverId,
            @RequestParam Long fundingSourceId,
            @RequestParam(required = false) String comments) {
        ProposalDTO updatedProposal = proposalService.updateProposalStatus(id, newStatus, approverId, fundingSourceId,
                comments);
        return updatedProposal != null ? ResponseEntity.ok(updatedProposal) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ApprovalHistoryDTO>> getProposalHistory(@PathVariable Long id) {
        List<ApprovalHistoryDTO> history = approvalHistoryService.getHistoryByProposalId(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProposalDTO>> searchProposals(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        List<ProposalDTO> proposals = proposalService.searchProposals(userId, status, startDate, endDate);
        return ResponseEntity.ok(proposals);
    }

    @GetMapping("/approver/{approverId}/status/{status}")
    public List<ProposalDTO> getProposalsByApproverIdAndStatus(
            @PathVariable Long approverId,
            @PathVariable String status) {
        return proposalService.getProposalsByApproverAndStatus(approverId, status);
    }

    @GetMapping("/faculty/{facultyId}/status/{status}")
    public List<ProposalDTO> getProposalsByFacultyIdAndStatus(
            @PathVariable Long facultyId,
            @PathVariable String status) {
        // Call the Service method
        return proposalService.getProposalsByFacultyIdAndStatus(facultyId, status);
    }

    @PutMapping("/{id}/comment")
    public ResponseEntity<ProposalDTO> addComment(
            @PathVariable Long id,
            @RequestParam Long currentUserId,
            @RequestParam(required = false) Long fundingSourceId,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) String actionDate) {
        // 1) Call the service
        ProposalDTO updatedProposal = proposalService.addComment(
                id, currentUserId, fundingSourceId, comments, actionDate);

        // 2) Decide how to handle null:
        if (updatedProposal == null) {
            // If you want 404 if proposal doesn't exist => you can detect that specifically
            // Otherwise, 403 is your fallback for "not found or not allowed".
            return ResponseEntity.status(404).build();
        }

        // 3) Otherwise, return the updated proposal
        return ResponseEntity.ok(updatedProposal);
    }

    @GetMapping("/faculty-stats/{facultyId}")
    public FacultyStatsDTO getFacultyStats(@PathVariable Long facultyId) {
        // Suppose we define the timeframe as the last 1 year
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);

        return proposalService.getFacultyStatsForUser(facultyId, oneYearAgo, now);
    }

    @GetMapping("/history-logs/faculty/{facultyId}")
    public ResponseEntity<HistoryLogsResponse> getHistoryLogsByFaculty(@PathVariable Long facultyId) {
        // Step 1: call the new service method
        HistoryLogsResponse result = proposalService.getHistoryLogsByFaculty(facultyId);

        // Step 2: return the JSON
        return ResponseEntity.ok(result);
    }

}

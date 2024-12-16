package com.example.event_review.Controller;

import com.example.event_review.DTO.ApprovalHistoryDTO;
import com.example.event_review.DTO.ProposalDTO;
import com.example.event_review.Service.ApprovalHistoryService;
import com.example.event_review.Service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDTO> getProposalById(@PathVariable Long id) {
        return proposalService.getProposalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        return updatedProposal != null ? 
                ResponseEntity.ok(updatedProposal) : 
                ResponseEntity.notFound().build();
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
    ProposalDTO updatedProposal = proposalService.updateProposalStatus(id, newStatus, approverId, fundingSourceId, comments);
    return updatedProposal != null ?
            ResponseEntity.ok(updatedProposal) :
            ResponseEntity.notFound().build();
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
}

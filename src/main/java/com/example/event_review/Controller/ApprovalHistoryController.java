package com.example.event_review.Controller;

import com.example.event_review.DTO.ApprovalHistoryDTO;
import com.example.event_review.Service.ApprovalHistoryService;

// import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@RestController
@RequestMapping("/api/approval-history")
@CrossOrigin(origins = "*")
public class ApprovalHistoryController {
    private static final Logger logger = LoggerFactory.getLogger(ApprovalHistoryController.class);
    @Autowired
    private ApprovalHistoryService historyService;

    @GetMapping("/proposal/{proposalId}")
    public ResponseEntity<?> getHistoryByProposalId(@PathVariable Long proposalId) {
        try {
            List<ApprovalHistoryDTO> history = historyService.getHistoryByProposalId(proposalId);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching approval history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).body("An unexpected error occurred");

        }
    }
    
}


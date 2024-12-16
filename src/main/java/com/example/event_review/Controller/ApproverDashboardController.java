// package com.example.event_review.Controller;

// import com.example.event_review.DTO.ProposalDTO;
// import com.example.event_review.DTO.ProposalFeedbackDTO;
// import com.example.event_review.Service.ApproverDashboardService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import java.util.List;

// @RestController
// @RequestMapping("/api/approver")
// @CrossOrigin
// public class ApproverDashboardController {

//     @Autowired
//     private ApproverDashboardService approverService;

//     @GetMapping("/{approverId}/proposals")
//     public List<ProposalDTO> getApproverProposals(@PathVariable Long approverId) {
//         return approverService.getProposalsForApprover(approverId);
//     }

//     @PostMapping("/proposals/{proposalId}/review")
//     public ResponseEntity<ProposalFeedbackDTO> reviewProposal(
//             @PathVariable Long proposalId,
//             @RequestBody ProposalFeedbackDTO feedbackDTO) {
//         ProposalFeedbackDTO savedFeedback = approverService.reviewProposal(proposalId, feedbackDTO);
//         return savedFeedback != null ? 
//                 ResponseEntity.ok(savedFeedback) : 
//                 ResponseEntity.badRequest().build();
//     }

//     @GetMapping("/proposals/{proposalId}/feedback")
//     public List<ProposalFeedbackDTO> getFeedbackHistory(@PathVariable Long proposalId) {
//         return approverService.getFeedbackHistory(proposalId);
//     }
// }
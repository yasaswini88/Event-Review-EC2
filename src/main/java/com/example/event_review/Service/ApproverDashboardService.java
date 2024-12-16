// package com.example.event_review.Service;

// import com.example.event_review.DTO.ProposalDTO;
// import com.example.event_review.DTO.ProposalFeedbackDTO;
// import com.example.event_review.Entity.Proposal;
// import com.example.event_review.Entity.ProposalFeedback;
// import com.example.event_review.Repo.ProposalRepo;
// import com.example.event_review.Repo.ProposalFeedbackRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class ApproverDashboardService {
    
//     @Autowired
//     private ProposalRepo proposalRepo;
    
//     @Autowired
//     private ProposalFeedbackRepo feedbackRepo;

//     public List<ProposalDTO> getProposalsForApprover(Long approverId) {
//         return proposalRepo.findByUser_UserId(approverId).stream()
//                 .map(this::convertToDTO)
//                 .collect(Collectors.toList());
//     }

//     public ProposalFeedbackDTO reviewProposal(Long proposalId, ProposalFeedbackDTO feedbackDTO) {
//         Optional<Proposal> proposal = proposalRepo.findById(proposalId);
        
//         if (proposal.isPresent()) {
//             ProposalFeedback feedback = new ProposalFeedback();
//             feedback.setProposal(proposal.get());
//             feedback.setComment(feedbackDTO.getComment());
//             feedback.setStatus(feedbackDTO.getStatus());
//             feedback.setFeedbackDate(LocalDateTime.now());
            
//             // Update proposal status
//             Proposal existingProposal = proposal.get();
//             existingProposal.setStatus(feedbackDTO.getStatus());
//             proposalRepo.save(existingProposal);
            
//             ProposalFeedback savedFeedback = feedbackRepo.save(feedback);
//             return convertFeedbackToDTO(savedFeedback);
//         }
//         return null;
//     }

//     public List<ProposalFeedbackDTO> getFeedbackHistory(Long proposalId) {
//         return feedbackRepo.findByProposal_ProposalId(proposalId).stream()
//                 .map(this::convertFeedbackToDTO)
//                 .collect(Collectors.toList());
//     }

//     private ProposalDTO convertToDTO(Proposal proposal) {
//         ProposalDTO dto = new ProposalDTO();
//         dto.setProposalId(proposal.getProposalId());
//         dto.setEventId(proposal.getEvent().getEventId());
//         dto.setUserId(proposal.getUser().getUserId());
//         dto.setCatererName(proposal.getCatererName());
//         dto.setFoodMenu(proposal.getFoodMenu());
//         dto.setEstimatedCost(proposal.getEstimatedCost());
//         dto.setStatus(proposal.getStatus());
//         dto.setProposalDate(proposal.getProposalDate());
//         return dto;
//     }

//     private ProposalFeedbackDTO convertFeedbackToDTO(ProposalFeedback feedback) {
//         ProposalFeedbackDTO dto = new ProposalFeedbackDTO();
//         dto.setFeedbackId(feedback.getFeedbackId());
//         dto.setProposalId(feedback.getProposal().getProposalId());
//         dto.setComment(feedback.getComment());
//         dto.setStatus(feedback.getStatus());
//         dto.setFeedbackDate(feedback.getFeedbackDate());
//         return dto;
//     }
// }

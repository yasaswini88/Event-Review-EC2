package com.example.event_review.Service;

import com.example.event_review.DTO.FacultyStatsDTO;
import com.example.event_review.DTO.HistoryLogsResponse;
import com.example.event_review.DTO.MonthlyStatsDTO;
import com.example.event_review.DTO.ProposalDTO;
import com.example.event_review.DTO.YearlyStatsDTO;
import com.example.event_review.Entity.Department;
import com.example.event_review.Entity.FundingSource;
import com.example.event_review.Entity.Proposal;
import com.example.event_review.Entity.ProposalHistory;
import com.example.event_review.Entity.PurchaseOrder;
import com.example.event_review.Entity.User;
import com.example.event_review.Repo.DepartmentRepo;
import com.example.event_review.Repo.FundingSourceRepo;
import com.example.event_review.Repo.ProposalHistoryRepo;
import com.example.event_review.Repo.ProposalRepo;
import com.example.event_review.Repo.PurchaseOrderRepo;
import com.example.event_review.Repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.Objects;

@Service
public class ProposalService {
    private static final Logger logger = LoggerFactory.getLogger(ProposalService.class);
    // This is used to log information, warnings, and errors. It's useful for
    // debugging and keeping track of application flow.

    @Autowired
    private ProposalRepo proposalRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;

    @Autowired
    private FundingSourceRepo fundingSourceRepo;

    @Autowired
    private ApproverBudgetService approverBudgetService;

    @Autowired
private ProposalHistoryRepo proposalHistoryRepo;


    public List<ProposalDTO> getAllProposals() {
        try {
            return proposalRepo.findAll().stream() // findAll() method is used to get all proposals from the database.
                    .map(this::convertToDTO) // map() method is used to convert each Proposal entity to a ProposalDTO
                                             // object.
                    .collect(Collectors.toList()); // collect() method is used to collect the converted ProposalDTO
                                                   // objects into a list.
        } catch (Exception e) {
            logger.error("Error getting all proposals: ", e);
            return null;
        }
    }

    public Optional<ProposalDTO> getProposalById(Long id) { // This method is used to get a proposal by its ID.
        try {
            return proposalRepo.findById(id) // findById() method is used to get a proposal by its ID.
                    .map(this::convertToDTO); // map() method is used to convert the Proposal entity to a ProposalDTO
                                              // object.
        } catch (Exception e) {
            logger.error("Error getting proposal with id {}: ", id, e);
            return Optional.empty();
        }
    }

    public List<ProposalDTO> getProposalsByUserId(Long userId) {
        try {
            return proposalRepo.findByUser_UserId(userId).stream() // findByUser_UserId() method is used to get all
                                                                   // proposals associated with a specific user.
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting proposals for user id {}: ", userId, e);
            return null;
        }
    }

    public boolean isUserAuthorizedToViewProposal(ProposalDTO proposalDTO, Long currentUserId) {
        if (currentUserId == null) {
            return false; // no user => not authorized
        }

        Optional<User> userOpt = userRepo.findById(currentUserId);
        if (!userOpt.isPresent()) {
            return false;
        }
        User user = userOpt.get();

        if (user.getRoles() == null) {
            return false;
        }

    //     Long roleId = user.getRoles().getRoleId();

    //     switch (roleId.intValue()) {
    //         case 1: // Admin => can see all
    //             // case 4: // Purchaser => can see all
    //             return true;
                

    //         case 4: // Purchaser => can only see if proposal is approved
    //             return "APPROVED".equalsIgnoreCase(proposalDTO.getStatus());

    //         case 2: // Faculty => can only see proposals they created
    //             return proposalDTO.getUserId().equals(currentUserId);

    //         // case 2: // Faculty => can now comment on ANY proposal
    //         // return true;

    //         case 3: // Approver => can only see proposals where they are the currentApprover
    //             return proposalDTO.getCurrentApproverId() != null
    //                     && proposalDTO.getCurrentApproverId().equals(currentUserId);

    //         default:
    //             return false;
    //     }
    // }

    boolean isAdmin     = user.getRoles().stream().anyMatch(r -> r.getRoleId() == 1);
boolean isPurchaser = user.getRoles().stream().anyMatch(r -> r.getRoleId() == 4);
boolean isFaculty   = user.getRoles().stream().anyMatch(r -> r.getRoleId() == 2);
boolean isApprover  = user.getRoles().stream().anyMatch(r -> r.getRoleId() == 3);

if (isAdmin) {
    // Admin => can see all
    return true;
} else if (isPurchaser) {
    // Purchaser => can only see if proposal is approved
    return "APPROVED".equalsIgnoreCase(proposalDTO.getStatus());
} else if (isFaculty) {
    // Faculty => can only see proposals they created
    return proposalDTO.getUserId().equals(currentUserId);
} else if (isApprover) {
    // Approver => can only see proposals where they are the currentApprover
    return proposalDTO.getCurrentApproverId() != null
            && proposalDTO.getCurrentApproverId().equals(currentUserId);
} else {
    return false;
}

    }


    public List<ProposalDTO> getProposalsByApproverId(Long approverId) {
        try {
            return proposalRepo.findByCurrentApprover_UserId(approverId).stream() // findByCurrentApprover_UserId()
                                                                                  // method is used to get all proposals
                                                                                  // associated with a specific
                                                                                  // approver.
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting proposals for approver id {}: ", approverId, e);
            return null;
        }
    }

    public List<ProposalDTO> getProposalsByFacultyIdAndStatus(Long facultyId, String status) {
        try {
            // Fetch from DB
            List<Proposal> proposals = proposalRepo.findByUser_UserIdAndStatus(facultyId, status);
            
            // Convert to DTO
            return proposals.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting proposals for faculty {} with status {}: ", facultyId, status, e);
            return Collections.emptyList();
        }
    }
    

    public List<ProposalDTO> getProposalsByStatus(String status) {
        try {
            return proposalRepo.findByStatus(status).stream() // findByStatus() method is used to get all proposals with
                                                              // a specific status.
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting proposals with status {}: ", status, e);
            return null;
        }
    }

    public ProposalDTO addProposal(ProposalDTO proposalDTO) {
        try {

            if (proposalDTO.getProposalDate() == null) {
                proposalDTO.setProposalDate(LocalDateTime.now()); // If the proposal date is not provided, the current
                                                                  // date and time are set.
            }

            Optional<User> user = userRepo.findById(proposalDTO.getUserId()); // findById() method is used to get the
                                                                              // user by ID.
            Optional<Department> department = departmentRepo.findById(proposalDTO.getDepartmentId()); // findById()
                                                                                                      // method is used
                                                                                                      // to get the
                                                                                                      // department by
                                                                                                      // ID.

            if (!user.isPresent() || !department.isPresent()) {
                logger.error("User or Department not found. UserId: {}, DeptId: {}",
                        proposalDTO.getUserId(), proposalDTO.getDepartmentId());
                return null;
            } // If the user or department is not found, an error message is logged and null
              // is returned.

            Proposal proposal = convertToEntity(proposalDTO); // convertToEntity() method is used to convert the
                                                              // ProposalDTO object to a Proposal entity.
            proposal.setUser(user.get()); // The user and department are set for the proposal.
            proposal.setDepartment(department.get());

            Proposal savedProposal = proposalRepo.save(proposal); // save() method is used to save the proposal to the
                                                                  // database.

            if (savedProposal.getCurrentApprover() != null) {
                User approver = savedProposal.getCurrentApprover();
                String subject = "New Proposal Submitted for Review";
                String message = String.format(
                        "A new proposal has been submitted for your review.<br><br>" +
                                "Proposal Details:<br>" +
                                "Item: %s<br>" +
                                "Category: %s<br>" +
                                "Description: %s<br>" +
                                "Estimated Cost: $%.2f<br>" +
                                "Business Purpose: %s",
                        savedProposal.getItemName(),
                        savedProposal.getCategory(),
                        savedProposal.getDescription(),
                        savedProposal.getEstimatedCost(),
                        savedProposal.getBusinessPurpose());

                // This URL points to your new route: /proposal/:proposalId
                // String link = "https://ravi-ai.com/proposal/" +
                // savedProposal.getProposalId();
                String link = "https://ravi-ai.com/proposal/"
                        + savedProposal.getProposalId();

                // Now call sendEmailWithLink
                emailService.sendEmailWithLink(
                        approver.getEmail(),
                        subject,
                        link,
                        message);
            }

            return convertToDTO(savedProposal); // The saved proposal is converted to a ProposalDTO object and returned.
        } catch (Exception e) {
            logger.error("Error adding proposal: ", e);
            return null;
        }

    }

    public List<ProposalDTO> searchProposals(Long userId, String status, LocalDateTime startDate,
            LocalDateTime endDate) {
        try {
            List<Proposal> proposals;

            if (userId != null && status != null && startDate != null && endDate != null) {
                proposals = proposalRepo.findByUser_UserIdAndStatusAndProposalDateBetween(
                        userId, status, startDate, endDate);
            } else if (userId != null && status != null) {
                proposals = proposalRepo.findByUser_UserIdAndStatus(userId, status);
            } else if (userId != null) {
                proposals = proposalRepo.findByUser_UserId(userId);
            } else {
                proposals = proposalRepo.findAll();
            }

            return proposals.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching proposals: ", e);
            return null;
        }
    }

    // public ProposalDTO updateProposal(Long id, ProposalDTO proposalDTO) {
    //     try {
    //         Optional<Proposal> existingProposal = proposalRepo.findById(id);
    //         if (existingProposal.isPresent()) {
    //             Proposal proposal = existingProposal.get();
    //             updateProposalFromDTO(proposal, proposalDTO);
    //             return convertToDTO(proposalRepo.save(proposal));
    //         }
    //         return null;
    //     } catch (Exception e) {
    //         logger.error("Error updating proposal with id {}: ", id, e);
    //         return null;
    //     }
    // } // This method is used to update an existing proposal. It first checks if the
      // proposal exists, then updates the proposal with the new data provided in the
      // ProposalDTO object.

      public ProposalDTO updateProposal(Long id, ProposalDTO proposalDTO) {
    try {
        // 1) Check if the proposal exists
        Optional<Proposal> existingProposalOpt = proposalRepo.findById(id);
        if (existingProposalOpt.isPresent()) {
            Proposal proposal = existingProposalOpt.get();

            // -----------------------------------------------------------------
            // 2) BUILD A ProposalHistory RECORD USING THE *OLD* DATA
            // -----------------------------------------------------------------
            ProposalHistory oldVersion = new ProposalHistory();
            oldVersion.setProposal(proposal);  // link to the main proposal

            // Copy all fields you want to preserve
            oldVersion.setItemName(proposal.getItemName());
            oldVersion.setCategory(proposal.getCategory());
            oldVersion.setDescription(proposal.getDescription());
            oldVersion.setQuantity(proposal.getQuantity());
            oldVersion.setEstimatedCost(proposal.getEstimatedCost());
            oldVersion.setVendorInfo(proposal.getVendorInfo());
            oldVersion.setBusinessPurpose(proposal.getBusinessPurpose());
            oldVersion.setStatus(proposal.getStatus());
            oldVersion.setProposalDate(proposal.getProposalDate());
            oldVersion.setExpectedDueDate(proposal.getExpectedDueDate());

            
            // If there's a currentApprover, store that userId
            oldVersion.setCurrentApproverId(
                proposal.getCurrentApprover() != null 
                    ? proposal.getCurrentApprover().getUserId() 
                    : null
            );
            
            // Department ID
            oldVersion.setDepartmentId(proposal.getDepartment().getDeptId());

            // versionNumber logic: find the highest version so far and add 1
            List<ProposalHistory> existingVersions = proposalHistoryRepo.findByProposal_ProposalId(id);
            int highestVersion = 0;
            for (ProposalHistory hist : existingVersions) {
                if (hist.getVersionNumber() != null && hist.getVersionNumber() > highestVersion) {
                    highestVersion = hist.getVersionNumber();
                }
            }
            oldVersion.setVersionNumber(highestVersion + 1);

            // Extra metadata (if desired)
            oldVersion.setChangedOn(LocalDateTime.now());
            oldVersion.setChangedBy(proposalDTO.getUserId()); 
              // e.g. the user who triggered this update

            // 3) SAVE THE "OLD" VERSION
            proposalHistoryRepo.save(oldVersion);

            // -----------------------------------------------------------------
            // 4) OVERWRITE THE PROPOSAL WITH NEW DATA (original functionality)
            // -----------------------------------------------------------------
            // This is your existing line from "updateProposalFromDTO(...)"
            updateProposalFromDTO(proposal, proposalDTO);

            // If the department changed, set it:
            if (proposalDTO.getDepartmentId() != null) {
                Optional<Department> deptOpt = departmentRepo.findById(proposalDTO.getDepartmentId());
                deptOpt.ifPresent(proposal::setDepartment);
            }

            // If the currentApprover changed, set it:
            if (proposalDTO.getCurrentApproverId() != null) {
                Optional<User> approverOpt = userRepo.findById(proposalDTO.getCurrentApproverId());
                approverOpt.ifPresent(proposal::setCurrentApprover);
            }

            // 5) SAVE THE UPDATED PROPOSAL (original logic)
            Proposal updatedProposal = proposalRepo.save(proposal);

            // 6) RETURN THE UPDATED PROPOSAL AS DTO (original logic)
            return convertToDTO(updatedProposal);
        }

        // If not present, return null or handle 404
        return null;

    } catch (Exception e) {
        // Original error-handling
        logger.error("Error updating proposal with id {}: ", id, e);
        return null;
    }
}



    public ProposalDTO updateProposalStatus(Long id, String newStatus, Long approverId, Long fundingSourceId,
            String comments) {
        try {
            Optional<Proposal> existingProposal = proposalRepo.findById(id);
            Optional<User> approver = userRepo.findById(approverId);

            if (existingProposal.isPresent() && approver.isPresent()) {
                Proposal proposal = existingProposal.get();
                String oldStatus = proposal.getStatus();
                proposal.setStatus(newStatus);
                proposal.setCurrentApprover(approver.get());

                // Save the proposal first
                Proposal updatedProposal = proposalRepo.save(proposal);

                // Add the history entry
                approvalHistoryService.addHistoryEntry(id, approverId, fundingSourceId, oldStatus, newStatus, comments,
                        LocalDateTime.now());
                // Send email to the faculty member who created the proposal
                // Send email to the faculty member who created the proposal
                User faculty = updatedProposal.getUser();
                String facultySubject = "Your Proposal Status Has Been Updated";
                String facultyMessage = String.format(
                        "Your proposal has been %s.<br><br>" +
                                "Proposal Details:<br>" +
                                "Item: %s<br>" +
                                "Status: %s<br>" +
                                "Comments: %s<br>",
                        newStatus.toLowerCase(),
                        updatedProposal.getItemName(),
                        newStatus,
                        comments != null ? comments : "No comments provided");

                // String link = "https://ravi-ai.com/proposal/" +
                // updatedProposal.getProposalId();
                String link = "https://ravi-ai.com/proposal/"
                        + updatedProposal.getProposalId();

                emailService.sendEmailWithLink(
                        faculty.getEmail(),
                        facultySubject,
                        link,
                        facultyMessage);

                // === BUDGET ALERT CHECK ===
                if ("APPROVED".equalsIgnoreCase(newStatus)) {
                    // 1) Get the current date/time
                    LocalDateTime now = LocalDateTime.now();
                    int currentYear = now.getYear();
                    int currentMonth = now.getMonthValue();

                    String purchaserSubject = "Proposal Approved – Please Proceed with Ordering";
                    String purchaserMessage = String.format(
                            "Hello Purchaser,<br><br>" +
                                    "A newly approved proposal is awaiting your purchasing steps.<br><br>" +
                                    "Proposal ID: %d<br>" +
                                    "Item: %s<br>" +
                                    "Estimated Cost: $%.2f<br><br>" +
                                    "Please log in to place the order.<br><br>" +
                                    "Thank you!",
                            updatedProposal.getProposalId(),
                            updatedProposal.getItemName(),
                            updatedProposal.getEstimatedCost() != null ? updatedProposal.getEstimatedCost() : 0.0);

                    // 2) Typically fetch Purchasers (roleId=4)
                    List<User> purchasers = userRepo.findAll().stream()
                    .filter(u -> u.getRoles() != null 
                            && u.getRoles().stream().anyMatch(r -> r.getRoleId() == 4)
                    )
                    .collect(Collectors.toList());
                

                    // 3) Construct the link
                    String purchaserLink = "https://ravi-ai.com/proposal/" + updatedProposal.getProposalId();

                    // 4) Send the email to each purchaser
                    for (User purchaser : purchasers) {
                        emailService.sendEmailWithLink(
                                purchaser.getEmail(),
                                purchaserSubject,
                                purchaserLink,
                                purchaserMessage);
                    }

                    // 2) Fetch all proposals for this approver that are APPROVED
                    List<Proposal> approvedProposals = proposalRepo
                            .findByCurrentApprover_UserIdAndStatus(approverId, "APPROVED");

                    // 3) Sum the costs of proposals approved in the same year/month
                    double totalApprovedThisMonth = approvedProposals.stream()
                            .filter(p -> p.getProposalDate().getYear() == currentYear
                                    && p.getProposalDate().getMonthValue() == currentMonth)
                            .mapToDouble(p -> p.getEstimatedCost() != null ? p.getEstimatedCost() : 0.0)
                            .sum();

                    // 4) Call ApproverBudgetService to see if we crossed 50% or 80%
                    approverBudgetService.checkAndSendBudgetAlert(approverId, currentYear, currentMonth,
                            totalApprovedThisMonth);
                }

                // Convert and return the updated proposal
                return convertToDTO(updatedProposal);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error updating proposal status with id {}: ", id, e);
            return null;
        }
    }

    // public ProposalDTO addComment(Long proposalId,
    // Long currentUserId,
    // Long fundingSourceId,
    // String comments,
    // String actionDateString) {
    // try {
    // // 1) Find the proposal
    // Optional<Proposal> proposalOpt = proposalRepo.findById(proposalId);
    // if (!proposalOpt.isPresent()) {
    // // Return null, or throw exception => your controller can respond 404
    // return null;
    // }
    // Proposal proposal = proposalOpt.get();

    // // 2) Find the user (we still want to ensure the user exists, or at least is
    // // logged in)
    // Optional<User> userOpt = userRepo.findById(currentUserId);
    // if (!userOpt.isPresent()) {
    // // Return null => triggers 403 or 404 in the controller
    // return null;
    // }
    // // User user = userOpt.get();

    // User commentAuthor = userOpt.get();

    // // 4) Actually add the comment
    // FundingSource fundingSource = null;
    // if (fundingSourceId != null) {
    // fundingSource = fundingSourceRepo.findById(fundingSourceId).orElse(null);
    // }

    // LocalDateTime finalActionDate = LocalDateTime.now();
    // if (actionDateString != null && !actionDateString.isEmpty()) {
    // DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    // finalActionDate = LocalDateTime.parse(actionDateString, formatter);
    // }

    // approvalHistoryService.addHistoryEntry(
    // proposalId,
    // currentUserId,
    // (fundingSource != null ? fundingSource.getSourceId() : null),
    // proposal.getStatus(), // old status
    // proposal.getStatus(), // new status => same
    // comments,
    // finalActionDate);

    // try {
    // // Build your recipients
    // List<User> recipients = new ArrayList<>();
    // // The requestor/faculty who created the proposal
    // if (proposal.getUser() != null) {
    // recipients.add(proposal.getUser());
    // }
    // // The current approver
    // if (proposal.getCurrentApprover() != null
    // && !Objects.equals(proposal.getCurrentApprover().getUserId(),
    // proposal.getUser().getUserId())) {
    // recipients.add(proposal.getCurrentApprover());
    // }
    // String subject = "New Comment on Proposal #" + proposalId;
    // String commentAuthorName = commentAuthor.getFirstName() + " " +
    // commentAuthor.getLastName();
    // String message = String.format(
    // "Hello,<br>" +
    // "A new comment was posted by <b>%s</b>.<br>" +
    // "Comment Text: <i>%s</i><br><br>" +
    // "Please log in to view or reply.<br>",
    // commentAuthorName,
    // comments
    // );

    // String link = "https://ravi-ai.com/proposal/" + proposalId;

    // // Send to each recipient
    // for (User recipient : recipients) {
    // emailService.sendEmailWithLink(
    // recipient.getEmail(),
    // subject,
    // link,
    // message
    // );
    // }
    // } catch (Exception mailEx) {
    // logger.error("Error sending comment notification emails", mailEx);
    // }

    // // Return the updated proposal
    // return convertToDTO(proposal);
    // } catch (Exception e) {
    // logger.error("Error adding comment: ", e);
    // return null;
    // }
    // }

    public ProposalDTO addComment(Long proposalId,
            Long currentUserId,
            Long fundingSourceId,
            String comments,
            String actionDateString) {
        try {
            // 1) Find the proposal
            Optional<Proposal> proposalOpt = proposalRepo.findById(proposalId);
            if (!proposalOpt.isPresent()) {
                return null; // or throw NotFound exception
            }
            Proposal proposal = proposalOpt.get();

            // 2) Find the user who is adding the comment
            Optional<User> userOpt = userRepo.findById(currentUserId);
            if (!userOpt.isPresent()) {
                return null; // or throw 403
            }
            User commentAuthor = userOpt.get();

            // 3) Prepare the FundingSource if provided
            FundingSource fundingSource = null;
            if (fundingSourceId != null) {
                fundingSource = fundingSourceRepo.findById(fundingSourceId).orElse(null);
            }

            // 4) Determine the finalActionDate
            LocalDateTime finalActionDate = LocalDateTime.now();
            if (actionDateString != null && !actionDateString.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                finalActionDate = LocalDateTime.parse(actionDateString, formatter);
            }

            // 5) Add the comment to approval history
            approvalHistoryService.addHistoryEntry(
                    proposalId,
                    currentUserId,
                    (fundingSource != null ? fundingSource.getSourceId() : null),
                    proposal.getStatus(), // old status
                    proposal.getStatus(), // new status => same for a comment
                    comments,
                    finalActionDate);

            // ---------------------------
            // NEW or CHANGED: build recipients
            // ---------------------------
            try {
                // Convert the existing 'proposal' entity to a ProposalDTO
                // so we can reuse isUserAuthorizedToViewProposal
                ProposalDTO tempDto = convertToDTO(proposal);

                // Grab all possible users from the DB
                List<User> allUsers = userRepo.findAll();

                // We'll build the list of recipients
                List<User> recipients = new ArrayList<>();

                for (User potentialViewer : allUsers) {
                    // 1) Check if the user is authorized to view
                    if (potentialViewer.getRoles() != null 
        && potentialViewer. getRoles().stream().anyMatch(r -> r.getRoleId() == 1)) {
        continue; // no notifications to admin
    }
    
                    boolean canView = isUserAuthorizedToViewProposal(tempDto, potentialViewer.getUserId());

                    // 2) Exclude the comment author
                    if (canView && !Objects.equals(potentialViewer.getUserId(), commentAuthor.getUserId())) {
                        recipients.add(potentialViewer);
                    }
                }

                // Build the email subject/message
                String subject = "New Comment on Proposal #" + proposalId;
                String commentAuthorName = commentAuthor.getFirstName() + " " + commentAuthor.getLastName();
                String message = String.format(
                        "Hello,<br>" +
                                "A new comment was posted by <b>%s</b>.<br>" +
                                "Comment Text: <i>%s</i><br><br>" +
                                "Please log in to view or reply.<br>",
                        commentAuthorName,
                        comments);

                // Example link to the proposal detail page
                String link = "https://ravi-ai.com/proposal/" + proposalId;

                // 3) Send the email to each recipient
                for (User recipient : recipients) {
                    emailService.sendEmailWithLink(
                            recipient.getEmail(),
                            subject,
                            link,
                            message);
                }
            } catch (Exception mailEx) {
                logger.error("Error sending comment notification emails", mailEx);
            }

            // Return the updated proposal
            return convertToDTO(proposal);

        } catch (Exception e) {
            logger.error("Error adding comment: ", e);
            return null;
        }
    }

    public List<ProposalDTO> getProposalsByApproverAndStatus(Long approverId, String status) {
        try {
            return proposalRepo.findByCurrentApprover_UserIdAndStatus(approverId, status)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting proposals for approver {} with status {}: ", approverId, status, e);
            return Collections.emptyList();
        }
    }

    // This method is used to update the status of a proposal. It first checks if
    // the proposal and approver exist, then updates the status of the proposal with
    // the new status provided.

    public void deleteProposal(Long id) {
        try {
            proposalRepo.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting proposal with id {}: ", id, e);
        }
    }

    public ProposalDTO convertToDTO(Proposal proposal) {
        ProposalDTO proposalDTO = new ProposalDTO();

        proposalDTO.setProposalId(proposal.getProposalId());
        proposalDTO.setUserId(proposal.getUser().getUserId());
        proposalDTO.setItemName(proposal.getItemName());
        proposalDTO.setCategory(proposal.getCategory());

        // Already in your snippet, ensures the "description" field is included:
        proposalDTO.setDescription(proposal.getDescription());

        proposalDTO.setQuantity(proposal.getQuantity());
        proposalDTO.setEstimatedCost(proposal.getEstimatedCost());
        proposalDTO.setVendorInfo(proposal.getVendorInfo());
        proposalDTO.setBusinessPurpose(proposal.getBusinessPurpose());
        proposalDTO.setStatus(proposal.getStatus());
        proposalDTO.setProposalDate(proposal.getProposalDate());
        proposalDTO.setExpectedDueDate(proposal.getExpectedDueDate());
        proposalDTO.setCurrentApproverId(
                proposal.getCurrentApprover() != null ? proposal.getCurrentApprover().getUserId() : null);
        proposalDTO.setDepartmentId(proposal.getDepartment().getDeptId());

        // ============== NEW LINES FOR REQUESTER/APPROVER NAMES ==============
        // (Assuming user.getEmail() is the best field to display.
        // If you have user.getFirstName() / getLastName(), feel free to adapt.)

        // 1) Requester name (the person who created the proposal)
        proposalDTO.setRequesterName(proposal.getUser().getEmail());

        String requesterFullName = proposal.getUser().getFirstName()
        + " "
        + proposal.getUser().getLastName();
proposalDTO.setRequesterOriginalName(requesterFullName);

if (proposal.getCurrentApprover() != null) {
String approverFullName = proposal.getCurrentApprover().getFirstName()
           + " "
           + proposal.getCurrentApprover().getLastName();
proposalDTO.setApproverOriginalName(approverFullName);
} else {
proposalDTO.setApproverOriginalName(null);
}

        // 2) Approver name (only if there's a currentApprover)
        if (proposal.getCurrentApprover() != null) {
            proposalDTO.setApproverName(proposal.getCurrentApprover().getEmail());
        } else {
            proposalDTO.setApproverName(null);
        }
        // =====================================================================

        // If the proposal is approved, also show order/delivery fields (as you already
        // do):
        if ("APPROVED".equalsIgnoreCase(proposal.getStatus())) {
            PurchaseOrder purchaseOrder = purchaseOrderRepo.findByProposal_ProposalId(proposal.getProposalId());
            if (purchaseOrder != null) {
                proposalDTO.setOrderStatus(purchaseOrder.getOrderStatus());
                proposalDTO.setDeliveryStatus(purchaseOrder.getDeliveryStatus());
            }
        }

        return proposalDTO;
    }

    public FacultyStatsDTO getFacultyStatsForUser(Long facultyUserId,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        // 1) Find all proposals by this user (facultyUserId)
        List<Proposal> allProposals = proposalRepo.findByUser_UserId(facultyUserId);

        // 2) Filter to the given timeframe (e.g., fromDate .. toDate)
        // If you only want the last 1 year, you can do:
        List<Proposal> filteredByDate = allProposals.stream()
                .filter(p -> p.getProposalDate() != null
                        && (p.getProposalDate().isAfter(fromDate) || p.getProposalDate().isEqual(fromDate))
                        && (p.getProposalDate().isBefore(toDate) || p.getProposalDate().isEqual(toDate)))
                .collect(Collectors.toList());

        // 3) Count how many submitted
        int totalSubmitted = filteredByDate.size();

        // 4) Among them, find which are "APPROVED", and sum their cost
        List<Proposal> approved = filteredByDate.stream()
                .filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        int totalApproved = approved.size();
        double sumApprovedAmount = approved.stream()
                .mapToDouble(p -> p.getEstimatedCost() != null ? p.getEstimatedCost() : 0.0)
                .sum();

        // 5) Construct and return the DTO
        FacultyStatsDTO dto = new FacultyStatsDTO();
        dto.setFacultyId(facultyUserId);

        // If you want the faculty name, you can do:
        Optional<User> facultyOpt = userRepo.findById(facultyUserId);
        if (facultyOpt.isPresent()) {
            User facultyUser = facultyOpt.get();
            String fullName = facultyUser.getFirstName() + " " + facultyUser.getLastName();
            dto.setFacultyName(fullName);
        }

        dto.setTotalSubmittedCount(totalSubmitted);
        dto.setTotalApprovedCount(totalApproved);
        dto.setTotalApprovedAmount(sumApprovedAmount);

        return dto;
    }

    public HistoryLogsResponse getHistoryLogs() {
        // 1) Get all proposals or a relevant subset:
        List<Proposal> allProposals = proposalRepo.findAll();

        // 2) Create a data structure to hold stats:
        // a Map< Integer (year), YearlyStatsDTO > for each year
        Map<Integer, YearlyStatsDTO> yearMap = new HashMap<>();

        // 3) Loop over every proposal, group by year, accumulate totals
        for (Proposal p : allProposals) {
            if (p.getProposalDate() == null) {
                // If it doesn't have a date, skip or handle
                continue;
            }
            int year = p.getProposalDate().getYear();

            // If we don't have an entry yet for this year, create it
            if (!yearMap.containsKey(year)) {
                YearlyStatsDTO ystats = new YearlyStatsDTO();
                // a map for month breakdown: "Jan", "Feb", ...
                Map<String, MonthlyStatsDTO> monthlyMap = new HashMap<>();
                ystats.setMonthlyBreakdown(monthlyMap);

                yearMap.put(year, ystats);
            }

            // 4) Grab the YearlyStatsDTO
            YearlyStatsDTO ydto = yearMap.get(year);

            // Bump totalProposals
            ydto.setTotalProposals(ydto.getTotalProposals() + 1);

            // If it's approved, increment approvedProposals and budgetApproved
            if ("APPROVED".equalsIgnoreCase(p.getStatus())) {
                ydto.setApprovedProposals(ydto.getApprovedProposals() + 1);
                double cost = (p.getEstimatedCost() != null) ? p.getEstimatedCost() : 0.0;
                ydto.setBudgetApproved(ydto.getBudgetApproved() + cost);
            }

            // 5) Determine the short month name, e.g. "Jan", "Feb", ...
            // Or store them as numeric, up to you.
            String monthName = p.getProposalDate().getMonth().name().substring(0, 3);
            // e.g. "JAN", "FEB"... If you want capitalization or just first 3 letters

            // Then get or create a MonthlyStatsDTO for that month
            MonthlyStatsDTO mstats = ydto.getMonthlyBreakdown().get(monthName);
            if (mstats == null) {
                mstats = new MonthlyStatsDTO();
                ydto.getMonthlyBreakdown().put(monthName, mstats);
            }
            // Bump the month’s totals
            mstats.setTotal(mstats.getTotal() + 1);

            if ("APPROVED".equalsIgnoreCase(p.getStatus())) {
                mstats.setApproved(mstats.getApproved() + 1);
                double cost = (p.getEstimatedCost() != null) ? p.getEstimatedCost() : 0.0;
                mstats.setBudgetApproved(mstats.getBudgetApproved() + cost);
            }
        }

        // 6) Convert that Map<Integer, YearlyStatsDTO> into
        // the final List<Map<String, YearlyStatsDTO>> shape
        List<Map<String, YearlyStatsDTO>> finalList = new ArrayList<>();

        for (Map.Entry<Integer, YearlyStatsDTO> entry : yearMap.entrySet()) {
            Integer year = entry.getKey();
            YearlyStatsDTO stats = entry.getValue();

            // We want an object like { "2024": { totalProposals..., monthlyBreakdown... } }
            Map<String, YearlyStatsDTO> singleYearMap = new HashMap<>();
            singleYearMap.put(String.valueOf(year), stats);

            // Add it to finalList
            finalList.add(singleYearMap);
        }

        // 7) Construct and return the HistoryLogsResponse
        HistoryLogsResponse response = new HistoryLogsResponse(finalList);
        return response;
    }

    public HistoryLogsResponse getHistoryLogsByFaculty(Long facultyUserId) {
        // 1) Get all proposals for this particular faculty member
        List<Proposal> facultyProposals = proposalRepo.findByUser_UserIdOrderByProposalDateDesc(facultyUserId);

        // 2) Create a data structure to hold stats:
        // Map<Integer, YearlyStatsDTO> yearMap = new HashMap<>();

        Map<Integer, YearlyStatsDTO> yearMap = new TreeMap<>(Collections.reverseOrder());


        // 3) Loop over each proposal and do the same grouping as in getHistoryLogs():
        for (Proposal p : facultyProposals) {
            // skip if date is null
            if (p.getProposalDate() == null)
                continue;

            int year = p.getProposalDate().getYear();

            // If not present, create a new YearlyStatsDTO
            if (!yearMap.containsKey(year)) {
                YearlyStatsDTO ystats = new YearlyStatsDTO();
                ystats.setMonthlyBreakdown(new HashMap<>());
                yearMap.put(year, ystats);
            }

            YearlyStatsDTO ydto = yearMap.get(year);
            // bump totalProposals
            ydto.setTotalProposals(ydto.getTotalProposals() + 1);

            // if approved => bump approvedProposals, add to budgetApproved
            if ("APPROVED".equalsIgnoreCase(p.getStatus())) {
                ydto.setApprovedProposals(ydto.getApprovedProposals() + 1);
                double cost = (p.getEstimatedCost() != null) ? p.getEstimatedCost() : 0.0;
                ydto.setBudgetApproved(ydto.getBudgetApproved() + cost);
            }

            // figure out month name
            String monthName = p.getProposalDate().getMonth().name().substring(0, 3);
            MonthlyStatsDTO mstats = ydto.getMonthlyBreakdown().get(monthName);
            if (mstats == null) {
                mstats = new MonthlyStatsDTO();
                ydto.getMonthlyBreakdown().put(monthName, mstats);
            }

            // bump monthly totals
            mstats.setTotal(mstats.getTotal() + 1);
            if ("APPROVED".equalsIgnoreCase(p.getStatus())) {
                mstats.setApproved(mstats.getApproved() + 1);
                double cost = (p.getEstimatedCost() != null) ? p.getEstimatedCost() : 0.0;
                mstats.setBudgetApproved(mstats.getBudgetApproved() + cost);
            }
        }

        System.out.println("YearMap: " + yearMap);

        // 4) Convert Map<Integer,YearlyStatsDTO> -> List<Map<String,YearlyStatsDTO>>
        List<Map<String, YearlyStatsDTO>> finalList = new ArrayList<>();
        for (Map.Entry<Integer, YearlyStatsDTO> entry : yearMap.entrySet()) {
            Integer year = entry.getKey();
            YearlyStatsDTO stats = entry.getValue();

            Map<String, YearlyStatsDTO> singleYearMap = new HashMap<>();
            singleYearMap.put(String.valueOf(year), stats);

            finalList.add(singleYearMap);
        }

        // 5) Build and return the HistoryLogsResponse
        HistoryLogsResponse response = new HistoryLogsResponse(finalList);
        return response;
    }

    public Proposal convertToEntity(ProposalDTO proposalDTO) {
        Proposal proposal = new Proposal();
        proposal.setProposalId(proposalDTO.getProposalId());
        proposal.setItemName(proposalDTO.getItemName());
        proposal.setCategory(proposalDTO.getCategory());
        proposal.setDescription(proposalDTO.getDescription());
        proposal.setQuantity(proposalDTO.getQuantity());
        proposal.setEstimatedCost(proposalDTO.getEstimatedCost());
        proposal.setVendorInfo(proposalDTO.getVendorInfo());
        proposal.setBusinessPurpose(proposalDTO.getBusinessPurpose());
        proposal.setStatus(proposalDTO.getStatus());
        proposal.setProposalDate(proposalDTO.getProposalDate());
        proposal.setExpectedDueDate(proposalDTO.getExpectedDueDate());

        Optional<User> user = userRepo.findById(proposalDTO.getUserId());
        user.ifPresent(proposal::setUser);

        Optional<User> currentApprover = userRepo.findById(proposalDTO.getCurrentApproverId());
        currentApprover.ifPresent(proposal::setCurrentApprover);

        Optional<Department> department = departmentRepo.findById(proposalDTO.getDepartmentId());
        department.ifPresent(proposal::setDepartment);

        return proposal;
    }

    private void updateProposalFromDTO(Proposal proposal, ProposalDTO dto) {
        proposal.setItemName(dto.getItemName());
        proposal.setCategory(dto.getCategory());
        proposal.setDescription(dto.getDescription());
        proposal.setQuantity(dto.getQuantity());
        proposal.setEstimatedCost(dto.getEstimatedCost());
        proposal.setVendorInfo(dto.getVendorInfo());
        proposal.setBusinessPurpose(dto.getBusinessPurpose());
        proposal.setStatus(dto.getStatus());
        proposal.setProposalDate(dto.getProposalDate());
        proposal.setExpectedDueDate(dto.getExpectedDueDate());
    }
 

    @Scheduled(cron = "0 11 22 * * ?", zone = "America/New_York")
    public void sendReminderEmails() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate oneWeekFromToday = today.plusDays(7);
    
            // Fetch proposals with pending status and due date within a week
            List<Proposal> pendingProposals = proposalRepo.findByStatus("PENDING")
                    .stream()
                    .filter(proposal -> proposal.getExpectedDueDate() != null)
                    .filter(proposal -> proposal.getExpectedDueDate().isAfter(today) && 
                                        proposal.getExpectedDueDate().isBefore(oneWeekFromToday))
                    .collect(Collectors.toList());
    
            // Loop through proposals and send emails
            for (Proposal proposal : pendingProposals) {
                User approver = proposal.getCurrentApprover();
    
                if (approver != null && approver.getEmail() != null) {
                    String subject = "Reminder: Pending Proposal with Approaching Due Date";
                    String message = String.format(
                            "Dear %s,<br><br>" +
                                    "This is a reminder that a proposal assigned to you is pending action.<br><br>" +
                                    "Proposal Details:<br>" +
                                    "Item: %s<br>" +
                                    "Category: %s<br>" +
                                    "Estimated Cost: $%.2f<br>" +
                                    "Expected Due Date: %s<br><br>" +
                                    "Please take the necessary action before the due date.<br>" +
                                    "<a href='https://ravi-ai.com/proposal/%s'>Click here to view the proposal</a><br><br>" +

                                    "Thank you.",
                            approver.getFirstName(),
                            proposal.getItemName(),
                            proposal.getCategory(),
                            proposal.getEstimatedCost(),
                            proposal.getExpectedDueDate(), // Already LocalDate, no conversion needed
                            "https://ravi-ai.com/proposal/" + proposal.getProposalId() // Proposal link
                    );
    
                    // Send email
                    emailService.sendEmailWithLink(approver.getEmail(), subject, null, message);
                }
            }
    
            logger.info("Reminder emails sent successfully for pending proposals.");
        } catch (Exception e) {
            logger.error("Error sending reminder emails: ", e);
        }
    }
    

    
}

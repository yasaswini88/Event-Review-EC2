package com.example.event_review.Service;

import com.example.event_review.Entity.ApproverBudget;
import com.example.event_review.Entity.User;
import com.example.event_review.Repo.ApproverBudgetRepo;
import com.example.event_review.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for managing 
 * an Approver's monthly budget and alert preferences.
 */
@Service
public class ApproverBudgetService {

    @Autowired
    private ApproverBudgetRepo approverBudgetRepo;

    @Autowired
    private UserRepo userRepo;

    // We'll use the existing EmailService to send alert emails later.
    @Autowired
    private EmailService emailService;

    /**
     * Admin sets or updates the monthly budget for an Approver.
     *
     * @param adminId        The ID of the user with role Admin.
     * @param approverId     The ID of the user with role Approver.
     * @param monthlyBudget  The budget allocated to the Approver.
     * @param year           The budget's applicable year.
     * @param month          The budget's applicable month.
     * @return               The updated or newly created ApproverBudget record.
     */
    public ApproverBudget adminSetMonthlyBudget(Long adminId, Long approverId, Double monthlyBudget, int year, int month) {
        // 1) Confirm that 'adminId' is indeed an Admin
        User adminUser = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("User not found: " + adminId));
        if (!isAdmin(adminUser)) {
            throw new SecurityException("Only Admins can set monthly budgets!");
        }

        // 2) Find the Approver
        User approverUser = userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found: " + approverId));
        if (!isApprover(approverUser)) {
            throw new IllegalArgumentException("User with ID " + approverId + " is not an Approver!");
        }

        // 3) Find or create the ApproverBudget record
        Optional<ApproverBudget> optBudget = approverBudgetRepo.findByApproverAndYearAndMonth(approverUser, year, month);
        ApproverBudget ab = optBudget.orElse(new ApproverBudget());
        ab.setApprover(approverUser);
        ab.setYear(year);
        ab.setMonth(month);

        // 4) Admin sets the monthly budget
        ab.setMonthlyBudget(monthlyBudget);

        // 5) Save and return
        return approverBudgetRepo.save(ab);
    }

    /**
     * Approver sets or updates their own alert preferences.
     *
     * @param approverId      The ID of the user with role Approver.
     * @param year            The alert's applicable year.
     * @param month           The alert's applicable month.
     * @param alertEnabled    Whether the Approver wants alerts enabled.
     * @param alertAt50       Whether the Approver wants a 50% alert.
     * @param alertAt80       Whether the Approver wants an 80% alert.
     * @return                The updated or newly created ApproverBudget record.
     */
    public ApproverBudget approverSetAlertPreferences(Long approverId, int year, int month, boolean alertEnabled, boolean alertAt50, boolean alertAt80) {
        // 1) Confirm that 'approverId' is indeed an Approver
        User approver = userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found: " + approverId));
        if (!isApprover(approver)) {
            throw new SecurityException("Only Approvers can set their alert preferences!");
        }

        // 2) Find or create the ApproverBudget record
        Optional<ApproverBudget> optBudget = approverBudgetRepo.findByApproverAndYearAndMonth(approver, year, month);
        ApproverBudget ab = optBudget.orElse(new ApproverBudget());
        ab.setApprover(approver);
        ab.setYear(year);
        ab.setMonth(month);

        // 3) Approver sets only the alert fields
        ab.setAlertEnabled(alertEnabled);
        ab.setAlertAt50(alertAt50);
        ab.setAlertAt80(alertAt80);

        // 4) Save and return
        return approverBudgetRepo.save(ab);
    }

    /**
     * This method checks if the Approver's total approved spend 
     * has crossed 50% or 80% threshold. 
     * If so, and if alerts are enabled, it sends an email.
     * 
     * @param approverId          The ID of the Approver.
     * @param year                The applicable year.
     * @param month               The applicable month.
     * @param totalApprovedSoFar  The total approved spending so far.
     */
    public void checkAndSendBudgetAlert(Long approverId, int year, int month, Double totalApprovedSoFar) {
        // 1) Fetch the Approver
        User approver = userRepo.findById(approverId)
                .orElse(null);
        if (approver == null) {
            return; // no approver => do nothing
        }

        // 2) Find the ApproverBudget for that user & month/year
        Optional<ApproverBudget> abOpt = approverBudgetRepo.findByApproverAndYearAndMonth(approver, year, month);
        if (!abOpt.isPresent()) {
            return; // no budget record => do nothing
        }
        ApproverBudget ab = abOpt.get();
        
        // 3) If alerts are disabled => do nothing
        if (!ab.isAlertEnabled()) {
            return;
        }

        // 4) If monthly budget is null or zero => do nothing
        if (ab.getMonthlyBudget() == null || ab.getMonthlyBudget() <= 0) {
            return;
        }

        // 5) Check threshold
        double usedRatio = totalApprovedSoFar / ab.getMonthlyBudget(); // e.g. 0.5 = 50%

        // If the Approver selected "alertAt50" and usage is 50% or more (but <80)
        if (ab.isAlertAt50() && usedRatio >= 0.5 && usedRatio < 0.8) {
            // => Send an email for 50% threshold
            sendAlertEmail(approver, 50, totalApprovedSoFar, ab.getMonthlyBudget(), year, month);
        }
        // If the Approver selected "alertAt80" and usage is 80% or more
        else if (ab.isAlertAt80() && usedRatio >= 0.8) {
            // => Send an email for 80% threshold
            sendAlertEmail(approver, 80, totalApprovedSoFar, ab.getMonthlyBudget(), year, month);
        }
    }

    /**
     * Helper method to send an email alert to the approver.
     */
    private void sendAlertEmail(User approver, int threshold, double used, double budget, int year, int month) {
        String subject = "Budget Alert Notification";
        String message = String.format(
            "Hello %s,\n\n" +
            "Your spending for %d/%d has reached %d%% of your allocated budget.\n" +
            "Allocated budget: $%.2f\n" +
            "Used so far: $%.2f\n\n" +
            "Please review your approvals.\n\n" +
            "Regards,\nSystem",
            approver.getFirstName() != null ? approver.getFirstName() : "Approver",
            month, year,
            threshold,
            budget,
            used
        );

        // Using your existing EmailService:
        emailService.sendSimpleEmail(approver.getEmail(), subject, message);
    }

    public ApproverBudget getApproverBudget(Long approverId, int year, int month) {
        User approver = userRepo.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found: " + approverId));
        // optionally check if (isApprover(approver)) ...
        
        Optional<ApproverBudget> abOpt = approverBudgetRepo.findByApproverAndYearAndMonth(approver, year, month);
        return abOpt.orElse(new ApproverBudget()); 
        // or you might return null if not found, 
        // but returning a new ApproverBudget is fine.
    }
    

    /** Utility checks **/
    private boolean isAdmin(User user) {
        return user.getRoles() != null 
               && "Admin".equalsIgnoreCase(user.getRoles().getRoleName());
    }

    private boolean isApprover(User user) {
        return user.getRoles() != null 
               && "Approver".equalsIgnoreCase(user.getRoles().getRoleName());
    }
}

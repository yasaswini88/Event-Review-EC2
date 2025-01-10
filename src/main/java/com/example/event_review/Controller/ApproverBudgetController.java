package com.example.event_review.Controller;

import com.example.event_review.Entity.ApproverBudget;
import com.example.event_review.Service.ApproverBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin
public class ApproverBudgetController {

    @Autowired
    private ApproverBudgetService approverBudgetService;

    /**
     * Admin Endpoint: Sets or updates the monthly budget for an Approver.
     * 
     * Example usage:
     *   POST /api/budget/admin-set-budget?adminId=1&approverId=252&monthlyBudget=10000&year=2025&month=1
     *
     * @param adminId the ID of the Admin user calling this endpoint
     * @param approverId the ID of the Approver user for whom the budget is being set
     * @param monthlyBudget the budget allocated to the Approver for the specified month/year
     * @param year the applicable year for the budget (e.g., 2025)
     * @param month the applicable month for the budget (e.g., 1 for January)
     * @return the updated or newly created ApproverBudget record
     */
    @PostMapping("/admin-set-budget")
    public ApproverBudget adminSetBudget(
        @RequestParam Long adminId,
        @RequestParam Long approverId,
        @RequestParam Double monthlyBudget,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return approverBudgetService.adminSetMonthlyBudget(
            adminId,
            approverId,
            monthlyBudget,
            year,
            month
        );
    }

    /**
     * Approver Endpoint: Sets or updates their own alert preferences.
     * 
     * Example usage:
     *   POST /api/budget/approver-set-alerts?approverId=252&year=2025&month=1&alertEnabled=true&alertAt50=true&alertAt80=true
     *
     * @param approverId the ID of the Approver user calling this endpoint
     * @param year the applicable year for the alert preferences (e.g., 2025)
     * @param month the applicable month for the alert preferences (e.g., 1 for January)
     * @param alertEnabled boolean indicating whether alerts are enabled
     * @param alertAt50 boolean indicating whether 50% threshold alerts are desired
     * @param alertAt80 boolean indicating whether 80% threshold alerts are desired
     * @return the updated or newly created ApproverBudget record
     */
    @PostMapping("/approver-set-alerts")
    public ApproverBudget approverSetAlerts(
        @RequestParam Long approverId,
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam boolean alertEnabled,
        @RequestParam boolean alertAt50,
        @RequestParam boolean alertAt80
    ) {
        return approverBudgetService.approverSetAlertPreferences(
            approverId,
            year,
            month,
            alertEnabled,
            alertAt50,
            alertAt80
        );
    }
    @GetMapping("/approver-preferences")
public ApproverBudget getApproverPreferences(
    @RequestParam Long approverId,
    @RequestParam int year,
    @RequestParam int month
) {
    // 1) fetch user from DB (check role if you like)
    // 2) call service to find the ApproverBudget if it exists
    // 3) return the ApproverBudget or a new instance if not found
    return approverBudgetService.getApproverBudget(approverId, year, month);
}

}

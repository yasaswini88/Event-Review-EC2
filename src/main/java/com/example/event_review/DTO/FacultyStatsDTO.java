
package com.example.event_review.DTO;

public class FacultyStatsDTO {
    private Long facultyId;
    private String facultyName;       
    private int totalSubmittedCount;  
    private int totalApprovedCount;   
    private Double totalApprovedAmount;
    public Long getFacultyId() {
        return facultyId;
    }
    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }
    public String getFacultyName() {
        return facultyName;
    }
    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
    public int getTotalSubmittedCount() {
        return totalSubmittedCount;
    }
    public void setTotalSubmittedCount(int totalSubmittedCount) {
        this.totalSubmittedCount = totalSubmittedCount;
    }
    public int getTotalApprovedCount() {
        return totalApprovedCount;
    }
    public void setTotalApprovedCount(int totalApprovedCount) {
        this.totalApprovedCount = totalApprovedCount;
    }
    public Double getTotalApprovedAmount() {
        return totalApprovedAmount;
    }
    public void setTotalApprovedAmount(Double totalApprovedAmount) {
        this.totalApprovedAmount = totalApprovedAmount;
    }

    

}
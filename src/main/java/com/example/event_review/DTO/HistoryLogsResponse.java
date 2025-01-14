
package com.example.event_review.DTO;

import java.util.List;
import java.util.Map;

public class HistoryLogsResponse {
    private List<Map<String, YearlyStatsDTO>> historylogs;

    // Constructor
    public HistoryLogsResponse(List<Map<String, YearlyStatsDTO>> historylogs) {
        this.historylogs = historylogs;
    }

    // Getter and Setter
    public List<Map<String, YearlyStatsDTO>> getHistorylogs() {
        return historylogs;
    }

    public void setHistorylogs(List<Map<String, YearlyStatsDTO>> historylogs) {
        this.historylogs = historylogs;
    }
}

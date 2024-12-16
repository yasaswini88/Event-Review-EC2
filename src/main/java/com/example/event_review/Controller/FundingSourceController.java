// Controller Layer: FundingSourceController
package com.example.event_review.Controller;

import com.example.event_review.DTO.FundingSourceDTO;
import com.example.event_review.Service.FundingSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/funding-sources")
@CrossOrigin(origins = "*")
public class FundingSourceController {
    
    @Autowired
    private FundingSourceService fundingSourceService;
    
    @GetMapping
    public List<FundingSourceDTO> getAllFundingSources() {
        return fundingSourceService.getAllFundingSources();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FundingSourceDTO> getFundingSourceById(@PathVariable Long id) {
        return fundingSourceService.getFundingSourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<FundingSourceDTO> addFundingSource(@RequestBody FundingSourceDTO fundingSourceDTO) {
        FundingSourceDTO createdFundingSource = fundingSourceService.addFundingSource(fundingSourceDTO);
        return ResponseEntity.ok(createdFundingSource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FundingSourceDTO> updateFundingSource(
            @PathVariable Long id,
            @RequestBody FundingSourceDTO fundingSourceDTO) {
        FundingSourceDTO updatedFundingSource = fundingSourceService.updateFundingSource(id, fundingSourceDTO);
        return updatedFundingSource != null ?
                ResponseEntity.ok(updatedFundingSource) :
                ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFundingSource(@PathVariable Long id) {
        fundingSourceService.deleteFundingSource(id);
        return ResponseEntity.noContent().build();
    }
}
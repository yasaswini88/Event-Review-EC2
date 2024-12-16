package com.example.event_review.Service;

import com.example.event_review.DTO.FundingSourceDTO;
import com.example.event_review.Entity.FundingSource;
import com.example.event_review.Repo.FundingSourceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FundingSourceService {
    
    @Autowired
    private FundingSourceRepo fundingSourceRepo;
    
    public List<FundingSourceDTO> getAllFundingSources() {
        return fundingSourceRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<FundingSourceDTO> getFundingSourceById(Long id) {
        return fundingSourceRepo.findById(id)
                .map(this::convertToDTO);
    }
    
    public FundingSourceDTO addFundingSource(FundingSourceDTO fundingSourceDTO) {
        FundingSource fundingSource = convertToEntity(fundingSourceDTO);
        FundingSource savedFundingSource = fundingSourceRepo.save(fundingSource);
        return convertToDTO(savedFundingSource);
    }
    
    public FundingSourceDTO updateFundingSource(Long id, FundingSourceDTO fundingSourceDTO) {
        Optional<FundingSource> existingSource = fundingSourceRepo.findById(id);
        if (existingSource.isPresent()) {
            FundingSource fundingSource = existingSource.get();
            fundingSource.setSourceName(fundingSourceDTO.getSourceName());
            return convertToDTO(fundingSourceRepo.save(fundingSource));
        }
        return null;
    }
    
    public void deleteFundingSource(Long id) {
        fundingSourceRepo.deleteById(id);
    }
    
    private FundingSourceDTO convertToDTO(FundingSource fundingSource) {
        FundingSourceDTO dto = new FundingSourceDTO();
        dto.setSourceId(fundingSource.getSourceId());
        dto.setSourceName(fundingSource.getSourceName());
        return dto;
    }
    
    private FundingSource convertToEntity(FundingSourceDTO dto) {
        FundingSource fundingSource = new FundingSource();
        fundingSource.setSourceId(dto.getSourceId());
        fundingSource.setSourceName(dto.getSourceName());
        return fundingSource;
    }
}
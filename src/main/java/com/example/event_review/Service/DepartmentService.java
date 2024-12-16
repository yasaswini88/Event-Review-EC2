package com.example.event_review.Service;

import com.example.event_review.DTO.DepartmentDTO;
import com.example.event_review.Entity.Department;
import com.example.event_review.Repo.DepartmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    
    @Autowired
    private DepartmentRepo departmentRepo;
    
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<DepartmentDTO> getDepartmentById(Long id) {
        return departmentRepo.findById(id)
                .map(this::convertToDTO);
    }
    
    public DepartmentDTO addDepartment(DepartmentDTO departmentDTO) {
        Department department = convertToEntity(departmentDTO);
        Department savedDepartment = departmentRepo.save(department);
        return convertToDTO(savedDepartment);
    }
    
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Optional<Department> existingDept = departmentRepo.findById(id);
        if (existingDept.isPresent()) {
            Department department = existingDept.get();
            department.setDeptName(departmentDTO.getDeptName());
            return convertToDTO(departmentRepo.save(department));
        }
        return null;
    }
    
    public void deleteDepartment(Long id) {
        departmentRepo.deleteById(id);
    }
    
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setDeptId(department.getDeptId());
        dto.setDeptName(department.getDeptName());
        return dto;
    }
    
    private Department convertToEntity(DepartmentDTO dto) {
        Department department = new Department();
        department.setDeptId(dto.getDeptId());
        department.setDeptName(dto.getDeptName());
        return department;
    }
}


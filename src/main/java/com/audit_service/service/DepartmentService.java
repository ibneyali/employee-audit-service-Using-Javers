package com.audit_service.service;

import com.audit_service.model.Department;
import com.audit_service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final Javers javers;

    public List<Department> getAllDepartments() {
        return (List<Department>) departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    @Transactional
    public Department createDepartment(Department department) {
        department.setCreatedTimestamp(LocalDateTime.now());
        department.setUpdatedTimestamp(LocalDateTime.now());
        if (department.getUpdatedBy() == null) {
            department.setUpdatedBy("SYSTEM");
        }

        Department saved = departmentRepository.save(department);

        // Commit to JaVers
        javers.commit(saved.getUpdatedBy(), saved);

        return saved;
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        department.setName(departmentDetails.getName());
        department.setUpdatedTimestamp(LocalDateTime.now());
        department.setUpdatedBy(departmentDetails.getUpdatedBy() != null ? departmentDetails.getUpdatedBy() : "SYSTEM");

        Department saved = departmentRepository.save(department);

        // Commit to JaVers
        javers.commit(saved.getUpdatedBy(), saved);

        return saved;
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        String initiator = department.getUpdatedBy() != null ? department.getUpdatedBy() : "SYSTEM";

        // Commit deletion to JaVers before deleting
        javers.commitShallowDelete(initiator, department);

        departmentRepository.delete(department);
    }
}

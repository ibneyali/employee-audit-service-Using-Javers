package com.audit_service.service;

import com.audit_service.model.EmployeeTraining;
import com.audit_service.repository.EmployeeTrainingRepository;
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
public class EmployeeTrainingService {

    private final EmployeeTrainingRepository employeeTrainingRepository;
    private final Javers javers;

    public List<EmployeeTraining> getAllEmployeeTrainings() {
        return (List<EmployeeTraining>) employeeTrainingRepository.findAll();
    }

    public Optional<EmployeeTraining> getEmployeeTrainingById(Long id) {
        return employeeTrainingRepository.findById(id);
    }

    public List<EmployeeTraining> getTrainingsByEmployee(Long employeeId) {
        return employeeTrainingRepository.findByEmpId(employeeId);
    }

    public List<EmployeeTraining> getEmployeesByTraining(Long trainingId) {
        return employeeTrainingRepository.findByTrainingId(trainingId);
    }

    public List<EmployeeTraining> getEmployeeTrainingsByStatus(Long employeeId, String status) {
        return employeeTrainingRepository.findByEmpIdAndStatus(employeeId, status);
    }

    public List<EmployeeTraining> getTrainingEmployeesByStatus(Long trainingId, String status) {
        return employeeTrainingRepository.findByTrainingIdAndStatus(trainingId, status);
    }

    @Transactional
    public EmployeeTraining assignTraining(EmployeeTraining employeeTraining) {
        employeeTraining.setCreatedTimestamp(LocalDateTime.now());
        employeeTraining.setUpdatedTimestamp(LocalDateTime.now());
        if (employeeTraining.getUpdatedBy() == null) {
            employeeTraining.setUpdatedBy("SYSTEM");
        }

        EmployeeTraining saved = employeeTrainingRepository.save(employeeTraining);

        // Commit to JaVers
        javers.commit(saved.getUpdatedBy(), saved);

        return saved;
    }

    @Transactional
    public EmployeeTraining updateTrainingStatus(Long id, String status, String updatedBy) {
        EmployeeTraining employeeTraining = employeeTrainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Training not found with id: " + id));

        employeeTraining.setStatus(status);
        employeeTraining.setUpdatedTimestamp(LocalDateTime.now());
        employeeTraining.setUpdatedBy(updatedBy != null ? updatedBy : "SYSTEM");

        EmployeeTraining saved = employeeTrainingRepository.save(employeeTraining);

        // Commit to JaVers
        javers.commit(saved.getUpdatedBy(), saved);

        return saved;
    }

    @Transactional
    public void removeTrainingAssignment(Long id) {
        EmployeeTraining employeeTraining = employeeTrainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Training not found with id: " + id));

        String initiator = employeeTraining.getUpdatedBy() != null ? employeeTraining.getUpdatedBy() : "SYSTEM";

        // Commit deletion to JaVers before deleting
        javers.commitShallowDelete(initiator, employeeTraining);

        employeeTrainingRepository.delete(employeeTraining);
    }
}

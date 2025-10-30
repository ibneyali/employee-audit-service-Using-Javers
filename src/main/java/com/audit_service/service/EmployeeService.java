package com.audit_service.service;

import com.audit_service.exception.EmployeeNotFoundException;
import com.audit_service.model.Employee;
import com.audit_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;
    private final Javers javers;

    public List<Employee> getAllEmployees() {
        return (List<Employee>) employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining(name);
    }

    public List<Employee> getEmployeesByTraining(Long trainingId) {
        return employeeRepository.findByTrainingId(trainingId);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        // Set timestamps and defaults
        employee.setCreatedTimestamp(LocalDateTime.now());
        employee.setUpdatedTimestamp(LocalDateTime.now());
        if (employee.getUpdatedBy() == null) {
            employee.setUpdatedBy("SYSTEM");
        }

        Employee saved = employeeRepository.save(employee);

        // Commit to JaVers
        javers.commit(saved.getUpdatedBy(), saved);

        // Also log to custom audit service
        auditService.logEmployeeCreated(saved, saved.getUpdatedBy());

        return saved;
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Store old state for audit
        Employee oldState = Employee.builder()
                .id(existing.getId())
                .firstName(existing.getFirstName())
                .lastName(existing.getLastName())
                .email(existing.getEmail())
                .phone(existing.getPhone())
                .departmentId(existing.getDepartmentId())
                .addressId(existing.getAddressId())
                .createdTimestamp(existing.getCreatedTimestamp())
                .updatedTimestamp(existing.getUpdatedTimestamp())
                .updatedBy(existing.getUpdatedBy())
                .version(existing.getVersion())
                .build();

        // Update fields
        existing.setFirstName(updatedEmployee.getFirstName());
        existing.setLastName(updatedEmployee.getLastName());
        existing.setEmail(updatedEmployee.getEmail());
        existing.setPhone(updatedEmployee.getPhone());
        existing.setDepartmentId(updatedEmployee.getDepartmentId());
        existing.setAddressId(updatedEmployee.getAddressId());
        existing.setUpdatedTimestamp(LocalDateTime.now());
        existing.setUpdatedBy(updatedEmployee.getUpdatedBy() != null ? updatedEmployee.getUpdatedBy() : "SYSTEM");

        Employee saved = employeeRepository.save(existing);

        // Commit to JaVers
        javers.commit(saved.getUpdatedBy(), saved);

        // Also log to custom audit service
        auditService.logEmployeeUpdated(oldState, saved, saved.getUpdatedBy());

        return saved;
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Create a snapshot before deletion for audit
        Employee employeeSnapshot = Employee.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .departmentId(employee.getDepartmentId())
                .addressId(employee.getAddressId())
                .createdTimestamp(employee.getCreatedTimestamp())
                .updatedTimestamp(employee.getUpdatedTimestamp())
                .updatedBy(employee.getUpdatedBy())
                .version(employee.getVersion())
                .build();

        // Delete employee
        employeeRepository.delete(employee);

        // Log audit event for deletion
        String initiator = employee.getUpdatedBy() != null ? employee.getUpdatedBy() : "SYSTEM";
        auditService.logEmployeeDeleted(employeeSnapshot, initiator);
    }

    public String getEmployeeChanges(Long employeeId) {
        List<Change> changes = javers.findChanges(
                QueryBuilder.byInstanceId(employeeId, Employee.class).build()
        );
        return javers.getJsonConverter().toJson(changes);
    }

    public String compareEmployees(Employee oldEmployee, Employee newEmployee) {
        Diff diff = javers.compare(oldEmployee, newEmployee);
        return javers.getJsonConverter().toJson(diff);
    }
}

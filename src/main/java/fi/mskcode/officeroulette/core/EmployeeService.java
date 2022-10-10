package fi.mskcode.officeroulette.core;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public Employee addNewEmployee(String firstName, String lastName, Instant employmentStartTime) {
        var employeeId = UUID.randomUUID();
        var status = Employee.Status.ACTIVE;
        return employeeDao.insertEmployee(employeeId, firstName, lastName, employmentStartTime, Employee.Status.ACTIVE);
    }

    public Optional<Employee> findEmployeeById(UUID employeeId) {
        return employeeDao.findEmployeeById(employeeId);
    }
}

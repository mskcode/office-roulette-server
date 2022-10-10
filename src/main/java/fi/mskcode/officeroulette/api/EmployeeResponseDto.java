package fi.mskcode.officeroulette.api;

import fi.mskcode.officeroulette.core.Employee;

public record EmployeeResponseDto(
        String id, String firstName, String lastName, String employmentStartTime, String status) {

    public static EmployeeResponseDto from(Employee employee) {
        return new EmployeeResponseDto(
                employee.id().toString(),
                employee.firstName(),
                employee.lastName(),
                employee.employmentStartTime().toString(),
                employee.status().name());
    }
}

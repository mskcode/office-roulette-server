package fi.mskcode.officeroulette.api;

import static fi.mskcode.officeroulette.util.DateTimeUtil.formatAsIso8601;

import fi.mskcode.officeroulette.core.Employee;

public record EmployeeResponseDto(
        String id, String firstName, String lastName, String employmentStartTime, String status) {

    public static EmployeeResponseDto from(Employee employee) {
        return new EmployeeResponseDto(
                employee.id().toString(),
                employee.firstName(),
                employee.lastName(),
                formatAsIso8601(employee.employmentStartTime()),
                employee.status().name());
    }
}

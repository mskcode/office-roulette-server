package fi.mskcode.officeroulette.api;

import static com.google.common.collect.ImmutableList.toImmutableList;

import fi.mskcode.officeroulette.core.Employee;
import java.util.List;

public record EmployeesResponseDto(List<EmployeeResponseDto> employees) {

    public static EmployeesResponseDto from(List<Employee> employees) {
        var list = employees.stream().map(EmployeeResponseDto::from).collect(toImmutableList());
        return new EmployeesResponseDto(list);
    }
}

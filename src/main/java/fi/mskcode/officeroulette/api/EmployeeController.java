package fi.mskcode.officeroulette.api;

import static java.lang.String.format;

import fi.mskcode.officeroulette.core.EmployeeService;
import fi.mskcode.officeroulette.error.NotImplementedException;
import fi.mskcode.officeroulette.error.ResourceNotFound;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public EmployeesResponseDto getAllEmployees() {
        throw new NotImplementedException();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EmployeeResponseDto getSingleEmployee(@PathVariable("id") String id) {
        var employee = employeeService
                .findEmployeeById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFound(format("Employee ID %s does not exist", id)));
        return EmployeeResponseDto.from(employee);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public EmployeeResponseDto createNewEmployee() {
        throw new NotImplementedException();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public EmployeeResponseDto updateEmployee() {
        throw new NotImplementedException();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public EmployeeResponseDto deleteEmployee() {
        // TODO should not actually delete but passivate employee
        throw new NotImplementedException();
    }
}

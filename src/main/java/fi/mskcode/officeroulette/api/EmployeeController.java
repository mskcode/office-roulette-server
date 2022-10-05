package fi.mskcode.officeroulette.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public EmployeesDto getAllEmployees() {
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EmployeeDto getSingleEmployee() {
        return null;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public EmployeeDto createNewEmployee() {
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public EmployeeDto updateEmployee() {
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public EmployeeDto deleteEmployee() {
        return null;
    }
}

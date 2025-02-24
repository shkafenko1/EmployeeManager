package by.koronatech.office.api.controller;

import by.koronatech.office.api.dto.CreateEmployeeDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.service.EmployeeService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public EmployeeDto createEmployee(@RequestBody CreateEmployeeDto employeeDto) {
        return employeeService.createEmployee(employeeDto);
    }

    @GetMapping(params = "department")
    public List<EmployeeDto> findAllEmployeesByDepartment(
            @RequestParam("department") String department) {
        return employeeService.findAllEmployeesByDepartment(department);
    }

    @PatchMapping({"/{id}"})
    public EmployeeDto setManagerEmployee(@PathVariable int id) {
        return employeeService.setManagerEmployee(id);
    }

    @PutMapping("/{id}")
    public EmployeeDto updateEmployee(@PathVariable int id, @RequestBody EmployeeDto employeeDto) {
        return employeeService.updateEmployee(id, employeeDto);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/{id}")
    public EmployeeDto findEmployeeById(@PathVariable int id) {
        return employeeService.findEmployeeById(id);
    }
}
package by.koronatech.office.api.controller;

import by.koronatech.office.api.dto.CompanyDto;
import by.koronatech.office.api.dto.EmployeeDto;
import by.koronatech.office.core.service.CompanyService;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public List<CompanyDto> getAllCompanies() {
        return companyService.getAllCompanies();
    }
    
    @GetMapping("/{id}")
    public CompanyDto getCompany(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    @PostMapping
    public CompanyDto createCompany(@RequestBody CompanyDto companyDto) {
        return companyService.createCompany(companyDto);
    }

    @PutMapping("/{id}")
    public CompanyDto updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        return companyService.updateCompany(id, companyDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }

    @GetMapping("/{companyId}/employees")
    public List<EmployeeDto> getEmployeesByDepartment(
            @PathVariable Long companyId,
            @RequestParam String departmentName) {
        return companyService.findEmployeesByDepartment(companyId, departmentName);
    }

    @GetMapping("/high-salary")
    public List<CompanyDto> getCompaniesWithHighSalaryEmployees(
            @RequestParam BigDecimal salary) {
        return companyService.findCompaniesWithHighSalaryEmployeesNative(salary);
    }
}

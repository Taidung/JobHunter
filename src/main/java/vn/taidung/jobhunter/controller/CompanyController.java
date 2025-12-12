package vn.taidung.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.taidung.jobhunter.domain.Company;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.service.CompanyService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("create a new company")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company requestCompany) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(requestCompany));
    }

    @GetMapping("/companies")
    @ApiMessage("fetch companies")
    public ResponseEntity<ResultPaginationDTO> getCompany(
            @Filter Specification<Company> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.companyService.fetchCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("fetch companies")
    public ResponseEntity<Company> getCompanyById(
            @PathVariable("id") long id) {

        return ResponseEntity.ok(this.companyService.fetchCompanyById(id));
    }

    @PutMapping("/companies")
    @ApiMessage("update a company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company requestCompany) {
        return ResponseEntity.ok(companyService.handleUpdateCompany(requestCompany));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("delete a company")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

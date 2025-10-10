package vn.taidung.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Company;
import vn.taidung.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company c) {
        return this.companyRepository.save(c);
    }
}

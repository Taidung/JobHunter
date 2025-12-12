package vn.taidung.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Company;
import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.repository.CompanyRepository;
import vn.taidung.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        return companyOptional.isPresent() ? companyOptional.get() : null;
    }

    public ResultPaginationDTO fetchCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageCompany.getContent());

        return resultPaginationDTO;
    }

    public Company fetchCompanyById(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        return companyOptional.isPresent() ? companyOptional.get() : null;
    }

    public Company handleUpdateCompany(Company requestCompany) {
        Company currentCompany = this.fetchCompanyById(requestCompany.getId());
        if (currentCompany != null) {
            currentCompany.setName(requestCompany.getName());
            currentCompany.setDescription(requestCompany.getDescription());
            currentCompany.setAddress(requestCompany.getAddress());
            currentCompany.setLogo(requestCompany.getLogo());
            return this.companyRepository.save(currentCompany);
        }
        return null;

    }

    public void handleDeleteCompany(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();

            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}

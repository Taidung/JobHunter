package vn.taidung.jobhunter.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.taidung.jobhunter.domain.Job;
import vn.taidung.jobhunter.domain.Resume;
import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.domain.response.resume.CreateResumeDTO;
import vn.taidung.jobhunter.domain.response.resume.ResResumeDTO;
import vn.taidung.jobhunter.domain.response.resume.UpdateResumeDTO;
import vn.taidung.jobhunter.repository.ResumeRepository;
import vn.taidung.jobhunter.util.SecurityUtil;

@Service
public class ResumeService {

    // @Autowired
    // private FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;

    }

    public CreateResumeDTO createNewResume(Resume reqResume) {
        Resume resume = new Resume();

        resume.setEmail(reqResume.getEmail());
        resume.setUrl(reqResume.getUrl());
        resume.setStatus(reqResume.getStatus());
        // Gán User
        if (reqResume.getUser() != null && reqResume.getUser().getId() != null) {
            User user = new User();
            user.setId(reqResume.getUser().getId());
            resume.setUser(user);
        } else
            return null;

        // Gán Job
        if (reqResume.getJob() != null && reqResume.getJob().getId() != null) {
            Job job = new Job();
            job.setId(reqResume.getJob().getId());
            resume.setJob(job);
        } else
            return null;

        Resume savedResume = this.resumeRepository.save(resume);

        CreateResumeDTO resumeDTO = new CreateResumeDTO();
        resumeDTO.setId(savedResume.getId());
        resumeDTO.setCreatedAt(savedResume.getCreatedAt());
        resumeDTO.setCreatedBy(savedResume.getCreatedBy());

        return resumeDTO;
    }

    public UpdateResumeDTO handleUpdateResume(Resume reqResume) {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(reqResume.getId());
        if (resumeOptional.isPresent()) {
            UpdateResumeDTO res = new UpdateResumeDTO();
            Resume resume = resumeOptional.get();
            resume.setStatus(reqResume.getStatus());

            Resume savedResume = this.resumeRepository.save(resume);
            res.setUpdatedAt(savedResume.getUpdatedAt());
            res.setUpdatedBy(savedResume.getUpdatedBy());

            return res;

        }

        return null;
    }

    public ResResumeDTO getResumeById(Long id) {
        Optional<Resume> optionalResume = this.resumeRepository.findById(id);

        if (optionalResume.isEmpty()) {
            return null;
        }

        Resume resume = optionalResume.get();

        ResResumeDTO dto = new ResResumeDTO();
        dto.setId(resume.getId());
        dto.setEmail(resume.getEmail());
        dto.setUrl(resume.getUrl());
        dto.setStatus(resume.getStatus());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setCreatedBy(resume.getCreatedBy());
        dto.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            dto.setCompanyName(resume.getJob().getCompany().getName());
        }

        if (resume.getUser() != null) {
            dto.setUser(new ResResumeDTO.User(
                    resume.getUser().getId(),
                    resume.getUser().getName()));
        }

        if (resume.getJob() != null) {
            dto.setJob(new ResResumeDTO.Job(
                    resume.getJob().getId(),
                    resume.getJob().getName()));
        }

        return dto;
    }

    public ResultPaginationDTO fetchResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        // meta
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        // convert Resume -> ResResumeDTO
        List<ResResumeDTO> dtoList = pageResume.getContent().stream().map(resume -> {
            ResResumeDTO dto = new ResResumeDTO();
            dto.setId(resume.getId());
            dto.setEmail(resume.getEmail());
            dto.setUrl(resume.getUrl());
            dto.setStatus(resume.getStatus());
            dto.setCreatedAt(resume.getCreatedAt());
            dto.setUpdatedAt(resume.getUpdatedAt());
            dto.setCreatedBy(resume.getCreatedBy());
            dto.setUpdatedBy(resume.getUpdatedBy());

            if (resume.getJob() != null) {
                dto.setCompanyName(resume.getJob().getCompany().getName());
            }

            if (resume.getUser() != null) {
                dto.setUser(new ResResumeDTO.User(
                        resume.getUser().getId(),
                        resume.getUser().getName()));
            }

            if (resume.getJob() != null) {
                dto.setJob(new ResResumeDTO.Job(
                        resume.getJob().getId(),
                        resume.getJob().getName()));
            }

            return dto;
        }).toList();

        resultPaginationDTO.setResult(dtoList);

        return resultPaginationDTO;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        // Lấy tất cả resume và chuyển sang ResResumeDTO
        List<ResResumeDTO> resumes = pageResume.getContent().stream()
                .map(Resume::getId)
                .map(this::getResumeById) // gọi hàm format dto
                .filter(Objects::nonNull) // loại bỏ null nếu có
                .toList();

        rs.setResult(resumes);

        return rs;
    }

    public Boolean isIdExist(long id) {
        return this.resumeRepository.existsById(id);
    }

    public void detaleById(long id) {
        this.resumeRepository.deleteById(id);
    }
}

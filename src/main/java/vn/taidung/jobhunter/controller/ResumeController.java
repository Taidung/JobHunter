package vn.taidung.jobhunter.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.taidung.jobhunter.domain.Company;
import vn.taidung.jobhunter.domain.Job;
import vn.taidung.jobhunter.domain.Resume;
import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.domain.response.resume.CreateResumeDTO;
import vn.taidung.jobhunter.domain.response.resume.ResResumeDTO;
import vn.taidung.jobhunter.domain.response.resume.UpdateResumeDTO;
import vn.taidung.jobhunter.service.JobService;
import vn.taidung.jobhunter.service.ResumeService;
import vn.taidung.jobhunter.service.UserService;
import vn.taidung.jobhunter.util.SecurityUtil;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    @Autowired
    private FilterBuilder filterBuilder;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;

    public ResumeController(ResumeService resumeService,
            UserService userService,
            JobService jobService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<CreateResumeDTO> createNewResume(@Valid @RequestBody Resume resumeRequest)
            throws ResourceNotFoundException {

        if (resumeRequest.getUser() == null || resumeRequest.getUser().getId() == null ||
                resumeRequest.getJob() == null || resumeRequest.getJob().getId() == null ||
                !this.userService.isIdExist(resumeRequest.getUser().getId()) ||
                !this.jobService.isIdExist(resumeRequest.getJob().getId())) {
            throw new ResourceNotFoundException("User/ Job không tồn tại");
        }

        return ResponseEntity.ok(resumeService.createNewResume(resumeRequest));
    }

    @PutMapping("/resumes")
    @ApiMessage("update a resume")
    public ResponseEntity<UpdateResumeDTO> updateResume(@RequestBody Resume requestJob)
            throws ResourceNotFoundException {
        if (!this.resumeService.isIdExist(requestJob.getId())) {
            throw new ResourceNotFoundException(
                    "Id: " + requestJob.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.resumeService.handleUpdateResume(requestJob));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id)
            throws ResourceNotFoundException {
        if (!this.resumeService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }
        this.resumeService.detaleById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch resumes")
    public ResponseEntity<ResultPaginationDTO> getResumes(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok(this.resumeService.fetchResumes(finalSpec, pageable));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get resume by id")
    public ResponseEntity<ResResumeDTO> getResumeById(@PathVariable("id") Long id)
            throws ResourceNotFoundException {
        if (!this.resumeService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }

        ResResumeDTO dto = resumeService.getResumeById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("resumes/by-user")
    @ApiMessage("Get list resume by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(
            Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}

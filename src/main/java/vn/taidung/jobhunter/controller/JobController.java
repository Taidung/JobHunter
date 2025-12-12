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
import vn.taidung.jobhunter.domain.Job;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.taidung.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.taidung.jobhunter.service.JobService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a jobs")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job requestJob) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(requestJob));
    }

    @PutMapping("/jobs")
    @ApiMessage("update a jobs")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@RequestBody Job requestJob)
            throws ResourceNotFoundException {
        if (!this.jobService.isIdExist(requestJob.getId())) {
            throw new ResourceNotFoundException(
                    "Id: " + requestJob.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(jobService.handleUpdateJob(requestJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a jobs")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.jobService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }
        this.jobService.handleDeleteJobs(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("fetch a jobs")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.jobService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.jobService.fetchJobById(id));
    }

    @GetMapping("/jobs")
    @ApiMessage("fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.jobService.fetchAllJobs(spec, pageable));
    }
}

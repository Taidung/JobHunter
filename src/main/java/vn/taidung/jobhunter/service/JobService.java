package vn.taidung.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Job;
import vn.taidung.jobhunter.domain.Skill;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.taidung.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.taidung.jobhunter.repository.JobRepository;
import vn.taidung.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private JobRepository jobRepository;
    private SkillRepository skillRepository;

    public JobService(JobRepository jobRepository,
            SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO handleCreateJob(Job requestJob) {
        // lấy ra list id từ request
        List<Long> skillIds = requestJob.getSkills()
                .stream()
                .map(Skill::getId)
                .collect(Collectors.toList());

        List<Skill> skillsFromDb = skillRepository.findByIdIn(skillIds);
        requestJob.setSkills(skillsFromDb);

        Job savedJob = this.jobRepository.save(requestJob);

        // map sang list string
        List<String> skillNames = savedJob.getSkills()
                .stream()
                .map(Skill::getName)
                .collect(Collectors.toList());

        return new ResCreateJobDTO(
                savedJob.getId(),
                savedJob.getName(),
                savedJob.getLocation(),
                savedJob.getSalary(),
                savedJob.getQuantity(),
                savedJob.getLevel().toString(),
                savedJob.getStartDate(),
                savedJob.getEndDate(),
                skillNames,
                savedJob.getCreatedAt(),
                savedJob.getCreatedBy(),
                savedJob.isActive());
    }

    public ResUpdateJobDTO handleUpdateJob(Job requestJob) {
        Optional<Job> jobOptional = this.jobRepository.findById(requestJob.getId());

        if (jobOptional.isPresent()) {
            Job currentJob = jobOptional.get();

            // Cập nhật thông tin cơ bản
            currentJob.setName(requestJob.getName());
            currentJob.setLocation(requestJob.getLocation());
            currentJob.setSalary(requestJob.getSalary());
            currentJob.setQuantity(requestJob.getQuantity());
            currentJob.setLevel(requestJob.getLevel());
            currentJob.setStartDate(requestJob.getStartDate());
            currentJob.setEndDate(requestJob.getEndDate());
            currentJob.setActive(requestJob.isActive());

            // Cập nhật skills
            List<Long> skillIds = requestJob.getSkills()
                    .stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());
            List<Skill> skillsFromDb = skillRepository.findByIdIn(skillIds);
            currentJob.setSkills(skillsFromDb);

            // Lưu job
            Job savedJob = jobRepository.save(currentJob);

            // Map sang DTO
            List<String> skillNames = savedJob.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());

            return new ResUpdateJobDTO(
                    savedJob.getId(),
                    savedJob.getName(),
                    savedJob.getLocation(),
                    savedJob.getSalary(),
                    savedJob.getQuantity(),
                    savedJob.getLevel().toString(),
                    savedJob.getStartDate(),
                    savedJob.getEndDate(),
                    skillNames,
                    savedJob.getUpdatedAt(),
                    savedJob.getUpdatedBy(),
                    savedJob.isActive());
        } else
            return null;
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {

        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageJob.getNumber() + 1);
        meta.setPageSize(pageJob.getSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<Job> listJob = pageJob.getContent();
        rs.setResult(listJob);

        return rs;
    }

    public void handleDeleteJobs(long id) {
        this.jobRepository.deleteById(id);
    }

    public Boolean isIdExist(long id) {
        return this.jobRepository.existsById(id);
    }

    public Job fetchJobById(long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        return jobOptional.isPresent() ? jobOptional.get() : null;
    }
}

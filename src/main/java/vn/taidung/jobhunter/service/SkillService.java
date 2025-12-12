package vn.taidung.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Skill;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.repository.SkillRepository;

@Service
public class SkillService {

    private SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill requestSkill) {
        Optional<Skill> skillOptional = this.skillRepository.findById(requestSkill.getId());
        if (skillOptional.isPresent()) {
            Skill currentSkill = skillOptional.get();
            currentSkill.setName(requestSkill.getName());

            return this.skillRepository.save(currentSkill);
        }

        return null;
    }

    public Boolean existsBySkill(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Boolean existsById(long id) {
        return this.skillRepository.existsById(id);
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {

        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageSkill.getNumber() + 1);
        meta.setPageSize(pageSkill.getSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());

        return rs;
    }

    public void deleteSkill(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete
        currentSkill.getSubscribers().forEach(sub -> sub.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);
    }
}

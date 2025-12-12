package vn.taidung.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.taidung.jobhunter.domain.Job;
import java.util.List;
import vn.taidung.jobhunter.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Boolean existsById(long id);

    List<Job> findBySkillsIn(List<Skill> skills);
}

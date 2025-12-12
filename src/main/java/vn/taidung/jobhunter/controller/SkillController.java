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
import vn.taidung.jobhunter.domain.Skill;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.service.SkillService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill requestSkill)
            throws ResourceNotFoundException {

        if (this.skillService.existsBySkill(requestSkill.getName())) {
            throw new ResourceNotFoundException(
                    "Skill: " + requestSkill.getName() + " đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(requestSkill));
    }

    @PutMapping("/skills")
    @ApiMessage("update a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill requestSkill)
            throws ResourceNotFoundException {

        if (!this.skillService.existsById(requestSkill.getId())) {
            throw new ResourceNotFoundException(
                    "Id: " + requestSkill.getId() + " không tồn tại");
        }

        if (this.skillService.existsBySkill(requestSkill.getName())) {
            throw new ResourceNotFoundException(
                    "Skill: " + requestSkill.getName() + " đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.handleUpdateSkill(requestSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(
            @Filter Specification<Skill> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.skillService.fetchAllSkill(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.skillService.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok(null);
    }
}

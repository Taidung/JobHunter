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
import vn.taidung.jobhunter.domain.Role;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.service.RoleService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a roles")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role reqRole) throws ResourceNotFoundException {

        if (this.roleService.isExistsByName(reqRole.getName())) {
            throw new ResourceNotFoundException(
                    "Name đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.roleService.handleCreateRole(reqRole));
    }

    @PutMapping("/roles")
    @ApiMessage("update a roles")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role reqRole) throws ResourceNotFoundException {

        if (!this.roleService.isIdExist(reqRole.getId())) {
            throw new ResourceNotFoundException(
                    "Id không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.roleService.handleUpdateRole(reqRole));
    }

    @GetMapping("/roles")
    @ApiMessage("get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(@Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.roleService.fetchAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("get a role")
    public ResponseEntity<Role> getRole(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.roleService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id không tồn tại");
        }
        return ResponseEntity.ok(this.roleService.fetchById(id));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a roles")
    public ResponseEntity<Void> DeleteRole(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.roleService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id không tồn tại");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.ok(null);
    }
}

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
import vn.taidung.jobhunter.domain.Permission;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.service.PermissionService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission reqPermission)
            throws ResourceNotFoundException {
        Boolean isExists = this.permissionService.isApiExisting(reqPermission.getApiPath(),
                reqPermission.getMethod(),
                reqPermission.getModule());

        if (isExists) {
            throw new ResourceNotFoundException(
                    "Permission đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.handleCreatePermission(reqPermission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission reqPermission)
            throws ResourceNotFoundException {

        // Kiểm tra xem permission theo ID có tồn tại không
        Permission existingPermission = this.permissionService.findById(reqPermission.getId());

        String currentName = existingPermission.getName();
        String newName = reqPermission.getName();

        // Kiểm tra API path/method/module nếu tên không đổi
        if (newName.equals(currentName)) {
            Boolean isApiExists = this.permissionService.isApiExisting(
                    reqPermission.getApiPath(),
                    reqPermission.getMethod(),
                    reqPermission.getModule());

            if (isApiExists) {
                throw new ResourceNotFoundException("Permission với API path này đã tồn tại");
            }
        } else {
            // Tên đổi → bạn có thể bỏ qua check API, hoặc log nếu muốn
            if (this.permissionService.isNameExits(newName)) {
                throw new ResourceNotFoundException("Permission với tên này đã tồn tại");
            }
        }

        // Thực hiện update permission
        Permission updatedPermission = this.permissionService.handleUpdatePermission(reqPermission);

        return ResponseEntity.ok(updatedPermission);
    }

    @GetMapping("/permissions")
    @ApiMessage("get all permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.fetchAllPermissions(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> DeletePermission(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.permissionService.isIdExisting(id)) {
            throw new ResourceNotFoundException(
                    "Id không tồn tại");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok(null);
    }
}

package vn.taidung.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Permission;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission reqPermission) {
        return this.permissionRepository.save(reqPermission);
    }

    public Permission handleUpdatePermission(Permission reqPermission) {
        Permission permission = permissionRepository.findById(reqPermission.getId())
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setName(reqPermission.getName());
        permission.setApiPath(reqPermission.getApiPath());
        permission.setMethod(reqPermission.getMethod());
        permission.setModule(reqPermission.getModule());

        return this.permissionRepository.save(permission);
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pagePermission.getContent());

        return resultPaginationDTO;
    }

    public void deletePermission(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete
        this.permissionRepository.delete(currentPermission);
    }

    public boolean isApiExisting(String apiPath, String method, String module) {
        return this.permissionRepository.existsByApiPathAndMethodAndModule(apiPath, method, module);
    }

    public boolean isIdExisting(long id) {
        return this.permissionRepository.existsById(id);
    }

    public boolean isNameExits(String name) {
        return this.permissionRepository.existsByName(name);
    }

    public Permission findById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        return permissionOptional.isPresent() ? permissionOptional.get() : null;
    }
}

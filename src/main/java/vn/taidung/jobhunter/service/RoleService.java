package vn.taidung.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Permission;
import vn.taidung.jobhunter.domain.Role;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.repository.PermissionRepository;
import vn.taidung.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role handleCreateRole(Role reqRole) {

        if (reqRole.getPermissions() != null) {
            // lấy ra list id từ permission
            List<Long> permissionsIds = reqRole.getPermissions()
                    .stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());

            // check DB
            List<Permission> permissionsFromDb = permissionRepository.findByIdIn(permissionsIds);

            // save
            reqRole.setPermissions(permissionsFromDb);
        }

        return this.roleRepository.save(reqRole);
    }

    public Role handleUpdateRole(Role reqRole) {
        Role roleDB = this.roleRepository.findById(reqRole.getId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // ✅ Cập nhật permissions (nếu có truyền lên)
        if (reqRole.getPermissions() != null) {
            List<Long> permissionIds = reqRole.getPermissions()
                    .stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());

            // Lấy danh sách Permission hợp lệ từ DB
            List<Permission> permissionsFromDb = permissionRepository.findByIdIn(permissionIds);

            roleDB.setPermissions(permissionsFromDb);
        }

        if (reqRole.getName() != null && !reqRole.getName().isBlank()) {
            roleDB.setName(reqRole.getName());
        }

        if (reqRole.getDescription() != null) {
            roleDB.setDescription(reqRole.getDescription());
        }

        roleDB.setActive(reqRole.isActive());

        return this.roleRepository.save(roleDB);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageRole.getContent());

        return resultPaginationDTO;
    }

    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public boolean isExistsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public boolean isIdExist(long id) {
        return this.roleRepository.existsById(id);
    }

    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        return roleOptional.isPresent() ? roleOptional.get() : null;
    }
}

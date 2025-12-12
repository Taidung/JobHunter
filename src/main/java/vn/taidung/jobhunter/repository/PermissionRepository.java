package vn.taidung.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.taidung.jobhunter.domain.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsByApiPathAndMethodAndModule(String apiPath, String method, String module);

    boolean existsById(Long id);

    boolean existsByName(String name);

    List<Permission> findByIdIn(List<Long> id);
}

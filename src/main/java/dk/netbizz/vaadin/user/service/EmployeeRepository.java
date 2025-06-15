package dk.netbizz.vaadin.user.service;

import dk.netbizz.vaadin.user.domain.ApplicationUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Transactional is default for all methods
@Repository
public interface EmployeeRepository extends ListCrudRepository<ApplicationUser, Integer> {

    @Query("select * from application_user where tenant_department_id = :tenantDepartmentId")
    List<ApplicationUser> findByTenantDepartmentId(int tenantDepartmentId);

    Optional<ApplicationUser> findByFullnameIgnoreCase(String name);

    @Query("select * from application_user order by id limit 1000")
    List<ApplicationUser> findFirst1000();

}

package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantDepartmentRepository extends ListCrudRepository<TenantDepartment, Integer> {
    List<TenantDepartment> findByTenantCompanyId(int tenantCompanyId);
}

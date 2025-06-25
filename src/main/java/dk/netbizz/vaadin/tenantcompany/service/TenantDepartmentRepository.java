package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantDepartmentRepository extends ListCrudRepository<TenantDepartment, Integer> {

    @Query("select * from tenant_department where tenant_company_id = :tenantCompanyId")
    List<TenantDepartment> findByTenantCompanyId(int tenantCompanyId);

}

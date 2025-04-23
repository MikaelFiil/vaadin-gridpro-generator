package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Transactional is default for all methods
@Repository
public interface TenantCompanyRepository extends ListCrudRepository<TenantCompany, Integer> {

    Optional<TenantCompany> findByCompanyNameIgnoreCase(String name);
}

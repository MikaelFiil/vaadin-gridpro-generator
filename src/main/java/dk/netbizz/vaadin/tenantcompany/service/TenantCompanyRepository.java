package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Transactional is default for all methods
@Repository
public interface TenantCompanyRepository extends ListCrudRepository<TenantCompany, Integer> {

    @Query("select * from tenant_company where company_name = :lowerCaseName")
    Optional<TenantCompany> findByCompanyNameIgnoreCase(String lowerCaseName);

    @Query("select * from tenant_company order by :orderBy limit :size offset :offset")
    List<TenantCompany> findAllFromQuery(String orderBy, Integer size, Integer offset);

    // @Query("select * from tenant_company where company_name like :where order by :orderby limit :limit offset :offset")                     // Where and Order may both be empty strings
    @Query("select * from tenant_company limit :limit offset :offset")                     // Where and Order may both be empty strings
    List<TenantCompany> findFromQueryParams(@Param("limit") int limit, @Param("offset") int offset);


}

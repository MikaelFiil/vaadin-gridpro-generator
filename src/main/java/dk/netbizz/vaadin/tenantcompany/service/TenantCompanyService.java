package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class TenantCompanyService {

    private final JdbcTemplate jdbcTemplate;

    public TenantCompanyService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional
    public void save(TenantCompany tenantCompany) {
        ServicePoint.servicePointInstance().getTenantCompanyRepository().save(tenantCompany);
    }

    @Transactional
    public TenantCompany findById(Integer id) {
        Optional<TenantCompany> optionalTenantCompany = ServicePoint.servicePointInstance().getTenantCompanyRepository().findById(id);
        if (optionalTenantCompany.isPresent()) {
            TenantCompany tenantCompany = optionalTenantCompany.get();
            tenantCompany.setDepartments(ServicePoint.servicePointInstance().getTenantDepartmentService().findByTenantCompanyId(tenantCompany.getId()));
            return tenantCompany;
        }
        return null;
    }


    @Transactional
    public List<TenantCompany> findAll() {
        List<TenantCompany> tenantCompanyList = ServicePoint.servicePointInstance().getTenantCompanyRepository().findAll();
        for (TenantCompany tenantCompany : tenantCompanyList) {
            tenantCompany.setDepartments(ServicePoint.servicePointInstance().getTenantDepartmentService().findByTenantCompanyId(tenantCompany.getId()));
        }
        return tenantCompanyList;
    }

    @Transactional
    public List<TenantCompany> findFromQuery(String where, String orderBy, int limit, int offset) {
        return jdbcTemplate.query("select * from tenant_company" + where + orderBy + " limit " + limit + " offset " + offset,
                new TenantCompanyMapper());
    }

    @Transactional
    public Integer countFromQueryFilter(String where) {
        return jdbcTemplate.queryForObject("select count(*) as rowCount from tenant_company" + where, null, Integer.class);
    }

    private static final class TenantCompanyMapper implements RowMapper<TenantCompany> {

        public TenantCompany mapRow(ResultSet rs, int rowNum) throws SQLException {
            TenantCompany tenantCompany = new TenantCompany();
            tenantCompany.setId(rs.getInt("id"));
            tenantCompany.setVersion(rs.getInt("version"));

            tenantCompany.setCompanyName(rs.getString("company_name"));
            tenantCompany.setAddressStreet(rs.getString("address_street"));
            tenantCompany.setAddressZipCity(rs.getString("address_zip_city"));
            return tenantCompany;
        }
    }

}

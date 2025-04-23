package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.exception.ApplicationRuntimeException;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TenantCompanyService {

    private static final String table = "tenant_company";
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private final TenantCompanyRepository tenantCompanyRepository;
    private final Clock clock;


    TenantCompanyService(DataSource dataSource, TenantCompanyRepository tenantCompanyRepository, Clock clock) {
        this.dataSource = dataSource;
        this.tenantCompanyRepository = tenantCompanyRepository;
        this.clock = clock;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // When utilizing multiple repository updates
    @Transactional
    public void doCompundTransaction() {
        for (TenantCompany company : tenantCompanyRepository.findAll()) {
            company.setAddressStreet("Elisevej 3");
            tenantCompanyRepository.save(company);
        }
    }

    @Transactional
    public void failCompundTransaction() {
        for (TenantCompany company : tenantCompanyRepository.findAll()) {
            company.setAddressStreet("Præstevanget 1");
            tenantCompanyRepository.save(company);
        }
        throw new ApplicationRuntimeException("Failing on purpose");
    }


    private static final class TenantCompanyMapper implements RowMapper<TenantCompany> {

        public TenantCompany mapRow(ResultSet rs, int rowNum) throws SQLException {
            TenantCompany tenantCompany = new TenantCompany();
            tenantCompany.setId(rs.getInt("id"));
            tenantCompany.setCompanyName(rs.getString("company_name"));
            tenantCompany.setAddressStreet(rs.getString("address_street"));
            tenantCompany.setAddressZipCity(rs.getString("address_zip_city"));
            return tenantCompany;
        }
    }

}

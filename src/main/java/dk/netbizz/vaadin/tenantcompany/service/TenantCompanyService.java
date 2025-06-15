package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TenantCompanyService {


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(TenantCompany tenantCompany) {
        ServicePoint.getInstance().getTenantCompanyRepository().save(tenantCompany);
    }

    @Transactional
    public TenantCompany findById(Integer id) {
        Optional<TenantCompany> optionalTenantCompany = ServicePoint.getInstance().getTenantCompanyRepository().findById(id);
        if (optionalTenantCompany.isPresent()) {
            TenantCompany tenantCompany = optionalTenantCompany.get();
            tenantCompany.setDepartments(ServicePoint.getInstance().getTenantDepartmentService().findByTenantCompanyId(tenantCompany.getId()));
            return tenantCompany;
        }
        return null;
    }


    @Transactional
    public List<TenantCompany> findAll() {
        List<TenantCompany> tenantCompanyList = ServicePoint.getInstance().getTenantCompanyRepository().findAll();
        for (TenantCompany tenantCompany : tenantCompanyList) {
            tenantCompany.setDepartments(ServicePoint.getInstance().getTenantDepartmentService().findByTenantCompanyId(tenantCompany.getId()));
        }
        return tenantCompanyList;
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

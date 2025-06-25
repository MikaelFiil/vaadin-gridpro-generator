package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class TenantDepartmentService {

    private final JdbcTemplate jdbcTemplate;

    public TenantDepartmentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void save(TenantDepartment tenantDepartment) {
        ServicePoint.servicePointInstance().getTenantDepartmentRepository().save(tenantDepartment);
    }


    @Transactional
    public TenantDepartment findById(Integer id) {
        TenantDepartment tenantDepartment = ServicePoint.servicePointInstance().getTenantDepartmentRepository().findById(id).orElse(null);
        if (tenantDepartment != null) {
            getEmployees(tenantDepartment);
        }
        return tenantDepartment;
    }


    @Transactional
    public List<TenantDepartment> findByTenantCompanyId(Integer id) {
        List<TenantDepartment> tenantDepartmentList = ServicePoint.servicePointInstance().getTenantDepartmentRepository().findByTenantCompanyId(id);
        for (TenantDepartment tenantDepartment : tenantDepartmentList) {
            getEmployees(tenantDepartment);
        }
        return tenantDepartmentList;
    }

    private void getEmployees(TenantDepartment tenantDepartment) {
        tenantDepartment.setEmployees(ServicePoint.servicePointInstance().getEmployeeService().findByTenantDepartmentId(tenantDepartment.getId()));
    }

    @Transactional
    public List<TenantDepartment> findFromQuery(String where, String orderBy, int limit, int offset) {
        return jdbcTemplate.query("select * from tenant_department" + where + orderBy + " limit " + limit + " offset " + offset,
                new TenantDepartmentService.TenantDepartmentMapper());
    }

    @Transactional
    public Integer countFromQueryFilter(String where) {
        return jdbcTemplate.queryForObject("select count(*) as rowCount from tenant_department" + where, null, Integer.class);
    }

    private static final class TenantDepartmentMapper implements RowMapper<TenantDepartment> {

        public TenantDepartment mapRow(ResultSet rs, int rowNum) throws SQLException {
            TenantDepartment tenantDepartment = new TenantDepartment();
            tenantDepartment.setId(rs.getInt("id"));
            tenantDepartment.setVersion(rs.getInt("version"));
            tenantDepartment.setTenantCompanyId(rs.getInt("tenant_company_id"));

            tenantDepartment.setDepartmentName(rs.getString("department_name"));
            tenantDepartment.setDescription(rs.getString("description"));
            return tenantDepartment;
        }
    }


}

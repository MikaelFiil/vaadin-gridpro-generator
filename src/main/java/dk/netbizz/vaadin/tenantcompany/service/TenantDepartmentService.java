package dk.netbizz.vaadin.tenantcompany.service;

import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TenantDepartmentService {

    @Transactional
    public void save(TenantDepartment tenantDepartment) {
        ServicePoint.getInstance().getTenantDepartmentRepository().save(tenantDepartment);
    }


    @Transactional
    public TenantDepartment findById(Integer id) {
        TenantDepartment tenantDepartment = ServicePoint.getInstance().getTenantDepartmentRepository().findById(id).orElse(null);
        if (tenantDepartment != null) {
            getEmployees(tenantDepartment);
        }
        return tenantDepartment;
    }


    @Transactional
    public List<TenantDepartment> findByTenantCompanyId(Integer id) {
        List<TenantDepartment> tenantDepartmentList = ServicePoint.getInstance().getTenantDepartmentRepository().findByTenantCompanyId(id);
        for (TenantDepartment tenantDepartment : tenantDepartmentList) {
            getEmployees(tenantDepartment);
        }
        return tenantDepartmentList;
    }

    private void getEmployees(TenantDepartment tenantDepartment) {
        tenantDepartment.setEmployees(ServicePoint.getInstance().getEmployeeService().findByTenantDepartmentId(tenantDepartment.getId()));
    }


}

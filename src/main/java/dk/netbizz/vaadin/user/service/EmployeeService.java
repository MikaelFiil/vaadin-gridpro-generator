package dk.netbizz.vaadin.user.service;

import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class EmployeeService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ApplicationUser applicationUser) {
        ServicePoint.getInstance().getEmployeeRepository().save(applicationUser);
    }

    @Transactional
    public ApplicationUser findById(Integer id) {
        ApplicationUser applicationUser = ServicePoint.getInstance().getEmployeeRepository().findById(id).orElse(null);
        getApplicationUserItems(applicationUser);
        return applicationUser;
    }


    @Transactional
    public List<ApplicationUser> findByTenantDepartmentId(Integer id) {
        List<ApplicationUser> applicationUserList =  ServicePoint.getInstance().getEmployeeRepository().findByTenantDepartmentId(id);
        for(ApplicationUser applicationUser : applicationUserList) {
            getApplicationUserItems(applicationUser);
        }
        return applicationUserList;
    }

    private void getApplicationUserItems(ApplicationUser user) {
        user.setItems(ServicePoint.getInstance().getItemRepository().findByApplicationUserId(user.getId()));
    }


}

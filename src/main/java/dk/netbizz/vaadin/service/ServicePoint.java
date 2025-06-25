package dk.netbizz.vaadin.service;


import dk.netbizz.vaadin.item.service.ItemRepository;
import dk.netbizz.vaadin.item.service.ItemService;
import dk.netbizz.vaadin.tenantcompany.service.TenantCompanyRepository;
import dk.netbizz.vaadin.tenantcompany.service.TenantCompanyService;
import dk.netbizz.vaadin.tenantcompany.service.TenantDepartmentRepository;
import dk.netbizz.vaadin.tenantcompany.service.TenantDepartmentService;
import dk.netbizz.vaadin.user.service.EmployeeRepository;
import dk.netbizz.vaadin.user.service.EmployeeService;
import dk.netbizz.vaadin.warehouse.service.WarehouseRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServicePoint {

    private static ServicePoint instance;

    @Autowired
    private TenantCompanyService tenantCompanyService;
    @Autowired
    private TenantCompanyRepository tenantCompanyRepository;
    @Autowired
    private TenantDepartmentService tenantDepartmentService;
    @Autowired
    private TenantDepartmentRepository tenantDepartmentRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;


    private ServicePoint() {
    }

    @Bean
    public static synchronized ServicePoint servicePointInstance() {
        if (instance == null) {
            instance = new ServicePoint();
        }
        return instance;
    }

}

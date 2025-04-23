package dk.netbizz.vaadin.service;


import dk.netbizz.vaadin.item.service.ItemRepository;
import dk.netbizz.vaadin.user.service.TenantDepartmentEmployeeRepository;
import dk.netbizz.vaadin.warehouse.service.WarehouseRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServiceAccessPoint {

    private static ServiceAccessPoint instance;

    @Autowired
    private dk.netbizz.vaadin.tenantcompany.service.TenantCompanyService tenantCompanyService;
    @Autowired
    private dk.netbizz.vaadin.tenantcompany.service.TenantCompanyRepository tenantCompanyRepository;
    @Autowired
    private dk.netbizz.vaadin.tenantcompany.service.TenantDepartmentRepository tenantDepartmentRepository;
    @Autowired
    private TenantDepartmentEmployeeRepository tenantDepartmentEmployeeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private ItemRepository itemRepository;



    private ServiceAccessPoint() {
    }

    @Bean
    public static synchronized ServiceAccessPoint getServiceAccessPointInstance() {
        if (instance == null) {
            instance = new ServiceAccessPoint();
        }
        return instance;
    }

}

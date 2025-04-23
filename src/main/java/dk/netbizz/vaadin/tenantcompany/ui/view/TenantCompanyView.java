package dk.netbizz.vaadin.tenantcompany.ui.view;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.MainLayout;
import dk.netbizz.vaadin.item.ui.view.ItemGrid;
import dk.netbizz.vaadin.signal.Signal;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import dk.netbizz.vaadin.user.ui.view.TenantDepartmentEmployeeGrid;

// https://docs.spring.io/spring-data/jdbc/docs/2.4.15/reference/html/
// https://docs.spring.io/spring-framework/docs/4.3.25.RELEASE/spring-framework-reference/html/jdbc.html

@PageTitle("Company list")
@Menu(order = 4, icon = "vaadin:clipboard-check", title = "Companies")
@Route(value = "companies", layout = MainLayout.class)
public class TenantCompanyView extends Main implements Signal {

    HorizontalLayout horizontalLayout = new HorizontalLayout(new Span("10000 items =>"), new Span("Company 1050 - Department 33080 - Employee 6835990"), new Span("Company 1300 - Department 58100 - Employee 7086190"));

    VerticalLayout verticalLayout = new VerticalLayout();
    ItemGrid itemGrid = new ItemGrid(this);
    TenantDepartmentEmployeeGrid tenantDepartmentEmployeeGrid = new TenantDepartmentEmployeeGrid(this, itemGrid);
    TenantDepartmentGrid tenantDepartmentGrid = new TenantDepartmentGrid(this, tenantDepartmentEmployeeGrid);
    TenantCompanyGrid tenantCompanyGrid = new TenantCompanyGrid(tenantDepartmentGrid);

    Details departmentDetails = new Details("Departments");
    Details employeeDetails = new Details("Employees");
    Details itemDetails = new Details("Items");

    public TenantCompanyView() {
        setSizeFull();
        buildUI();
        buildUX();
        verticalLayout.setWidthFull();
        departmentDetails.setWidthFull();
        employeeDetails.setWidthFull();
        itemDetails.setWidthFull();

        add(verticalLayout);
        tenantCompanyGrid.refresh();
    }

    private void buildUI() {
        verticalLayout.add(horizontalLayout);
        verticalLayout.add(tenantCompanyGrid);
        employeeDetails.add(tenantDepartmentEmployeeGrid);
        departmentDetails.add(tenantDepartmentGrid);
        itemDetails.add(itemGrid);
        verticalLayout.add(departmentDetails);
        verticalLayout.add(employeeDetails);
        verticalLayout.add(itemDetails);
    }


    private void buildUX()  {

    }

    public void signal(String signalEvent, Object signal) {
        switch(signalEvent.toLowerCase()) {
            case "companyselected" -> {
                if (signal != null) {
                    departmentDetails.setSummaryText("Departments of " + ((TenantCompany) signal).getCompanyName());
                } else {
                    departmentDetails.setSummaryText("Departments");
                }
            }
            case "departmentselected" -> {
                if (signal != null) {
                    employeeDetails.setSummaryText("Employees of " + ((TenantDepartment) signal).getDepartmentName());
                } else {
                    employeeDetails.setSummaryText("Employees");
                }
            }
            case "employeeselected" -> {
                if (signal != null) {
                    itemDetails.setSummaryText("Items of " + ((ApplicationUser) signal).getFullname());
                } else {
                    itemDetails.setSummaryText("Items");
                }
            }
        }
    }


}
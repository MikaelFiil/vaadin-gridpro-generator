package dk.netbizz.vaadin.tenantcompany.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.MainLayout;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.item.ui.view.ItemGrid;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.signal.Signal;
import dk.netbizz.vaadin.signal.SignalType;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import dk.netbizz.vaadin.user.ui.view.EmployeeGrid;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.*;
import static java.lang.Math.random;

// https://docs.spring.io/spring-data/jdbc/docs/2.4.15/reference/html/
// https://docs.spring.io/spring-framework/docs/4.3.25.RELEASE/spring-framework-reference/html/jdbc.html

// http://localhost:8090/h2-console

@PageTitle("Load & performance test")
@Menu(order = 5, icon = "vaadin:clipboard-check", title = "Load & performance test")
@Route(value = "loadtest", layout = MainLayout.class)
public class TenantCompanyView extends Main implements Signal {

    List<String> catList = new ArrayList<>(List.of("Technical", "Quality", "Delivery", "Legal"));
    List<String> criticalList = new ArrayList<>(List.of("Low", "Medium", "High"));

    Button genDepartmentsButton = new Button("Generate departments");
    Button genEmployeesButton = new Button("Generate employees");
    Button genItemsButton = new Button("Generate 100 items for first 1000 users ");
    Button genItems10000Button = new Button("Generate 10000 items for user with id: ");
    IntegerField applicatioUserIdField = new IntegerField("Enter Employee id");


    VerticalLayout verticalLayout = new VerticalLayout();
    ItemGrid itemGrid = new ItemGrid(this);
    EmployeeGrid tenantDepartmentEmployeeGrid = new EmployeeGrid(this, itemGrid);
    TenantDepartmentGrid tenantDepartmentGrid = new TenantDepartmentGrid(this, tenantDepartmentEmployeeGrid);
    TenantCompanyGrid tenantCompanyGrid = new TenantCompanyGrid(this, tenantDepartmentGrid);

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
        itemDetails.setSizeFull();

        add(verticalLayout);
        tenantCompanyGrid.refresh();
    }

    private void buildUI() {
        HorizontalLayout horizontalLayout = new HorizontalLayout(genDepartmentsButton, genEmployeesButton, genItemsButton, genItems10000Button, applicatioUserIdField);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        verticalLayout.add(horizontalLayout);
        verticalLayout.add(tenantCompanyGrid);
        employeeDetails.add(tenantDepartmentEmployeeGrid);
        departmentDetails.add(tenantDepartmentGrid);
        itemGrid.setSizeFull();
        itemDetails.add(itemGrid);
        verticalLayout.add(departmentDetails);
        verticalLayout.add(employeeDetails);
        verticalLayout.add(itemDetails);
    }


    private void buildUX() {
        genDepartmentsButton.addClickListener(evt -> {
            Faker faker = new Faker();
            System.out.println("Generating departments ...");

            List<TenantCompany> tenantCompanyList = ServicePoint.getInstance().getTenantCompanyRepository().findAll();
            for(TenantCompany tenantCompany : tenantCompanyList) {

                for (int j = 0; j < 100; j++) {
                    String depName = faker.company().buzzword() + " department - " + round(random() * 10000) + " - " + round(random() * 10000);
                    depName = depName.substring(0, min(depName.length(), 50));
                    String desc = faker.famousLastWords().lastWords();
                    desc = desc.substring(0, min(desc.length(), 250));
                    TenantDepartment tenantDepartment = new TenantDepartment(depName, desc);
                    tenantDepartment.setTenantCompanyId(tenantCompany.getId());
                    ServicePoint.getInstance().getTenantDepartmentService().save(tenantDepartment);
                    tenantCompany.getDepartments().add(tenantDepartment);
                }
            }

            System.out.println("Finished Generating departments ...");
        });

        genEmployeesButton.addClickListener(evt -> {
            Faker faker = new Faker();
            System.out.println("Generating employees ...");

            List<TenantDepartment> tenantDepartments = ServicePoint.getInstance().getTenantDepartmentRepository().findAll();
            for (TenantDepartment tenantDepartment : tenantDepartments) {
                List<ApplicationUser> applicationUsers = new ArrayList<>();

                for (int j = 0; j < 10; j++) {
                    ApplicationUser applicationUser = new ApplicationUser();
                    applicationUser.setHashedPassword("$2a$10$7Uq5UoCp/txscI11yYpOkuqJ7ASjLEyjJZPsF25VVW9kTKoK2CaQS");
                    applicationUser.setTenantDepartmentId(tenantDepartment.getId());
                    applicationUser.setMustChangePwd(false);

                    String fullName = faker.funnyName().name() + " " + faker.name().lastName();
                    fullName = fullName.substring(0, min(fullName.length(), 50));
                    applicationUser.setFullname(fullName);

                    applicationUser.setBirthday(faker.timeAndDate().birthday());
                    applicationUser.setPhone(faker.phoneNumber().cellPhone());

                    String email = faker.funnyName().name() + round(random() * 10000) + "@" + faker.company().name() + "-" + round(random() * 10000) + ".com";
                    email = email.substring(0, min(email.length(), 100));
                    applicationUser.setEmail(email);
                    applicationUser.setEmailConfirmed(true);
                    applicationUser.setCreated(LocalDateTime.now());
                    applicationUser.setLastLogin(LocalDateTime.now());
                    applicationUser.setIsLocked(false);
                    applicationUser.setIsDisabled(false);
                    applicationUser.setDescription(faker.famousLastWords().lastWords());
                    applicationUsers.add(applicationUser);
                }
                ServicePoint.getInstance().getEmployeeRepository().saveAll(applicationUsers);
            }

            System.out.println("Finished Generating employees ...");
        });

        genItemsButton.addClickListener(evt -> {
            Faker faker = new Faker();
            Random rand = new Random();

            System.out.println("Generating items ...");
            List<ApplicationUser> applicationUserList = ServicePoint.getInstance().getEmployeeRepository().findFirst1000();
            for (ApplicationUser applicationUser : applicationUserList) {
                for(int i = 0; i < 100; i++) {
                    Item item = createItem(faker, applicationUser);
                    ServicePoint.getInstance().getItemRepository().save(item);
                }
            }
            System.out.println("Finished generating items");
        });

        genItems10000Button.addClickListener(evt -> {
            Faker faker = new Faker();
            Random rand = new Random();

            ApplicationUser applicationUser = ServicePoint.getInstance().getEmployeeRepository().findById(applicatioUserIdField.getValue()).orElse(null);
            if (applicationUser != null) {
                System.out.println("Generating items ...");
                for(int i = 0; i < 10000; i++) {
                    Item item = createItem(faker, applicationUser);
                    ServicePoint.getInstance().getItemRepository().save(item);
                }
                System.out.println("Finished generating items");
            } else {
                StandardNotifications.showTempWarningNotification("No user found with id: " + applicatioUserIdField.getValue());
            }
        });
    }


    private Item createItem(Faker faker, ApplicationUser applicationUser) {
        String description = """
                    <h3>Rich Text Editor Demo</h3><p><strong>HTML text format as description of an Item</strong> </p>
                    <ul><li><span style="background-color: rgb(187, 187, 187); color: rgb(0, 138, 0);">Green</span></li><li><span style="background-color: rgb(187, 187, 187); color: rgb(255, 255, 0);">Yellow</span></li><li><span style="background-color: rgb(187, 187, 187); color: rgb(230, 0, 0);">Red</span></li></ul>
                    <p style="text-align: center"><strong><u>Vaadin is fantastic</u></strong></p><p style="text-align: center"><a href="https://vaadin.com" rel="nofollow">https://vaadin.com</a></p>
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
                """;

        Random rand = new Random();

        Item item = new Item();
        item.setApplicationUserId(applicationUser.getId());
        item.setItemName(faker.funnyName().name());
        item.setActive(true);
        item.setDescription("");
        item.setPrice(0);
        item.setCategory(catList.get(rand.nextInt(4)));
        item.setKrPerLiter(BigDecimal.valueOf(random() * 14));
        item.setPrice(rand.nextInt(1200));
        item.setWarehouse(ServicePoint.getInstance().getWarehouseRepository().findAll().get(rand.nextInt(5)));
        item.setBirthday(faker.timeAndDate().birthday());
        item.setActive(true);
        item.setCriticality(criticalList.get(rand.nextInt(3)));
        item.setDescription(description.concat("<p><h3>" + faker.famousLastWords().lastWords() + " - " + faker.funnyName().name()) + "</h3></p>");
        Integer[] yearlyAmount =  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int j = 0; j < yearlyAmount.length; j++) {
            yearlyAmount[j] = rand.nextInt(10000);
        }
        item.setYearlyAmount(yearlyAmount);

        Integer[] impactAmount =  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int j = 0; j < impactAmount.length; j++) {
            impactAmount[j] = rand.nextInt(1000);
        }
        item.setImpactAmount(impactAmount);

        BigDecimal[] likelyhood =  {BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(0)};
        for (int j = 0; j < likelyhood.length; j++) {
            likelyhood[j] = BigDecimal.valueOf(random()*14.0);
        }
        item.setLikelihood(likelyhood);

        return item;
    }

    private void clearSubHeadings() {
        departmentDetails.setSummaryText("Departments");
        employeeDetails.setSummaryText("Employees");
        itemDetails.setSummaryText("Items");
    }

    // See https://vaadin.com/docs/latest/flow/advanced/server-push  Broadcaster
    public void signal(SignalType signalEvent, Object signal) {
        switch(signalEvent) {
            case DOMAIN_ROOT_SELECTED -> {
                departmentDetails.setSummaryText("Departments of " + ((TenantCompany) signal).getCompanyName());
                tenantDepartmentGrid.setTenantCompany((TenantCompany)signal);
            }
            case DOMAIN_SUB_NODE_SELECTED -> {
                if (signal instanceof TenantDepartment) {
                    employeeDetails.setSummaryText("Employees of " + ((TenantDepartment) signal).getDepartmentName());
                    tenantDepartmentEmployeeGrid.setTenantDepartment((TenantDepartment) signal);
                }
                if (signal instanceof ApplicationUser) {
                    itemDetails.setSummaryText("Items of " + ((ApplicationUser) signal).getFullname());
                    itemGrid.setTenantDepartmentEmployee(((ApplicationUser) signal));
                }
            }
        }
    }


}
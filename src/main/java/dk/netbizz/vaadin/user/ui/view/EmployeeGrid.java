package dk.netbizz.vaadin.user.ui.view;

import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.signals.Signal;
import com.vaadin.signals.ValueSignal;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.signal.domain.SignalHost;
import dk.netbizz.vaadin.user.domain.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmployeeGrid extends GenericGridProEditView<ApplicationUser> {

    private DataProvider<ApplicationUser, String> dataProvider;
    private Integer tenantDepartmentId = null;
    private TextField tfFullNameFilter;
    private TextField tfEmailFilter;
    private final SignalHost signalHost;


    public EmployeeGrid(SignalHost signalHost) {
        super(ApplicationUser.class);
        this.signalHost = signalHost;

        setSizeFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        setMaxGridHeight(10);
        genericGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addClassName("vaadin-grid-generator");

        dataProvider = DataProvider.fromFilteringCallbacks(
            query -> (tenantDepartmentId == null) ? new ArrayList<ApplicationUser>().stream() : ServicePoint.servicePointInstance().getEmployeeService().findFromQuery(createWhere(), createOrderBy(query.getSortOrders()), query.getLimit(), query.getOffset()).stream(),
            query -> (tenantDepartmentId == null) ? 0 : ServicePoint.servicePointInstance().getEmployeeService().countFromQueryFilter(createWhere())
        );

        setupGrid(makeParams());
        setupGridEventHandlers();
        genericGrid.setDataProvider(dataProvider);

        HeaderRow headerRow = genericGrid.appendHeaderRow();
        tfFullNameFilter = createSearchField("fullname",headerRow.getCell(genericGrid.getColumnByKey("fullname")));
        tfEmailFilter = createSearchField("email",headerRow.getCell(genericGrid.getColumnByKey("email")));

        ComponentEffect.effect(this, () -> {
            setTenantDepartmentId(signalHost.getSignal(SignalHost.DEPARTMENT_ID).value());
            Signal.runWithoutTransaction(() -> {
                signalHost.getSignal(SignalHost.EMPLOYEE_ID).value(null);
            });
        });
    }

    private ApplicationUser createEmptyEmployee(Integer tenantDepartmentId)  {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setTenantDepartmentId(tenantDepartmentId);
        applicationUser.setFullname("Enter a name ...");
        applicationUser.setCreated(LocalDateTime.now());
        applicationUser.setLastLogin(LocalDateTime.now());
        applicationUser.setDescription("");
        return applicationUser;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        params.put("id.readonly", "true");
        return params;
    }

    public void setTenantDepartmentId(Integer tenantDepartmentId) {
        this.tenantDepartmentId = tenantDepartmentId;
        refreshGrid();
    }

    private String createWhere() {
        StringBuilder where = new StringBuilder(" where tenant_department_id = " + tenantDepartmentId);
        where.append(tfFullNameFilter.getValue().isEmpty() ? "" : (" and " + "lower(fullname) like '%" + tfFullNameFilter.getValue().toLowerCase() + "%'"));
        where.append(tfEmailFilter.getValue().isEmpty() ? "" : (" and " + "lower(email) like '%" + tfEmailFilter.getValue().toLowerCase() + "%'"));
        return where.toString();
    }

    @Override
    protected boolean isEditableEntity(ApplicationUser entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }                               // TODO You may add new rows

    @Override
    protected boolean canDeleteEntities() { return true; }                         // You cannot delete entire row, but you may edit it or part of it

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        if (tenantDepartmentId != null) {
            ApplicationUser entity = createEmptyEmployee(tenantDepartmentId);
            saveEntity(entity);
            genericGrid.select(entity);
            signalHost.getSignal(SignalHost.EMPLOYEE_ID).value(entity.getId());
        }
        refreshGrid();
    }

    @Override
    protected void setValidationError(ApplicationUser entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(String classname, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected boolean validUpdate(ApplicationUser entity, String colName, Object  newColValue) {
        return true;
    }

    @Override
    protected void saveEntity(ApplicationUser entity) {
        ServicePoint.servicePointInstance().getEmployeeRepository().save( entity);
    }

    @Override
    protected void loadEntities() {
        System.out.println("loadEntities employee");
        dataProvider.refreshAll();
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(ApplicationUser entity) {
        ServicePoint.servicePointInstance().getEmployeeRepository().delete(entity);
        signalHost.getSignal(SignalHost.EMPLOYEE_ID).value(null);
    }

    @Override
    protected void selectEntity(ApplicationUser entity) {
        signalHost.getSignal(SignalHost.EMPLOYEE_ID).value(entity.getId());
    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(ApplicationUser item, String colName) { return ""; }


}

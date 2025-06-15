package dk.netbizz.vaadin.user.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.item.ui.view.ItemGrid;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.signal.Signal;
import dk.netbizz.vaadin.signal.SignalType;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import dk.netbizz.vaadin.user.domain.ApplicationUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeGrid extends GenericGridProEditView<ApplicationUser> {

    private Signal signal;
    private ItemGrid itemGrid;
    private TenantDepartment tenantDepartment = null;

    public EmployeeGrid(Signal signal, ItemGrid itemGrid) {
        super(ApplicationUser.class);
        this.signal = signal;
        this.itemGrid = itemGrid;

        setWidthFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        genericGrid.setHeight("300px");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addClassName("vaadin-grid-generator");

        setupGrid(makeParams());
        setupGridEventHandlers();
    }


    private ApplicationUser createEmptyEmployee(TenantDepartment tenantDepartment)  {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setTenantDepartmentId(tenantDepartment.getId());
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

    public void setTenantDepartment(TenantDepartment tenantDepartment) {
        this.tenantDepartment = tenantDepartment;
        refreshGrid();
        itemGrid.setTenantDepartmentEmployee(null);
    }


    @Override
    protected boolean isEditableEntity(ApplicationUser entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    @Override
    protected boolean canDeleteEntities() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        ApplicationUser item = null;
        if (tenantDepartment != null) {
            item = createEmptyEmployee(tenantDepartment);
            saveEntity(item);
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
        ServicePoint.getInstance().getEmployeeRepository().save( entity);
    }

    @Override
    protected List<ApplicationUser> loadEntities() {
        if ((tenantDepartment != null) && (tenantDepartment.getId() != null)) {
            return new ArrayList<>(tenantDepartment.getEmployees());
            // return ServiceAccessPoint.getServiceAccessPointInstance().getTenantDepartmentEmployeeRepository().findByTenantDepartmentId(tenantDepartment.getId());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(ApplicationUser entity) {
        ServicePoint.getInstance().getEmployeeRepository().delete(entity);
    }

    @Override
    protected void selectEntity(ApplicationUser entity) {
        signal.signal(SignalType.DOMAIN_SUB_NODE_SELECTED, entity);
        // itemGrid.setTenantDepartmentEmployee(entity);
    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(ApplicationUser item, String colName) { return ""; }


}

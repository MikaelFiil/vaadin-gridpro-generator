package dk.netbizz.vaadin.tenantcompany.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.signal.Signal;
import dk.netbizz.vaadin.signal.SignalType;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import dk.netbizz.vaadin.user.ui.view.EmployeeGrid;

import java.util.*;

public class TenantDepartmentGrid extends GenericGridProEditView<TenantDepartment> {

    private Signal signal;
    private TenantCompany tenantCompany = null;
    private EmployeeGrid tenantDepartmentEmployeeGrid;

    public TenantDepartmentGrid(Signal signal, EmployeeGrid tenantDepartmentEmployeeGrid) {
        super(TenantDepartment.class);
        this.signal = signal;
        this.tenantDepartmentEmployeeGrid = tenantDepartmentEmployeeGrid;

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


    private TenantDepartment createEmptyTenantDepartment(TenantCompany tenantCompany) {
        TenantDepartment tenantDepartment = new TenantDepartment();
        tenantDepartment.setTenantCompanyId(tenantCompany.getId());
        tenantDepartment.setDepartmentName("Enter a name ...");
        tenantDepartment.setDescription("");
        return tenantDepartment;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        params.put("id.readonly", "true");
        return params;
    }

    public void setTenantCompany(TenantCompany tenantCompany) {
        this.tenantCompany = tenantCompany;
        refreshGrid();
        tenantDepartmentEmployeeGrid.setTenantDepartment(null);
    }

    @Override
    protected boolean isEditableEntity(TenantDepartment entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    @Override
    protected boolean canDeleteEntities() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        TenantDepartment item = null;
        if (tenantCompany != null) {
            item = createEmptyTenantDepartment(tenantCompany);
            saveEntity(item);
        }
        refreshGrid();
    }

    @Override
    protected void setValidationError(TenantDepartment entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(String classname, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected boolean validUpdate(TenantDepartment entity, String colName, Object  newColValue) {
        return true;
    }

    @Override
    protected void saveEntity(TenantDepartment entity) {
        System.out.println("Saving TenantDepartment " + entity);
        ServicePoint.getInstance().getTenantDepartmentRepository().save( entity);
    }

    @Override
    protected List<TenantDepartment> loadEntities() {
        if ((tenantCompany != null) && (tenantCompany.getId() != null)) {
            return new ArrayList<>(tenantCompany.getDepartments());
            // return ServiceAccessPoint.getServiceAccessPointInstance().getTenantDepartmentRepository().findByTenantCompanyId(tenantCompany.getId());
        } else {
            // return Collections.EMPTY_SET;
            return new ArrayList<>();
        }
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(TenantDepartment entity) {
        ServicePoint.getInstance().getTenantDepartmentRepository().delete(entity);
    }

    @Override
    protected void selectEntity(TenantDepartment entity) {
        signal.signal(SignalType.DOMAIN_SUB_NODE_SELECTED, entity);
        // tenantDepartmentEmployeeGrid.setTenantDepartment(entity);
    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(TenantDepartment item, String colName) { return ""; }


}

package dk.netbizz.vaadin.tenantcompany.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.service.ServiceAccessPoint;
import dk.netbizz.vaadin.signal.Signal;
import dk.netbizz.vaadin.signal.SignalType;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenantCompanyGrid extends GenericGridProEditView<TenantCompany> {

    private Signal signal;
    private  TenantDepartmentGrid tenantDepartmentGrid;

    public TenantCompanyGrid(Signal signal, TenantDepartmentGrid tenantDepartmentGrid) {
        super(TenantCompany.class);
        this.signal = signal;
        this.tenantDepartmentGrid = tenantDepartmentGrid;
        setWidthFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        genericGrid.setHeight("300px");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        genericGrid.addClassName("vaadin-grid-generator");

        setupGrid(makeParams());
        setupGridEventHandlers();
    }

    private TenantCompany createEmptyTenantCompany() {
        TenantCompany tenantCompany = new TenantCompany();
        tenantCompany.setCompanyName("Enter a company name ...");
        tenantCompany.setAddressStreet("");
        tenantCompany.setAddressZipCity("");
        return tenantCompany;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        params.put("id.readonly", "true");
        return params;
    }


    public void refresh() {
        refreshGrid();
    }

    @Override
    protected boolean isEditableEntity(TenantCompany entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        TenantCompany item = null;
        item = createEmptyTenantCompany();
        saveEntity(item);
        signal.signal(SignalType.DOMAIN_ROOT_SELECTED, item);
        refreshGrid();
    }

    @Override
    protected void setValidationError(TenantCompany entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(String classname, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected boolean validUpdate(TenantCompany entity, String colName, Object  newColValue) {
        return true;
    }

    @Override
    protected void saveEntity(TenantCompany entity) {
        ServiceAccessPoint.getServiceAccessPointInstance().getTenantCompanyRepository().save( entity);
    }

    @Override
    protected List<TenantCompany> loadEntities() {
        List<TenantCompany> list =  ServiceAccessPoint.getServiceAccessPointInstance().getTenantCompanyRepository().findAll();          // TODO How about LAZY loading ???  - AND WHICH REPOS are actually USED !?!?
        return  list;
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(TenantCompany entity) {
        ServiceAccessPoint.getServiceAccessPointInstance().getTenantCompanyRepository().delete(entity);
    }

    @Override
    protected void selectEntity(TenantCompany entity) {
        signal.signal(SignalType.DOMAIN_ROOT_SELECTED, entity);
        // tenantDepartmentGrid.setTenantCompany(entity);
    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(TenantCompany item, String colName) { return ""; }


}

package dk.netbizz.vaadin.tenantcompany.ui.view;

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
import dk.netbizz.vaadin.tenantcompany.domain.TenantDepartment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TenantDepartmentGrid extends GenericGridProEditView<TenantDepartment> {


    private Integer tenantCompanyId = null;
    private DataProvider<TenantDepartment, String> dataProvider;
    private TextField tfDepartmentNameFilter;
    private TextField tfDescriptionFilter;

    private final SignalHost signalHost;

    public TenantDepartmentGrid(SignalHost signalHost) {
        super(TenantDepartment.class);
        this.signalHost = signalHost;

        setSizeFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        setMaxGridHeight(10);
        genericGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addClassName("vaadin-grid-generator");

        dataProvider = DataProvider.fromFilteringCallbacks(
                query -> (tenantCompanyId == null) ? new ArrayList<TenantDepartment>().stream() : ServicePoint.servicePointInstance().getTenantDepartmentService().findFromQuery(createWhere(), createOrderBy(query.getSortOrders()), query.getLimit(), query.getOffset()).stream(),
                query -> (tenantCompanyId == null) ? 0 : ServicePoint.servicePointInstance().getTenantDepartmentService().countFromQueryFilter(createWhere())
        );

        setupGrid(makeParams());
        setupGridEventHandlers();
        genericGrid.setDataProvider(dataProvider);

        HeaderRow headerRow = genericGrid.appendHeaderRow();
        tfDepartmentNameFilter = createSearchField("name", headerRow.getCell(genericGrid.getColumnByKey("departmentName")));
        tfDescriptionFilter = createSearchField("description", headerRow.getCell(genericGrid.getColumnByKey("description")));

        ComponentEffect.effect(this, () -> {
            setTenantCompanyId(signalHost.getSignal(SignalHost.COMPANY_ID).value());
            Signal.runWithoutTransaction(() -> {
                signalHost.getSignal(SignalHost.DEPARTMENT_ID).value(null);
            });
        });
    }

    private TenantDepartment createEmptyTenantDepartment(Integer tenantCompanyId) {
        TenantDepartment tenantDepartment = new TenantDepartment();
        tenantDepartment.setTenantCompanyId(tenantCompanyId);
        tenantDepartment.setDepartmentName("Enter a name ...");
        tenantDepartment.setDescription("");
        return tenantDepartment;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        params.put("id.readonly", "true");
        // params.put("readonly", "true");                         // Make entire grid readonly
        return params;
    }

    public void setTenantCompanyId(Integer tenantCompanyId) {
        this.tenantCompanyId = tenantCompanyId;
        refreshGrid();
    }

    private String createWhere() {
        StringBuilder where = new StringBuilder(" where tenant_company_id = " + tenantCompanyId);
        where.append(tfDepartmentNameFilter.getValue().isEmpty() ? "" : (" and " + "lower(department_name) like '%" + tfDepartmentNameFilter.getValue().toLowerCase() + "%'"));
        where.append(tfDescriptionFilter.getValue().isEmpty() ? "" : (" and " + "lower(description) like '%" + tfDescriptionFilter.getValue().toLowerCase() + "%'"));
        return where.toString();
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
        if (tenantCompanyId != null) {
            TenantDepartment entity = createEmptyTenantDepartment(tenantCompanyId);
            saveEntity(entity);
            genericGrid.select(entity);
            signalHost.getSignal(SignalHost.DEPARTMENT_ID).value(entity.getId());
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
        ServicePoint.servicePointInstance().getTenantDepartmentRepository().save( entity);
    }

    @Override
    protected void loadEntities() {
        System.out.println("loadEntities department");
        dataProvider.refreshAll();
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(TenantDepartment entity) {
        ServicePoint.servicePointInstance().getTenantDepartmentRepository().delete(entity);
        signalHost.getSignal(SignalHost.DEPARTMENT_ID).value(null);
    }

    @Override
    protected void selectEntity(TenantDepartment entity) {
        signalHost.getSignal(SignalHost.DEPARTMENT_ID).value(entity.getId());
    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(TenantDepartment entity, String colName) { return ""; }


}

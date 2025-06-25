package dk.netbizz.vaadin.tenantcompany.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;

import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.signal.domain.SignalHost;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To avoid loading too many rows at once we need pagination and lazy loading
 * Also we should have filtering applied to certain columns and it should work in combination with pagination
 *
 * Filtering done right:  https://github.com/mstahv/grid-filtering-example/blob/master/src/main/java/org/example/views/GridColumnFiltering.java
 */


public class TenantCompanyGrid extends GenericGridProEditView<TenantCompany> {

    private DataProvider<TenantCompany, String> dataProvider;
    private TextField tfCompanyNameFilter;
    private TextField tfAddressStreetFilter;
    private TextField tfAddressZipCityFilter;

    public TenantCompanyGrid() {
        super(TenantCompany.class);
        setWidthFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        genericGrid.setHeight("600px");
        genericGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        genericGrid.addClassName("vaadin-grid-generator");

        dataProvider = DataProvider.fromFilteringCallbacks(
            query -> ServicePoint.servicePointInstance().getTenantCompanyService().findFromQuery(createWhere(), createOrderBy(query.getSortOrders()), query.getLimit(), query.getOffset()).stream(),
            query -> ServicePoint.servicePointInstance().getTenantCompanyService().countFromQueryFilter(createWhere())
        );

        setupGrid(makeParams());
        setupGridEventHandlers();
        genericGrid.setDataProvider(dataProvider);

        HeaderRow headerRow = genericGrid.appendHeaderRow();
        tfCompanyNameFilter = createSearchField("name",headerRow.getCell(genericGrid.getColumnByKey("companyName")));
        tfAddressStreetFilter = createSearchField("Street",headerRow.getCell(genericGrid.getColumnByKey("addressStreet")));
        tfAddressZipCityFilter = createSearchField("City",headerRow.getCell(genericGrid.getColumnByKey("addressZipCity")));

        setMaxGridHeight(10);
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

    // This is entity/table specific so must go in each subclass of GenericGridProEditView
    private String createWhere() {
        StringBuilder where = new StringBuilder();
        where.append(tfCompanyNameFilter.getValue().isEmpty() ? "" : (" " + "lower(company_name) like '%" + tfCompanyNameFilter.getValue().toLowerCase() + "%'"));

        if (!tfAddressStreetFilter.getValue().isEmpty() && !where.isEmpty()) {
            where.append(" and ");
        }
        where.append(tfAddressStreetFilter.getValue().isEmpty() ? "" : (" " + "lower(address_street) like '%" + tfAddressStreetFilter.getValue().toLowerCase() + "%'"));

        if (!tfAddressZipCityFilter.getValue().isEmpty() && !where.isEmpty()) {
            where.append(" and ");
        }
        where.append(tfAddressZipCityFilter.getValue().isEmpty() ? "" : (" " + "lower(address_zip_city) like '%" + tfAddressZipCityFilter.getValue().toLowerCase() + "%'"));

        if (where.isEmpty()) {
            return "";
        }
        return " where " + where.toString();
    }

    public void refresh() {
        refreshGrid();
    }

    @Override
    protected boolean isEditableEntity(TenantCompany entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    @Override
    protected boolean canDeleteEntities() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        TenantCompany entity = createEmptyTenantCompany();
        saveEntity(entity);
        refreshGrid();
        genericGrid.select(entity);
        SignalHost.signalHostInstance().getSignal("companyId").value(entity.getId());
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
        ServicePoint.servicePointInstance().getTenantCompanyRepository().save( entity);
    }

    @Override
    protected void loadEntities() {
        dataProvider.refreshAll();
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(TenantCompany entity) {
        ServicePoint.servicePointInstance().getTenantCompanyRepository().delete(entity);
        SignalHost.signalHostInstance().getSignal("companyId").value(0);
    }

    @Override
    protected void selectEntity(TenantCompany entity) {
        SignalHost.signalHostInstance().getSignal("companyId").value(entity.getId());
    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(TenantCompany entity, String colName) { return ""; }


}

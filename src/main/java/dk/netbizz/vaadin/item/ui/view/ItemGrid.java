package dk.netbizz.vaadin.item.ui.view;

import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.signals.Signal;
import com.vaadin.signals.ValueSignal;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.signal.domain.SignalHost;
import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemGrid extends GenericGridProEditView<Item> {

    private Integer applicationUserId;
    private DataProvider<Item, String> dataProvider;
    private TextField tfItemNameFilter;
    private Select<String> tfCriticalFilter;
    private final SignalHost signalHost;
    private final ServicePoint servicePoint;

    public ItemGrid(SignalHost signalHost, ServicePoint servicePoint) {
        super(Item.class);
        this.signalHost = signalHost;
        this.servicePoint = servicePoint;

        setSizeFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        genericGrid.setHeight("700px");
        // setMaxGridHeight(25);
        genericGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addClassName("vaadin-grid-generator");

        dataProvider = DataProvider.fromFilteringCallbacks(
            query -> (applicationUserId == null) ? new ArrayList<Item>().stream() : ServicePoint.servicePointInstance().getItemService().findFromQuery(servicePoint.getWarehouseRepository().findAll(), createWhere(), createOrderBy(query.getSortOrders()), query.getLimit(), query.getOffset()).stream(),
            query -> (applicationUserId == null) ? 0 : ServicePoint.servicePointInstance().getItemService().countFromQueryFilter(createWhere())
        );

        setupGrid(makeParams());
        setupGridEventHandlers();
        genericGrid.setDataProvider(dataProvider);

        HeaderRow headerRow = genericGrid.appendHeaderRow();
        tfItemNameFilter = createSearchField("itemname",headerRow.getCell(genericGrid.getColumnByKey("itemName")));
        tfCriticalFilter = createSelectSearchField("critical",headerRow.getCell(genericGrid.getColumnByKey("criticality")), getItemsForSelect("criticality"));

        ComponentEffect.effect(this, () -> {
            setTenantDepartmentEmployee(signalHost.getSignal(SignalHost.EMPLOYEE_ID).value());
            Signal.runWithoutTransaction(() -> {
                signalHost.getSignal(SignalHost.ITEM_ID).value(null);
            });
        });
    }

    private Item createEmptyItem() {
        Item item = new Item();
        item.setApplicationUserId(applicationUserId);
        item.setItemName("Enter a name ...");
        item.setWarehouse(ServicePoint.servicePointInstance().getWarehouseRepository().findAll().getFirst());
        item.setActive(true);
        item.setDescription("");
        item.setPrice(0);
        item.setCategory((String)getItemsForSelect("category").getFirst());
        return item;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        params.put("id.readonly", "true");
        params.put("price.readonly", "true");
        // params.put("criticality.readonly", "true");
        params.put("yearlyAmount.arrayEndIdx", "3");            // Indexes are zero based
        params.put("yearlyAmount.header0", "Year 2024");
        params.put("yearlyAmount.header1", "Year 2025");
        params.put("yearlyAmount.header2", "Year 2026");
        params.put("yearlyAmount.header3", "Year 2027");

        params.put("impactAmount.arrayEndIdx", "2");            // Indexes are zero based
        params.put("impactAmount.header0", "Impact 1");
        params.put("impactAmount.header1", "Impact 2");
        params.put("impactAmount.header2", "Impact 3");

        params.put("likelihood.arrayEndIdx", "2");              // Indexes are zero based
        params.put("likelihood.header0", "likelihood 1");
        params.put("likelihood.header1", "likelihood 2");
        params.put("likelihood.header2", "likelihood 3");

        params.put("calculatedImpact.arrayEndIdx", "2");        // Indexes are zero based
        return params;
    }

    public void setTenantDepartmentEmployee(Integer applicationUserId) {
        this.applicationUserId = applicationUserId;
        refreshGrid();
    }

    private String createWhere() {
        StringBuilder where = new StringBuilder(" where application_user_id = " + applicationUserId);
        where.append(tfItemNameFilter.getValue().isEmpty() ? "" : (" and " + "lower(item_name) like '%" + tfItemNameFilter.getValue().toLowerCase() + "%'"));
        where.append(tfCriticalFilter.getValue() == null || tfCriticalFilter.getValue().isEmpty() ? "" : (" and " + "lower(criticality) like '%" + tfCriticalFilter.getValue().toLowerCase() + "%'"));
        return where.toString();
    }

    @Override
    protected boolean isEditableEntity(Item entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    @Override
    protected boolean canDeleteEntities() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        if (applicationUserId != null) {
            Item entity = createEmptyItem();
            saveEntity(entity);
            genericGrid.select(entity);
            signalHost.getSignal(SignalHost.ITEM_ID).value(entity.getId());
        }
        refreshGrid();
    }

    @Override
    protected void setValidationError(Item entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(String className, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected boolean validUpdate(Item entity, String colName, Object  newColValue) {
        return true;
    }

    @Override
    protected void saveEntity(Item entity) {
        ServicePoint.servicePointInstance().getItemService().save(entity);
    }

    @Override
    protected void loadEntities() {
        System.out.println("loadEntities item");
        dataProvider.refreshAll();
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(Item entity) {
        ServicePoint.servicePointInstance().getItemRepository().delete(entity);
        signalHost.getSignal(SignalHost.ITEM_ID).value(null);
    }

    @Override
    protected void selectEntity(Item entity) {
        signalHost.getSignal(SignalHost.ITEM_ID).value(entity.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <S>List<S> getItemsForSelect(String colName) {

        switch (colName.toLowerCase()) {
            case "category" -> {
                return (List<S>) new ArrayList<>(List.of("Technical", "Quality", "Delivery", "Legal"));
            }
            case "warehouse" -> {
                return (List<S>) servicePoint.getWarehouseRepository().findAll();
            }
            case "criticality" -> {
                return (List<S>) new ArrayList<>(List.of("", "Low", "Medium", "High"));
            }
            default -> { /* keep compiler happy */ }
        }

        return new ArrayList<>();
    }

    @Override
    protected String getFixedCalculatedText(Item item, String colName) { return ""; }


}

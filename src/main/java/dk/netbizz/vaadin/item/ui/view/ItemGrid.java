package dk.netbizz.vaadin.item.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.item.domain.Item;
import dk.netbizz.vaadin.service.ServiceAccessPoint;
import dk.netbizz.vaadin.signal.Signal;
import dk.netbizz.vaadin.user.domain.ApplicationUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGrid extends GenericGridProEditView<Item> {

    private Signal signal;
    private ApplicationUser applicationUser;


    public ItemGrid(Signal signal) {
        super(Item.class);
        this.signal = signal;

        setWidthFull();
        setMargin(false);
        setPadding(false);
        genericGrid.setWidth("100%");
        genericGrid.setHeight("300px");
        genericGrid.setEmptyStateText("No items found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        genericGrid.addClassName("vaadin-grid-generator");

        setupGrid(makeParams());
        setupGridEventHandlers();
    }

    private Item createEmptyItem() {
        Item item = new Item();
        item.setApplicationUserId(applicationUser.getId());
        item.setItemName("Enter a name ...");
        // item.setWarehouse(ServiceAccessPoint.getServiceAccessPointInstance().getWarehouseRepository().findAll().getFirst());
        item.setActive(true);
        item.setDescription("");
        item.setPrice(0);
        item.setCategory((String)getItemsForSelect("category").getFirst());
        return item;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        params.put("yearlyAmount.arrayEndIdx", "3");            // Indexes are zero based
        params.put("yearlyAmount.header0", "Year 2024");
        params.put("yearlyAmount.header1", "Year 2025");
        params.put("yearlyAmount.header2", "Year 2026");
        params.put("yearlyAmount.header3", "Year 2027");

        params.put("impactAmount.arrayEndIdx", "2");
        params.put("impactAmount.header0", "Impact 1");
        params.put("impactAmount.header1", "Impact 2");
        params.put("impactAmount.header2", "Impact 3");

        params.put("likelihood.arrayEndIdx", "2");
        params.put("likelihood.header0", "likelihood 1");
        params.put("likelihood.header1", "likelihood 2");
        params.put("likelihood.header2", "likelihood 3");

        params.put("calculatedImpact.arrayEndIdx", "2");
        return params;
    }


    public void setTenantDepartmentEmployee(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
        refreshGrid();
        signal.signal("EmployeeSelected", applicationUser);
    }

    @Override
    protected boolean isEditableEntity(Item entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        Item item = createEmptyItem();
        saveEntity(item);
        refreshGrid();
    }

    @Override
    protected void setValidationError(Item entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(String classname, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
        System.out.println("Class " + classname + " - column " + columName);
        System.out.println(e.getMessage());
    }

    @Override
    protected boolean validUpdate(Item entity, String colName, Object  newColValue) {
        return true;
    }

    @Override
    protected void saveEntity(Item entity) {
        ServiceAccessPoint.getServiceAccessPointInstance().getItemRepository().save(entity);
    }

    @Override
    protected List<Item> loadEntities() {
        if ((applicationUser != null) && (applicationUser.getId() != null)) {
            return ServiceAccessPoint.getServiceAccessPointInstance().getItemRepository().findByApplicationUserId(applicationUser.getId());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(Item entity) {
        ServiceAccessPoint.getServiceAccessPointInstance().getItemRepository().delete(entity);
    }

    @Override
    protected void selectEntity(Item entity) {

    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {

        switch(colName.toLowerCase()) {
            case "category" : { return (List<S>) new ArrayList<String>(List.of("Technical", "Quality", "Delivery", "Legal"));  }
            case "warehouse" : return (List<S>) ServiceAccessPoint.getServiceAccessPointInstance().getWarehouseRepository().findAll();
        }

        return new ArrayList<>();
    }

    @Override
    protected String getFixedCalculatedText(Item item, String colName) { return ""; }


}

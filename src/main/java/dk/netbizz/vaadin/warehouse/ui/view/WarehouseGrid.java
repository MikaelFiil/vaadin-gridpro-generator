package dk.netbizz.vaadin.warehouse.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.warehouse.domain.Warehouse;
import dk.netbizz.vaadin.service.ServicePoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseGrid extends GenericGridProEditView<Warehouse> {

    public WarehouseGrid() {
        super(Warehouse.class);
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

    private Warehouse createEmptyWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName("Enter a name ...");
        warehouse.setStreet("");
        warehouse.setCity("");
        warehouse.setSqrM2(0);
        return warehouse;
    }

    private Map<String, String> makeParams() {
        Map<String , String> params = new HashMap<>();
        return params;
    }


    public void refresh() {
        refreshGrid();
    }

    @Override
    protected boolean isEditableEntity(Warehouse entity) { return true; }

    @Override
    protected boolean canAddEntity() { return true; }

    @Override
    protected boolean canDeleteEntities() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        Warehouse item = createEmptyWarehouse();
        saveEntity(item);
        refreshGrid();
    }

    @Override
    protected void setValidationError(Warehouse entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(String classname, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected boolean validUpdate(Warehouse entity, String colName, Object  newColValue) {
        return true;
    }

    @Override
    protected void saveEntity(Warehouse entity) {
        ServicePoint.servicePointInstance().getWarehouseRepository().save( entity);
    }

    @Override
    protected void loadEntities() {
        genericGrid.setItems(ServicePoint.servicePointInstance().getWarehouseRepository().findAll());
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
    }

    @Override
    protected void deleteEntity(Warehouse entity) {
        ServicePoint.servicePointInstance().getWarehouseRepository().delete(entity);
    }

    @Override
    protected void selectEntity(Warehouse entity) {

    }

    @Override
    protected <S>List<S> getItemsForSelect(String colName) {
        List<S> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String getFixedCalculatedText(Warehouse item, String colName) { return ""; }


}

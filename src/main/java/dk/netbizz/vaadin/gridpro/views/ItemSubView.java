package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.entity.Item;
import dk.netbizz.vaadin.gridpro.service.ItemDataService;
import dk.netbizz.vaadin.gridpro.utils.StandardNotifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSubView extends GenericGridProEditView<Item> {

    private final ItemDataService dataService;

    public ItemSubView(Map<String , String> params, ItemDataService dataService) {
        super(Item.class);
        this.dataService = dataService;                                          // setupGrid needs DataService
        setSizeFull();

        // Create parameters specifically for the arrays
        // The point is that it is dynamic as to the count and headers of the array columns
        genericGrid.setWidth("100%");
        genericGrid.setHeight("500px");
        genericGrid.setEmptyStateText("No items found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addClassName("vaadin-grid-generator");

        // with the dataService set we can now continue the generic setup
        setupGrid(params);
        refreshGrid();
    }

    public void resetGrid(Map<String , String> params) {
        // genericGrid.setItems(new ArrayList<>());
        setupGrid(params);
    }


    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        Item item = new Item();
        item.setCategory(getItemsForSelect("category").getFirst());
        saveEntity(item);
        refreshGrid();
    }

    @Override
    protected void setValidationError(Item entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(Item entity, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected void saveEntity(Item entity) {
        dataService.save(entity);
    }

    @Override
    protected List<Item> loadEntities() {
        return dataService.getItemList();
    }

    @Override
    protected void deleteEntity(Item item) {
        dataService.delete(item);
    }

    @Override
    protected void selectEntity(Item entity) {
        System.out.println("Item selected: " + entity);
    }

    @Override
    public List<String> getItemsForSelect(String colName) { return dataService.getItemsForSelect(colName); }

}

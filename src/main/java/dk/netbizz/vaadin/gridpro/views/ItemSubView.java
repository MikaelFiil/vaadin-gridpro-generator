package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.entity.Item;
import dk.netbizz.vaadin.gridpro.service.ItemDataService;
import dk.netbizz.vaadin.gridpro.utils.StandardNotifications;

import java.util.List;
import java.util.Map;

public class ItemSubView extends GenericGridProEditView<Item> {

    private final ItemDataService dataService;

    public ItemSubView(Map<String , String> params, ItemDataService dataService) {
        super(Item.class);
        this.dataService = dataService;                                          // setupGrid needs DataService
        setSizeFull();

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

    @Override
    protected boolean isEditableEntity(Item entity) {
        return true;
    }

    @Override
    protected boolean canAddEntity() { return true; }

    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        Item item = new Item();
        item.setCategory((String)getItemsForSelect("category").getFirst());
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
    }

    @Override
    protected boolean validUpdate(Item entity, String colName, Object  newColValue) { return true; }

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
    protected <S>List<S> getItemsForSelect(String colName) { return dataService.getItemsForSelect(colName); }

    @Override
    protected String getFixedCalculatedText(Item item, String colName) {
        return "";
    }

    @Override
    protected String getCssClassName(String aCssClass) { return "NA"; }
}

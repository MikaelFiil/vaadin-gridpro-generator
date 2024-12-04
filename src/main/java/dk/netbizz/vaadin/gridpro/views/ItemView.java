package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.gridpro.entity.Item;
import dk.netbizz.vaadin.gridpro.service.ItemDataService;
import dk.netbizz.vaadin.gridpro.utils.StandardNotifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("GridPro Inline")
@Menu(order = 0, icon = "line-awesome/svg/home-solid.svg")
@Route(value = "", layout = MainLayout.class)
public class ItemView extends GenericGridProEditView<Item> {

    private final ItemDataService dataService;

    public ItemView(ItemDataService dataService) {
        super(Item.class);
        this.dataService = dataService;                                          // setupGrid needs DataService

        // Create parameters specifically for the arrays
        // The point is that it is dynamic as to the count and headers of the array columns
        Map<String , String> params = new HashMap<>();
        params.put("yearlyAmount.arrayEndIdx", "2");            // Indexes are zero based
        params.put("yearlyAmount.header0", "Year 2024");
        params.put("yearlyAmount.header1", "Year 2025");
        params.put("yearlyAmount.header2", "Year 2026");

        params.put("impactAmount.arrayEndIdx", "1");
        params.put("impactAmount.header0", "Impact 1");
        params.put("impactAmount.header1", "Impact 2");

        params.put("likelihood.arrayEndIdx", "1");
        params.put("likelihood.header0", "likelihood 1");
        params.put("likelihood.header1", "likelihood 2");

        params.put("calculatedImpact.arrayEndIdx", "1");

        genericGrid.setWidth("100%");
        genericGrid.setHeight("500px");
        genericGrid.setEmptyStateText("No items found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        genericGrid.addClassName("vaadin-grid-generator");

        // with the dataService set we can now continue the generic setup
        setupGrid(params);
        setupGridEventHandlers();
        refreshGrid();
    }


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
    public <S>List<S> getItemsForSelect(String colName) { return dataService.getItemsForSelect(colName); }

}

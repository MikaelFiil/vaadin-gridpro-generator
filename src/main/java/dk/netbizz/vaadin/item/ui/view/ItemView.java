package dk.netbizz.vaadin.item.ui.view;

import com.flowingcode.vaadin.addons.gridhelpers.GridHelper;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.MainLayout;
import dk.netbizz.vaadin.item.service.ItemDataService;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;
import dk.netbizz.vaadin.item.domain.Item;
import lombok.experimental.ExtensionMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("GridPro Inline")
@Menu(order = 1, icon = "line-awesome/svg/home-solid.svg")
@Route(value = "", layout = MainLayout.class)
@ExtensionMethod(GridHelper.class)
public class ItemView extends GenericGridProEditView<Item> {

    private final ItemDataService dataService;

    public ItemView(ItemDataService dataService) {
        super(Item.class);
        this.dataService = dataService;                                          // setupGrid needs DataService

        // Create parameters specifically for the arrays
        // The point is that it is dynamic as to the count and headers of the array columns
        Map<String , String> params = new HashMap<>();
        // params.put("price.hidden", "");                         // Don't show price column in this view, only the key is used

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
        genericGrid.setMaxHeight("500px");
        genericGrid.setEmptyStateText("No items found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);       //, GridVariant.LUMO_NO_BORDER);
        // genericGrid.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_READ_ONLY_CELLS);
        genericGrid.addClassName("vaadin-grid-generator");
        // genericGrid.setSelectionFilter(Item::getActive);

        // with the dataService set we can now continue the generic setup
        setupGrid(params);
        setupGridEventHandlers();
        refreshGrid();
    }

    @Override
    protected boolean isEditableEntity(Item entity) {
        return entity.getActive();
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
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
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

    @Override
    protected String getFixedCalculatedText(Item item, String colName) {
        return "";
    }


}

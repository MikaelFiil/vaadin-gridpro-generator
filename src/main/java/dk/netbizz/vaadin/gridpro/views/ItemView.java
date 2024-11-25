package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.gridpro.entity.Item;
import dk.netbizz.vaadin.gridpro.entity.base.GenericGridProEditView;
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
        // The point is that it is dynamic as to the count and headers of the arrays columns
        Map<String , String> params = new HashMap<String, String>();
        params.put("yearlyAmount.arrayEndIdx", "3");            // Indexes are zero based
        params.put("yearlyAmount.header0", "Year 2024");
        params.put("yearlyAmount.header1", "Year 2025");
        params.put("yearlyAmount.header2", "Year 2026");
        params.put("yearlyAmount.header3", "Year 2027");

        params.put("siloTon.arrayEndIdx", "2");
        params.put("siloTon.header0", "Silo 1");
        params.put("siloTon.header1", "Silo 2");
        params.put("siloTon.header2", "Silo 3");

        genericGrid.setWidth("100%");;
        genericGrid.setHeight("500px");
        genericGrid.setEmptyStateText("No items found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);


        // with the dataService set we can now continue the generic setup
        setupGrid(params);
        setupGridEventHandlers();
        refreshGrid();

        // Add Button for adding new person
        Div addButton = new Div();
        addButton.setClassName("circle-button-container");
        Avatar addAvatar = new Avatar("+");
        addAvatar.addClassName("circle-button");
        addButton.add(addAvatar);
        addButton.addClickListener(event -> addNew());
        gridContainer.addComponentAsFirst(addButton);
    }


    // Constructing a new entity is domain specific
    private void addNew() {
        // Create empty instance
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
        // You may save the current list here if need be
        return dataService.findAll();
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
    public List<String> getItemsForSelect(String colName) { return dataService.getItemsForSelect(colName); };

}

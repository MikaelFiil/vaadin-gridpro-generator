package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.entity.Cost;
import dk.netbizz.vaadin.gridpro.service.CostDataService;
import dk.netbizz.vaadin.gridpro.utils.StandardNotifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CostView extends GenericGridProEditView<Cost> {

    private final CostDataService dataService;

    public CostView(CostDataService dataService) {
        super(Cost.class);
        this.dataService = dataService;                                          // setupGrid needs DataService

        // Create parameters specifically for the arrays
        // The point is that it is dynamic as to the count and headers of the array columns
        Map<String , String> params = new HashMap<String, String>();

        genericGrid.setEmptyStateText("No entities found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES); //, GridVariant.LUMO_NO_BORDER);

        // with the dataService set we can now continue the generic setup
        setupGrid(params);
        setupGridEventHandlers();
        refreshGrid();
    }


    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        Cost entity = new Cost();
        saveEntity(entity);
        refreshGrid();
    }

    @Override
    protected void setValidationError(Cost entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(Cost entity, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected void saveEntity(Cost entity) {
        dataService.save(entity);
    }

    @Override
    protected List<Cost> loadEntities() {
        return dataService.getEntityList();
    }

    @Override
    protected void deleteEntity(Cost entity) {
        dataService.delete(entity);
    }

    @Override
    protected void selectEntity(Cost entity) {
        System.out.println("Entity selected: " + entity);
    }

    @Override
    public List<String> getItemsForSelect(String colName) {return dataService.getItemsForSelect(colName); };

}

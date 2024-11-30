package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.entity.Resource;
import dk.netbizz.vaadin.gridpro.service.ResourceDataService;
import dk.netbizz.vaadin.gridpro.utils.StandardNotifications;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResourceView extends GenericGridProEditView<Resource> {


    private List<Resource> entityList = new ArrayList<>();     // DB
    private final ResourceDataService dataService;

    public ResourceView(ResourceDataService dataService) {
        super(Resource.class);
        this.dataService = dataService;

        // Create parameters specifically for the arrays
        // The point is that it is dynamic as to the count and headers of the array columns
        Map<String , String> params = new HashMap<String, String>();

        genericGrid.setEmptyStateText("No entities found.");
        genericGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);

        // with the dataService set we can now continue the generic setup
        setupGrid(params);
        setupGridEventHandlers();
        refreshGrid();
    }


    // Constructing a new entity is domain specific
    @Override
    protected void addNew() {
        // Create empty instance and add to the current list here if need be
        Resource entity = new Resource();
        saveEntity(entity);
        refreshGrid();
    }

    @Override
    protected void setValidationError(Resource entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(Resource entity, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected void saveEntity(Resource entity) {
        dataService.save(entity, entityList);
    }

    @Override
    protected List<Resource> loadEntities() {
        return entityList;
    }

    @Override
    protected void deleteEntity(Resource entity) {
        dataService.delete(entity, entityList);
    }

    @Override
    protected void selectEntity(Resource entity) {
        System.out.println("Entity selected: " + entity);
    }

    @Override
    public List<String> getItemsForSelect(String colName) {return dataService.getItemsForSelect(colName); };

}

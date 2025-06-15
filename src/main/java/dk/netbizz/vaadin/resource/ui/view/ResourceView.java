package dk.netbizz.vaadin.resource.ui.view;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.resource.domain.Resource;
import dk.netbizz.vaadin.resource.service.ResourceDataService;
import dk.netbizz.vaadin.gridpro.utils.components.StandardNotifications;
import dk.netbizz.vaadin.gridpro.utils.gridprogenerator.GenericGridProEditView;

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

    public void setHeightClassName(String height, String className) {
        genericGrid.addClassName(className);
        genericGrid.setHeight(height);
    }

    @Override
    protected boolean isEditableEntity(Resource entity) {
        return true;
    }

    @Override
    protected boolean canAddEntity() { return true; }

    @Override
    protected boolean canDeleteEntities() { return true; }

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
    protected void setSystemError(String classname, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected boolean validUpdate(Resource entity, String colName, Object  newColValue) { return true; }

    @Override
    protected void saveEntity(Resource entity) {
        dataService.save(entity, entityList);
    }

    @Override
    protected List<Resource> loadEntities() {
        return entityList;
    }

    @Override
    protected void clearEntities() {
        genericGrid.setItems(new ArrayList<>());
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

    @Override
    protected String getFixedCalculatedText(Resource item, String colName) {
        return "";
    }

/*
    @Override
    protected String getCssClassName(String aCssClass) {
        switch(aCssClass.toLowerCase()) {
            case "viavea-select-class":
                return "viavea-subgrid-select";
            default: return "NA";
        }
    }
 */

}

package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.grid.GridVariant;
import dk.netbizz.vaadin.gridpro.entity.Template;
import dk.netbizz.vaadin.gridpro.service.TemplateDataService;
import dk.netbizz.vaadin.gridpro.utils.StandardNotifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemplateView extends GenericGridProEditView<Template> {

    private List<Template> entityList = new ArrayList<>();     // State should go here
    private final TemplateDataService dataService;


    public TemplateView(TemplateDataService dataService) {
        super(Template.class);
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
        Template entity = new Template();
        saveEntity(entity);
        refreshGrid();
    }

    @Override
    protected void setValidationError(Template entity, String columName, String msg) {
        StandardNotifications.showTempWarningNotification(msg);
    }

    @Override
    protected void setSystemError(Template entity, String columName, Exception e) {
        StandardNotifications.showTempSystemError();
    }

    @Override
    protected void saveEntity(Template entity) {
        dataService.save(entity, entityList);
    }

    @Override
    protected List<Template> loadEntities() {
        entityList =  dataService.findAll();
        return entityList;
    }

    @Override
    protected void deleteEntity(Template entity) {
        dataService.delete(entity, entityList);
    }

    @Override
    protected void selectEntity(Template entity) {
        System.out.println("Entity selected: " + entity);
    }

    @Override
    public List<String> getItemsForSelect(String colName) {
        return new ArrayList<>();
    };

}

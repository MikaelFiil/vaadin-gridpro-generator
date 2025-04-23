package dk.netbizz.vaadin.gridpro.utils.components;

import com.vaadin.flow.component.grid.AbstractGridSingleSelectionModel;
import com.vaadin.flow.component.grid.Grid;

public class GridUtils {


    public static void setDeselectAllowed(Grid<?> grid, boolean deselectAllowed) {
        if (grid.getSelectionModel() != null) {
            ((AbstractGridSingleSelectionModel<?>) grid.getSelectionModel())
                    .setDeselectAllowed(deselectAllowed);
        }
    }

}
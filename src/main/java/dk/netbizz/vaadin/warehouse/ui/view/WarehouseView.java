package dk.netbizz.vaadin.warehouse.ui.view;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("warehouse")
@PageTitle("Warehouse list")
@Menu(order = 4, icon = "vaadin:clipboard-check", title = "Warehouses")
public class WarehouseView extends Main {

    VerticalLayout verticalLayout = new VerticalLayout();
    WarehouseGrid warehouseGrid = new WarehouseGrid();

    public WarehouseView() {
        setSizeFull();
        buildUI();
        buildUX();
        verticalLayout.setWidthFull();
        add(verticalLayout);
        warehouseGrid.refresh();
    }

    private void buildUI() {
        verticalLayout.add(warehouseGrid);
    }


    private void buildUX()  {

    }

}
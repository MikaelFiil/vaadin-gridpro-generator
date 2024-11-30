package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.gridpro.service.ItemDataService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@PageTitle("GridPro Generator Test")
@Menu(order = 1, icon = "line-awesome/svg/home-solid.svg")
@Route(value = "gridpro-gen-test", layout = MainLayout.class)
public class GridProGeneratorTest extends VerticalLayout {

    ItemDataService dataService;
    private ItemSubView itemSubView;
    Select<Integer> startYear = new Select<>();
    Select<Integer> endYear = new Select<>();
    List<Integer> years = Arrays.asList(2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033);


    public GridProGeneratorTest( ItemDataService dataService) {
        this.dataService = dataService;
        setSizeFull();

        makeMainView();
        itemSubView = new ItemSubView(makeParams(), dataService);
        HorizontalLayout selectRow = new HorizontalLayout(startYear, endYear);
        add(selectRow);
        add(itemSubView);

        startYear.addValueChangeListener(evt -> {
            itemSubView.resetGrid(makeParams());
        });

        endYear.addValueChangeListener(evt -> {
            itemSubView.resetGrid(makeParams());
        });

    }

    private void makeMainView() {
        startYear.setItems(years);
        endYear.setItems(years);
        startYear.setLabel("Start year");
        startYear.setValue(years.getFirst());
        endYear.setLabel("End year");
        endYear.setValue(years.getLast());
    }

    private Map<String , String> makeParams() {
        Map<String , String> params = new HashMap<>();

        int yearSpan = endYear.getValue() - startYear.getValue();
        params.put("yearlyAmount.arrayEndIdx", String.valueOf(yearSpan));            // Indexes are zero based
        for (int i = 0; i <= yearSpan; i++) {
            params.put("yearlyAmount.header" + i, "Year " + String.valueOf(startYear.getValue() + i));
        }

        params.put("impactAmount.arrayEndIdx", "0");
        params.put("impactAmount.header0", "Impact 1");
        params.put("likelihood.arrayEndIdx", "0");
        params.put("likelihood.header0", "likelihood 1");
        params.put("calculatedImpact.arrayEndIdx", "0");

        return params;
    }

}


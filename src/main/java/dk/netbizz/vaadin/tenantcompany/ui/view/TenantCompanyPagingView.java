package dk.netbizz.vaadin.tenantcompany.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.service.ServicePoint;
import dk.netbizz.vaadin.tenantcompany.domain.TenantCompany;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/*
@PageTitle("Paging, Sorting and Filtering")
@Menu(order = 15, icon = "vaadin:clipboard-check", title = "Paging and Sorting")
@Route("tenant-companies")
 */
public class TenantCompanyPagingView extends VerticalLayout {

    private Grid<TenantCompany> aGrid;
    DataProvider<TenantCompany, String> dataProvider;
    private TextField tfCompanyNameFilter;
    private TextField tfAddressStreetFilter;
    private TextField tfAddressZipCityFilter;

    /**
     * Constructor for the Vaadin View. Spring will automatically inject the TenantCompanyRepository.
     */
    @Autowired
    public TenantCompanyPagingView() {
        dataProvider = DataProvider.fromFilteringCallbacks(
                query -> ServicePoint.servicePointInstance().getTenantCompanyService().findFromQuery(createWhere(), createOrderBy(query.getSortOrders()), query.getLimit(), query.getOffset()).stream(),
                query -> ServicePoint.servicePointInstance().getTenantCompanyService().countFromQueryFilter(createWhere())
        );

        initGrid();
        configureGridColumns();
        HeaderRow headerRow = aGrid.appendHeaderRow();
        tfCompanyNameFilter = createSearchField("name",headerRow.getCell(aGrid.getColumnByKey("companyName")));
        tfAddressStreetFilter = createSearchField("Street",headerRow.getCell(aGrid.getColumnByKey("addressStreet")));
        tfAddressZipCityFilter = createSearchField("City",headerRow.getCell(aGrid.getColumnByKey("addressZipCity")));

        add(aGrid);
        setSizeFull();
    }

    private void initGrid() {
        aGrid = new Grid<>(TenantCompany.class, false); // Pass 'false' to disable auto-creation of columns initially
        aGrid.setAllRowsVisible(true);
        aGrid.setSizeFull();
        aGrid.setDataProvider(dataProvider);
    }

    private void configureGridColumns() {
        // Add columns. The arguments are the property names in the TenantCompany class.
        aGrid.addColumn("id").setHeader("ID").setSortProperty("id");
        aGrid.addColumn("companyName").setHeader("Company Name").setSortProperty("company_name");
        aGrid.addColumn("addressStreet").setHeader("Street Address").setSortProperty("address_street");
        aGrid.addColumn("addressZipCity").setHeader("Zip/City").setSortProperty("address_zip_city");
    }

    // Filters using components in grid header
    private TextField createSearchField(String labelText, HeaderRow.HeaderCell cell) {
        TextField textField = new TextField();
        textField.setPlaceholder("Filter by " + labelText + "...");
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.setClearButtonVisible(true);
        textField.setWidthFull();
        textField.addValueChangeListener(evt -> dataProvider.refreshAll());
        cell.setComponent(textField);
        return textField;
    }

    private String convertOrderString(String order) {
        if (order.toLowerCase().contains("asc"))
            return "asc";
        else
            return "desc";
    }

    private String createOrderBy(List<QuerySortOrder> sortOrders) {
        StringBuilder orderBy = new StringBuilder();
        AtomicReference<Boolean> first = new AtomicReference<>();
        first.set(true);

        sortOrders.forEach(order -> {
            if (first.get()) {
                orderBy.append(" order by ");
            }
            else {
                orderBy.append(",");
            }
            orderBy.append(order.getSorted() + " " + convertOrderString(order.getDirection().toString()) + " ");
            first.set(false);
        });
        return orderBy.toString();
    }


    private String createWhere() {
        StringBuilder where = new StringBuilder();
        where.append(tfCompanyNameFilter.getValue().isEmpty() ? "" : (" " + "lower(company_name) like '%" + tfCompanyNameFilter.getValue().toLowerCase() + "%'"));

        if (!tfAddressStreetFilter.getValue().isEmpty() && !where.isEmpty()) {
                where.append(" and ");
        }
        where.append(tfAddressStreetFilter.getValue().isEmpty() ? "" : (" " + "lower(address_street) like '%" + tfAddressStreetFilter.getValue().toLowerCase() + "%'"));

        if (!tfAddressZipCityFilter.getValue().isEmpty() && !where.isEmpty()) {
            where.append(" and ");
        }
        where.append(tfAddressZipCityFilter.getValue().isEmpty() ? "" : (" " + "lower(address_zip_city) like '%" + tfAddressZipCityFilter.getValue().toLowerCase() + "%'"));

        if (where.isEmpty()) {
            return "";
        }
        return " where " + where.toString();
    }

}


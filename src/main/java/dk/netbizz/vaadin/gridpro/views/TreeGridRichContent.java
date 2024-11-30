
package dk.netbizz.vaadin.gridpro.views;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dk.netbizz.vaadin.gridpro.entity.Person;
import dk.netbizz.vaadin.gridpro.service.CostDataService;
import dk.netbizz.vaadin.gridpro.service.PersonDataService;
import dk.netbizz.vaadin.gridpro.service.ResourceDataService;

import java.util.List;


@PageTitle("Tree Grid experimental")
@Menu(order = 2, icon = "line-awesome/svg/home-solid.svg")
@Route(value = "tree-grid-rich-content", layout = MainLayout.class)
public class TreeGridRichContent extends VerticalLayout {

    private final ResourceDataService resourceDataService;
    private final CostDataService costDataService;
    private final PersonDataService personDataService;
    private List<Person> managers;


    public TreeGridRichContent(PersonDataService personDataService, ResourceDataService resourceDataService,  CostDataService costDataService) {
        this.personDataService = personDataService;
        this.resourceDataService = resourceDataService;
        this.costDataService = costDataService;
        managers = personDataService.findAll();
        setSizeFull();

        TreeGrid<Person> treeGrid = new TreeGrid<>();
        treeGrid.setSizeFull();
        treeGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        treeGrid.addClassName("vaadin-tree-grid");
        treeGrid.setItems(managers, this::getStaff);

        // tag::snippet[]
        treeGrid.addComponentHierarchyColumn(person -> {
            Avatar avatar = new Avatar();
            avatar.setName(person.getFullName());
            avatar.setImage(person.getPictureUrl());

            Span fullName = new Span(person.getFullName());

            Span profession = new Span(person.getProfession());
            profession.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout column = new VerticalLayout(fullName, profession);
            column.getStyle().set("line-height", "var(--lumo-line-height-m)");
            column.setPadding(false);
            column.setSpacing(false);

            HorizontalLayout row = new HorizontalLayout(avatar, column);
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setSpacing(true);
            return row;
        })
        .setFlexGrow(1)
        .setHeader("Employee");

        treeGrid.addComponentColumn(person -> {
            Icon emailIcon = createIcon(VaadinIcon.ENVELOPE);
            Span email = new Span(person.getEmail());

            Anchor emailLink = new Anchor();
            emailLink.add(emailIcon, email);
            emailLink.setHref("mailto:" + person.getEmail());
            emailLink.getStyle().set("align-items", "center").set("display", "flex");

            Icon phoneIcon = createIcon(VaadinIcon.PHONE);
            Span phone = new Span(person.getAddress().getPhone());

            Anchor phoneLink = new Anchor();
            phoneLink.add(phoneIcon, phone);
            phoneLink.setHref("tel:" + person.getAddress().getPhone());
            phoneLink.getStyle().set("align-items", "center").set("display", "flex");

            VerticalLayout column = new VerticalLayout(emailLink, phoneLink);
            column.getStyle().set("font-size", "var(--lumo-font-size-s)")
                    .set("line-height", "var(--lumo-line-height-m)");
            column.setPadding(false);
            column.setSpacing(false);
            return column;
        })
        .setFlexGrow(1)
        .setHeader("Contact");
        // end::snippet[]

        treeGrid.addComponentColumn(person -> {
/*
            Grid<Cost> costGrid = new Grid<Cost>();
            costGrid.removeAllColumns();
            costGrid.addClassName("vaadin-tree-subgrid");
            costGrid.setEmptyStateText("No items found.");
            costGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);

            costGrid.addColumn(Cost::getDescription)
                    .setHeader("Description") ;
            costGrid.addColumn(Cost::getYearlyCost)
                    .setHeader("Yearly cost");
            costGrid.setItems(costDataService.findAll(person.getId()));
            costGrid.setHeight("100px");
            return costGrid;
*/
            ResourceView resourceView = new ResourceView(resourceDataService);
            resourceView.genericGrid.setHeight("250px");
            resourceView.genericGrid.addClassName("vaadin-subgrid-generator");
            return resourceView;

        })
        .setFlexGrow(6)
        .setHeader("Bid Resources");


        add(treeGrid);
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("margin-inline-end", "var(--lumo-space-s)");
        icon.setSize("var(--lumo-icon-size-s)");
        return icon;
    }

    public List<Person> getStaff(Person manager) {
        return personDataService.findStaffById(manager.getId());
    }

}

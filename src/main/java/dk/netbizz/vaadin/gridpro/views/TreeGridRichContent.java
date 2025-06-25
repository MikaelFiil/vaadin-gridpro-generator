
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
import dk.netbizz.vaadin.MainLayout;
import dk.netbizz.vaadin.person.domain.Person;
import dk.netbizz.vaadin.person.service.PersonDataService;
import dk.netbizz.vaadin.resource.service.ResourceDataService;
import dk.netbizz.vaadin.resource.ui.view.ResourceView;

import java.util.List;

/*
@PageTitle("Tree Grid experimental")
@Menu(order = 3, icon = "line-awesome/svg/home-solid.svg")
@Route(value = "tree-grid-rich-content", layout = MainLayout.class)
 */
public class TreeGridRichContent extends VerticalLayout {

    private final ResourceDataService resourceDataService;
    private final PersonDataService personDataService;
    private List<Person> managers;


    public TreeGridRichContent(PersonDataService personDataService, ResourceDataService resourceDataService) {
        this.personDataService = personDataService;
        this.resourceDataService = resourceDataService;
        managers = personDataService.findAll();
        setSizeFull();

        TreeGrid<Person> treeGrid = new TreeGrid<>();
        treeGrid.setSizeFull();
        treeGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        treeGrid.addClassName("vaadin-tree-grid");
        treeGrid.addClassName("viavea-zero-margin-padding");
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
            column.addClassName("viavea-zero-margin-padding");
            column.setPadding(false);
            column.setSpacing(false);

            HorizontalLayout row = new HorizontalLayout(avatar, column);
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setSpacing(false);
            row.addClassName("viavea-zero-margin-padding");
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
            column.addClassName("viavea-zero-margin-padding");
            return column;
        })
        .setFlexGrow(1)
        .setHeader("Contact");
        // end::snippet[]

        treeGrid.addComponentColumn(person -> {
            ResourceView resourceView = new ResourceView(resourceDataService);
            resourceView.setHeightClassName("200px", "vaadin-subgrid-generator");
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

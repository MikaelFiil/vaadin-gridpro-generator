package dk.netbizz.vaadin.gridpro.utils.inputcreators;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;


public class FormlayoutCreator {

    public static FormLayout createForm(Component... fields) {
        final var formLayout = new FormLayout(fields);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("11rem", 2),
                new FormLayout.ResponsiveStep("22em", 3),
                new FormLayout.ResponsiveStep("33rem", 4)
        );
        formLayout.addClassName("viavea-form-layout");
        return formLayout;
    }
}

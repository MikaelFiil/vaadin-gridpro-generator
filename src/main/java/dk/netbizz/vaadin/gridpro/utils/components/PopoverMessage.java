package dk.netbizz.vaadin.gridpro.utils.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;

public class PopoverMessage {


    public static void addPopover(String text, Component target) {
        Popover popover = new Popover();
        popover.add(text);
        popover.addThemeVariants(PopoverVariant.ARROW);
        popover.setPosition(PopoverPosition.TOP);
        popover.setOpenOnClick(false);
        popover.setOpenOnHover(true);
        popover.setOpenOnFocus(false);
        popover.setTarget(target);
    }


}

package dk.netbizz.vaadin.gridpro.utils.inputcreators;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;

public class ComboboxCreator {


    public interface LabelGenerator {
        <T> ItemLabelGenerator<T> genLabel();
    }

    // public static ConfirmDialog confirm(String header, String text, ConfirmationDialog.ResetIndicator resetIndicatior) {


    public static <T> ComboBox<T> createStandardCombobox(T t, String title) {
        ComboBox<T> cb  = new ComboBox<T>(title);
        cb.setAllowCustomValue(false);
        cb.setWidth("20rem");
        cb.setMaxWidth("20rem");
        return cb;
    }

    public static <T> ComboBox<T> createShortCombobox(T t, String title) {
        ComboBox<T> cb  = new ComboBox<T>(title);
        cb.setAllowCustomValue(false);
        cb.setWidth("9rem");
        cb.setMaxWidth("9rem");
        return cb;
    }

/*
    ComboBox<TenantDepartment> departmentEditorComponent = ComboboxCreator.createStandardCombobox( new TenantDepartment() , null);
        departmentEditorComponent.setItems(teamsAndTasksView.getTenantDepartmentService().findAllByUser(myUser));
        departmentEditorComponent.setItemLabelGenerator(TenantDepartment::getDepartmentName);
*/


    public static <T> ComboBox<T> createStandardCombobox(T t, String title, LabelGenerator labelGen) {
        ComboBox<T> cb  = new ComboBox<T>(title);
        cb.setAllowCustomValue(false);
        cb.setWidth("20rem");
        cb.setMaxWidth("20rem");
        cb.setItemLabelGenerator(labelGen.genLabel());
        return cb;
    }

}

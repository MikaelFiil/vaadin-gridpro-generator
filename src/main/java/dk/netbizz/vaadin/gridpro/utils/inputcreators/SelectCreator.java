package dk.netbizz.vaadin.gridpro.utils.inputcreators;

import com.vaadin.flow.component.select.Select;


public class SelectCreator {

    public static <T> Select<T> createStandardSelect(T t, String title, String placeholder) {
        Select<T> cb  = new Select<T>();
        cb.setLabel(title);
        cb.setPlaceholder(placeholder);
        cb.setWidth("20rem");
        cb.setMaxWidth("20rem");
        return cb;
    }

    public static <T> Select<T> createShortSelect(T t, String title, String placeholder) {
        Select<T> cb  = new Select<T>();
        cb.setLabel(title);
        cb.setPlaceholder(placeholder);
        cb.setWidth("9rem");
        cb.setMaxWidth("9rem");
        return cb;
    }

}

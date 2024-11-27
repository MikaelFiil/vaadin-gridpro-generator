package dk.netbizz.vaadin.gridpro.entity.base;

import com.vaadin.flow.component.textfield.TextField;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GridEditColumn {
    String header() default "";
    int order() default 999;
    boolean sortable() default true;
    String format() default "";     // Is mandatory for dates
    java.lang.Class editorClass() default TextField.class;
    int fieldLength() default 50;
    double minValue() default 0;
    double maxValue() default 1999999999;
    int arrayEndIdx() default 0;         // Values from 0 to size of array
    int alternatingCol() default -1;         // If there are multiple array columns they may alternate in the grid
}
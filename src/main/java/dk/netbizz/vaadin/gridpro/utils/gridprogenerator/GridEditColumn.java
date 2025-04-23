package dk.netbizz.vaadin.gridpro.utils.gridprogenerator;

import com.vaadin.flow.component.grid.ColumnTextAlign;
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
    Class<?> editorClass() default TextField.class;
    String labelGenerator() default "";
    int fieldLength() default 50;
    int flexGrow() default 1;
    double minValue() default 0;
    double maxValue() default 1999999999;
    ColumnTextAlign textAlign() default ColumnTextAlign.START;
    int arrayEndIdx() default 0;         // Values from 0 to size of array
    boolean alternatingCol() default false;         // If there are multiple array columns they may alternate in the grid
}

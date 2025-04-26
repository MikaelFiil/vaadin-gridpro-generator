package dk.netbizz.vaadin.gridpro.utils.inputcreators;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.*;


@SuppressWarnings("unused")
public class InputFieldCreator {

    private static final String WIDE_FIELD = "42rem";
    private static final String MEDIUM_FIELD = "20rem";
    private static final String SHORT_FIELD = "4.5rem";
    private static final String STANDARD_FIELD = "9.5rem";

    private InputFieldCreator() {}

    private static TextField setTextFieldStd(TextField tf, int maxLength) {
        tf.setWidth(MEDIUM_FIELD);
        tf.setMaxWidth(MEDIUM_FIELD);
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createStandardTextField(String label, int maxLength, String content) {
        TextField tf = setTextFieldStd(new TextField(label), maxLength);
        tf.setValue(content);
        return tf;
    }

    public static TextField createStandardTextField(int maxLength) {
        return setTextFieldStd(new TextField(), maxLength);
    }

    public static TextField createFullWidthTextField(int maxLength) {
        TextField tf = new TextField();
        tf.setWidth("100%");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createLongTextField(String label, int maxLength, String content) {
        TextField tf = new TextField(label);
        tf.setWidth(WIDE_FIELD);
        tf.setMaxWidth(WIDE_FIELD);
        tf.setMaxLength(maxLength);
        tf.setValue(content);
        return tf;
    }

    public static TextField createLongTextField(String label, int maxLength) {
        TextField tf = new TextField(label);
        tf.setWidth(WIDE_FIELD);
        tf.setMaxWidth(WIDE_FIELD);
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createShortTextField(String label, int maxLength) {
        TextField tf = new TextField(label);
        tf.setWidth(STANDARD_FIELD);
        tf.setMaxWidth(STANDARD_FIELD);
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createShortTextField(int maxLength) {
        TextField tf = new TextField();
        tf.setWidth(STANDARD_FIELD);
        tf.setMaxWidth(STANDARD_FIELD);
        tf.setMaxLength(maxLength);
        return tf;
    }

    /**************************************************************************************
     *  I N T E G E R   F I E L D
     */

    public static IntegerField createStandardIntegerField(String label) {
        IntegerField tf;
        if (label != null) {
            tf = new IntegerField(label);
        } else {
            tf = new IntegerField();
        }
        tf.setWidth(STANDARD_FIELD);
        tf.setMaxWidth(STANDARD_FIELD);
        tf.setStepButtonsVisible(false);
        return tf;
    }

    public static IntegerField createStandardIntegerField(String label, Integer min, Integer max, Integer step) {
        IntegerField tf = createStandardIntegerField(label);
        if (min != null) { tf.setMin(min); tf.setValue(min); }
        if (max != null) { tf.setMax(max); }
        if (step != null) { tf.setStep(step); }
        return tf;
    }

    public static IntegerField createFullWidthIntegerField(String label, Integer min, Integer max, Integer step) {
        IntegerField tf;
        if (label != null) {
            tf = new IntegerField(label);
        } else {
            tf = new IntegerField();
        }
        tf.setWidth("100%");
        tf.setStepButtonsVisible(false);
        if (min != null) { tf.setMin(min); tf.setValue(min); }
        if (max != null) { tf.setMax(max); }
        if (step != null) { tf.setStep(step); }
        return tf;
    }


    public static IntegerField createShortIntegerField(String label) {
        IntegerField tf;
        if (label != null) {
            tf = new IntegerField(label);
        } else {
            tf = new IntegerField();
        }
        tf.setWidth(SHORT_FIELD);
        tf.setMaxWidth(SHORT_FIELD);
        tf.setStepButtonsVisible(false);
        return tf;
    }

    public static IntegerField createShortIntegerField(String label, Integer min, Integer max, Integer step) {
        IntegerField tf = createShortIntegerField(label);
        if (min != null) { tf.setMin(min); tf.setValue(min); }
        if (max != null) { tf.setMax(max); }
        if (step != null) { tf.setStep(step); }
        return tf;
    }

    public static IntegerField createShortIntegerField(String label, Long min, Long max, Integer step) {
        IntegerField tf = createShortIntegerField(label);
        if (min != null) { tf.setMin(min.intValue()); tf.setValue(min.intValue()); }
        if (max != null) { tf.setMax(max.intValue()); }
        if (step != null) { tf.setStep(step); }
        return tf;
    }


    /**************************************************************************************
     *  F L O A T   F I E L D
     */

    public static NumberField createShortFloatField(String label) {
        NumberField tf;
        if (label != null) {
            tf = new NumberField(label);
        } else {
            tf = new NumberField();
        }
        tf.setWidth(SHORT_FIELD);
        tf.setMaxWidth(SHORT_FIELD);
        return tf;
    }

    public static NumberField createShortFloatField(String label, Float min, Float max) {
        NumberField tf = createShortNumberField(label);

        if (min != null) { tf.setMin(min); tf.setValue(min.doubleValue()); }
        if (max != null) { tf.setMax(max); }
        return tf;
    }



    /**************************************************************************************
     *  N U M B E R   F I E L D
     */

    public static NumberField createShortNumberField(String label) {
        NumberField tf;
        if (label != null) {
            tf = new NumberField(label);
        } else {
            tf = new NumberField();
        }
        tf.setWidth(SHORT_FIELD);
        tf.setMaxWidth(SHORT_FIELD);
        return tf;
    }

    public static NumberField createShortNumberField(String label, Double min, Double max) {
        NumberField tf = createShortNumberField(label);

        if (min != null) { tf.setMin(min); tf.setValue(min); }
        if (max != null) { tf.setMax(max); }
        return tf;
    }




    /**************************************************************************************
     *  B I G D E C I M A L   F I E L D
     */
    public static BigDecimalField createBigDecimalField(String label) {
        BigDecimalField tf;
        if (label != null) {
            tf = new BigDecimalField(label);
        } else {
            tf = new BigDecimalField();
        }
        tf.setAutocorrect(true);
        return tf;
    }


    public static BigDecimalField createShortBigDecimalField(String label) {
        BigDecimalField tf = createBigDecimalField(label);
        tf.setWidth(SHORT_FIELD);
        tf.setMaxWidth(SHORT_FIELD);
        return tf;
    }

    public static BigDecimalField createStandardBigDecimalField(String label) {
        BigDecimalField tf = createBigDecimalField(label);
        tf.setWidth(STANDARD_FIELD);
        tf.setMaxWidth(STANDARD_FIELD);
        return tf;
    }

    /**************************************************************************************
     *  T E X T A R E A    F I E L D
     */

    public static TextArea createStandardTextArea(String label) {
        TextArea ta = new TextArea(label);
        ta.setSizeFull();
        ta.getStyle().set("resize", "both");
        ta.getStyle().set("overflow", "auto");
        return ta;
    }


    public static TextArea createStandardTextArea(String label, String content) {
        TextArea ta = new TextArea(label);
        ta.setSizeFull();
        ta.getStyle().set("resize", "both");
        ta.getStyle().set("overflow", "auto");
        ta.setValue(content);
        return ta;
    }

    public static TextArea createFullWidthTextArea(String label, int maxLength, String content) {
        TextArea ta = new TextArea(label);
        ta.setMaxLength(maxLength);
        ta.setSizeFull();
        ta.getStyle().set("resize", "both");
        ta.getStyle().set("overflow", "auto");
        ta.setValue(content);
        return ta;
    }


    public static Html createDivider() {
        return new Html("</hr class='viavea-hr'>");
    }

    /**************************************************************************************
     *  S E L E C T     F I E L D
     */

    public static <T> Select<T> createStandardSelect(T t, String title, String placeholder) {
        Select<T> cb  = new Select<>();
        cb.setLabel(title);
        cb.setPlaceholder(placeholder);
        cb.setWidth(STANDARD_FIELD);
        cb.setMaxWidth(STANDARD_FIELD);
        return cb;
    }

    public static <T> Select<T> createShortSelect(T t, String title, String placeholder) {
        Select<T> cb  = new Select<>();
        cb.setLabel(title);
        cb.setPlaceholder(placeholder);
        cb.setWidth(SHORT_FIELD);
        cb.setMaxWidth(SHORT_FIELD);
        return cb;
    }

    /**************************************************************************************
     *  C H E C K B O X    F I E L D
     */

    public static Checkbox createCheckbox(boolean initialValue, boolean readonly, String format) {
        Checkbox cb  = new Checkbox(initialValue);
        if (!format.isEmpty()) {
            cb.getElement().getThemeList().add(format);
        }
        cb.setReadOnly(readonly);
        return cb;
    }
}

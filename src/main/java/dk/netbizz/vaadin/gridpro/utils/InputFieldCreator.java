package dk.netbizz.vaadin.gridpro.utils;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.textfield.*;

public class InputFieldCreator {

    /**************************************************************************************
     *  T E X T   F I E L D
     */

    private static TextField setTextFieldStd(TextField tf, int maxLength) {
        tf.setWidth("20rem");
        tf.setMaxWidth("20rem");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createStandardTextField(int maxLength) {
        return setTextFieldStd(new TextField(), maxLength);
    }

    public static TextField createStandardTextField(String label, int maxLength) {
        return setTextFieldStd(new TextField(label), maxLength);
    }

    public static TextField createFullWidthTextField(int maxLength) {
        TextField tf = new TextField();
        tf.setWidth("100%");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createFullWidthTextField(String label, int maxLength) {
        TextField tf = new TextField(label);
        tf.setWidth("100%");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createLongTextField(int maxLength) {
        TextField tf = new TextField();
        tf.setWidth("42rem");
        tf.setMaxWidth("42rem");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createLongTextField(String label, int maxLength) {
        TextField tf = new TextField(label);
        tf.setWidth("42rem");
        tf.setMaxWidth("42rem");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createShortTextField(int maxLength) {
        TextField tf = new TextField();
        tf.setWidth("9rem");
        tf.setMaxWidth("9rem");
        tf.setMaxLength(maxLength);
        return tf;
    }

    public static TextField createShortTextField(String label, int maxLength) {
        TextField tf = new TextField(label);
        tf.setWidth("9rem");
        tf.setMaxWidth("9rem");
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
        tf.setWidth("9.5rem");
        tf.setMaxWidth("9.5rem");
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
        tf.setWidth("4.5rem");
        tf.setMaxWidth("4.5rem");
        tf.setStepButtonsVisible(false);
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
     *  N U M B E R   F I E L D
     */

    public static NumberField createShortNumberField(String label) {
        NumberField tf;
        if (label != null) {
            tf = new NumberField(label);
        } else {
            tf = new NumberField();
        }
        tf.setWidth("4.5rem");
        tf.setMaxWidth("4.5rem");
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
        tf.setWidth("4.5rem");
        tf.setMaxWidth("4.5rem");
        return tf;
    }

    public static BigDecimalField createStandardBigDecimalField(String label) {
        BigDecimalField tf = createBigDecimalField(label);
        tf.setWidth("9.5rem");
        tf.setMaxWidth("9.5rem");
        return tf;
    }


    /**************************************************************************************
     *  T E X T A R E A    F I E L D
     */


    public static TextArea createStandardTextArea(String label) {
        TextArea ta = new TextArea(label);
        ta.setMaxLength(250);
        ta.setWidth("42rem");
        ta.setMaxWidth("42rem");
        return ta;
    }

    public static TextArea createFlexTextArea(String label) {
        TextArea ta = new TextArea(label);
        ta.setMaxLength(250);
        ta.setWidth("100%");
        ta.setMaxWidth("42rem");
        return ta;
    }

    public static TextArea createFullWidthTextArea(int maxLength) {
        TextArea ta = new TextArea();
        ta.setMaxLength(maxLength);
        ta.setWidth("100%");
        return ta;
    }

    public static Html createDivider() {
        return new Html("<hr class='viavea-hr'></hr>");
    }

}

package dk.netbizz.vaadin.gridpro.utils.inputcreators;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

public class DateTimePickerCreator {

    static DatePicker.DatePickerI18n datePickerI18n = new DatePicker.DatePickerI18n().setFirstDayOfWeek(1).setDateFormat("yyyy-MM-dd");
    public static DateTimePicker createDateTimePicker(String title, boolean isRequired) {
        DateTimePicker dtp = new DateTimePicker(title);
        dtp.setDatePickerI18n(datePickerI18n);
        dtp.setWeekNumbersVisible(true);
        dtp.setRequiredIndicatorVisible(isRequired);
        dtp.setWidth("20rem");
        dtp.setMaxWidth("20rem");
        return dtp;
    }

    public static DatePicker createDatePicker(String title, boolean isRequired) {
        DatePicker.DatePickerI18n dp2 = new DatePicker.DatePickerI18n().setFirstDayOfWeek(1).setDateFormat("yyyy-MM-dd");
        DatePicker dp = new DatePicker(title);
        dp.setI18n(dp2);
        dp.setWeekNumbersVisible(true);
        dp.setRequiredIndicatorVisible(isRequired);
        dp.setWidth("8rem");
        dp.setMaxWidth("8rem");

        return dp;
    }

    public static DateTimePicker createDateTimePicker(String title, String format, boolean isRequired) {
        DatePicker.DatePickerI18n datePickerI18n = new DatePicker.DatePickerI18n().setFirstDayOfWeek(1).setDateFormat(format);
        DateTimePicker dtp = new DateTimePicker(title);
        dtp.setDatePickerI18n(datePickerI18n);
        dtp.setWeekNumbersVisible(true);
        dtp.setRequiredIndicatorVisible(isRequired);
        dtp.setWidth("20rem");
        dtp.setMaxWidth("20rem");
        return dtp;
    }

    public static DatePicker createDatePicker(String title, String format, boolean isRequired) {
        DatePicker.DatePickerI18n dp2 = new DatePicker.DatePickerI18n().setFirstDayOfWeek(1).setDateFormat(format);
        DatePicker dp = new DatePicker(title);
        dp.setI18n(dp2);
        dp.setWeekNumbersVisible(true);
        dp.setRequiredIndicatorVisible(isRequired);
        dp.setWidth("8rem");
        dp.setMaxWidth("8rem");

        return dp;
    }

}

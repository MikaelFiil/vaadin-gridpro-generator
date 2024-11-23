package dk.netbizz.vaadin.gridpro.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class StandardNotifications {

    static int notificationTimeout = 3000;

    static public void showDialogNotification(String msg, NotificationVariant aVariant) {
        Notification notification = new Notification();
        notification.addThemeVariants(aVariant);

        Div text = new Div(new Text(msg));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }


    private static String systemErrorMsg = "System error: Your data may NOT have been saved";
    // private static String validationErrorMSg = "Validation error, check inputs. Data was NOT saved";
    private static String dataSavedMsg = "Data was saved";


    public static void showTempErrorNotification(String msg) {
        Notification
                .show(msg, notificationTimeout, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static void showTempWarningNotification(String msg) {
        Notification
                .show(msg, notificationTimeout, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    public static  void showTempSuccessNotification(String msg) {
        Notification
                .show(msg, notificationTimeout, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static  void showTempDataSaved() {
        showTempSuccessNotification(dataSavedMsg);
    }

    public static  void showTempSystemError() {
        showTempErrorNotification(systemErrorMsg);
    }

    public static void showDialogWarning(String msg) {
        StandardNotifications.showDialogNotification(msg, NotificationVariant.LUMO_WARNING);
    }

    public static void showDialogSuccess(String msg) {
        StandardNotifications.showDialogNotification(msg, NotificationVariant.LUMO_SUCCESS);
    }

    public static void showDialogError(String msg) {
        StandardNotifications.showDialogNotification(msg, NotificationVariant.LUMO_ERROR);
    }


}

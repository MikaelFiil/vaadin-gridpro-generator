package dk.netbizz.vaadin.gridpro.utils.components;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ConfirmationDialog {

    public interface ResetIndicator {
        void reset();
    }

    public static ConfirmDialog confirm(String header, String text, ResetIndicator resetIndicatior) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(header);
        dialog.setText(text);
        dialog.setCancelable(true);
        dialog.setCancelText("Cancel");
        dialog.setConfirmText("OK");
        dialog.addCancelListener(event -> { resetIndicatior.reset(); dialog.close(); });
        dialog.open();
        return dialog;
    }

    public static ConfirmDialog confirm(String header, String text) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(header);
        dialog.setText(text);
        dialog.setCancelable(true);
        dialog.setCancelText("Cancel");
        dialog.setConfirmText("OK");
        dialog.addCancelListener(event -> dialog.close() );
        dialog.open();
        return dialog;
    }


}

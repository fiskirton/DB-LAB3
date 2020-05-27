package extra;

import gui.views.CustomAlert;
import javafx.scene.control.Alert;

public class Alerts {
    public static Alert getErrorAlert(String content) {
        return getAlert(Alert.AlertType.ERROR, "ERROR", content);
    }

    public static Alert getWarningAlert(String content) {
        return getAlert(Alert.AlertType.WARNING, "WARNING", content);
    }

    public static Alert getConfirmationAlert(String content) {
        return getAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", content);
    }

    public static Alert getInfoAlert(String content) {
        return getAlert(Alert.AlertType.INFORMATION, "INFO", content);
    }

    private static Alert getAlert(Alert.AlertType type, String title, String content) {
        return CustomAlert.Builder()
                .alertType(type)
                .title(title)
                .content(content)
                .icon()
                .build();
    }
}

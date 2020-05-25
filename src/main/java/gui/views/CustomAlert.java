package gui.views;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class CustomAlert {
	private static final String STYLESHEET = CustomAlert.class.getResource("alert.css").toExternalForm();
	
	public static class AlertBuilder {
		private AlertType alertType;
		private String title;
		private String content;
		private String header = null;
		private Label icon;
		
		private AlertBuilder() {}
		
		public AlertBuilder alertType(AlertType alertType) {
			this.alertType = alertType;
			return this;
		}
		
		public AlertBuilder title(String title) {
			this.title = title;
			return this;
		}
		
		public AlertBuilder content(String content) {
			this.content = content;
			return this;
		}
		
		public AlertBuilder header(String header) {
			this.header = header;
			return this;
		}
		
		public AlertBuilder icon() {
			switch (alertType) {
				case CONFIRMATION:
					icon = new Label("\uf059");
					icon.getStyleClass().add("custom-confirmation-graphic");
					break;
				case ERROR:
					icon = new Label("\uf057");
					icon.getStyleClass().add("custom-error-graphic");
					break;
				case INFORMATION:
					icon = new Label("\uf058");
					icon.getStyleClass().add("custom-information-graphic");
					break;
				case WARNING:
					icon = new Label("\uf06a");
					icon.getStyleClass().add("custom-warning-graphic");
					break;
				default:
					icon = null;
			}
			
			assert icon != null;
			icon.setStyle("-fx-font-family: 'FontAwesome'");
			icon.setId("icon");
			
			return this;
		}
		
		public Alert build() {
			Alert alert = new Alert(this.alertType);
			alert.getDialogPane().getStylesheets().add(STYLESHEET);
			alert.setTitle(this.title);
			alert.setHeaderText(this.header);
			alert.setContentText(this.content);
			alert.setGraphic(this.icon);
			return alert;
		}
		
	}
	
	private CustomAlert() {}
	
	public static AlertBuilder Builder() {
		return new AlertBuilder();
	}
}
